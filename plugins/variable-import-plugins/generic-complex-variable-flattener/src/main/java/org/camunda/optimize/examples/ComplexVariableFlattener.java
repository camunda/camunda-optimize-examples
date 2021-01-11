package org.camunda.optimize.examples;

import com.github.wnameless.json.flattener.FlattenMode;
import com.github.wnameless.json.flattener.JsonFlattener;
import com.github.wnameless.json.flattener.JsonifyArrayList;
import org.camunda.optimize.plugin.importing.variable.PluginVariableDto;
import org.camunda.optimize.plugin.importing.variable.VariableImportAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Based on the plugin written by Jan Huddel.
 * See <a href="https://github.com/janhuddel/optimize-plugin-variable-flattener">optimize-plugin-variable-flattener</a>.
 */
public class ComplexVariableFlattener implements VariableImportAdapter {

	private static final Logger logger = LoggerFactory.getLogger(ComplexVariableFlattener.class);

	// maximum length for variables of type string
	public static final int VALUE_MAX_LENGTH = 4000;

	// ISO-8601-formatted Date format used in the Engine and Optimize
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

	@Override
	public List<PluginVariableDto> adaptVariables(List<PluginVariableDto> variables) {
		List<PluginVariableDto> resultList = new ArrayList<>();
		for (PluginVariableDto pluginVariableDto : variables) {
			logger.debug("adapting variable {} (v{}) of process-instance {}...", //
					pluginVariableDto.getName(), //
					pluginVariableDto.getVersion(), //
					pluginVariableDto.getProcessInstanceId());
			if (pluginVariableDto.getType().equalsIgnoreCase("object")) {
				String serializationDataFormat = String.valueOf(pluginVariableDto.getValueInfo().get("serializationDataFormat"));
				if (serializationDataFormat.equals("application/json")) {
					this.flatJsonObject(pluginVariableDto, resultList);
				} else {
					logger.warn("complex variable '{}' won't be imported (unsupported serializationDataFormat: {})",
							pluginVariableDto.getName(), serializationDataFormat);
				}
			} else {
				resultList.add(pluginVariableDto);
			}
		}
		return resultList;
	}

	private void flatJsonObject(PluginVariableDto variable, List<PluginVariableDto> resultList) {
		if (variable.getValue() == null || variable.getValue().isEmpty()) {
			return;
		}

		try {
			new JsonFlattener(variable.getValue()) //
					.withFlattenMode(FlattenMode.KEEP_ARRAYS) //
					.flattenAsMap() //
					.entrySet() //
					.stream() //
					.map(e -> this.map(e.getKey(), e.getValue(), variable)) //
					.filter(Optional::isPresent) //
					.map(Optional::get) //
					.forEach(resultList::add);
		} catch (Exception exception) {
			logger.error("error while flattening variable '{}')", variable.getName(), exception);
		}
	}

	private Optional<PluginVariableDto> map(String name, Object value, PluginVariableDto origin) {
		// null-values are not supported
		if (value == null) {
			logger.info("variable-attribute '{}' of '{}' is null and won't be imported", name, origin.getName());
			return Optional.empty();
		}

		PluginVariableDto newVariable = new PluginVariableDto();

		// copy meta-info from origin
		newVariable.setEngineAlias(origin.getEngineAlias());
		newVariable.setProcessDefinitionId(origin.getProcessDefinitionId());
		newVariable.setProcessDefinitionKey(origin.getProcessDefinitionKey());
		newVariable.setProcessInstanceId(origin.getProcessInstanceId());
		newVariable.setVersion(origin.getVersion());

		// set name, type and value
		if (JsonFlattener.ROOT.equals(name)) {
			// the name "root" is used by the flattener if the JSON is a string or array (no object)
			newVariable.setName(origin.getName());
		} else {
			newVariable.setName(String.join(".", origin.getName(), name));
		}

		if (value instanceof JsonifyArrayList) {
			// for lists, only the number of contained elements is exported to reduce complexity
			newVariable.setName(String.join(".", newVariable.getName(), "_listsize"));
			newVariable.setType("Long");
			newVariable.setValue(String.valueOf(((JsonifyArrayList<?>) value).size()));
		} else if (value instanceof String) {
			String stringValue = String.valueOf(value);

			Optional<OffsetDateTime> optDate = this.parsePossibleDate(stringValue);
			if (optDate.isPresent()) {
				newVariable.setType("Date");
				newVariable.setValue(DATE_TIME_FORMATTER.format(optDate.get()));
			} else {
				newVariable.setType("String");
				if (stringValue.length() > VALUE_MAX_LENGTH) {
					logger.warn("value of variable {} will be truncated (original size: {})", newVariable.getName(), stringValue
							.length());
					stringValue = stringValue.substring(0, VALUE_MAX_LENGTH);
				}
				newVariable.setValue(stringValue);
			}
		} else if (value instanceof Boolean) {
			newVariable.setType("Boolean");
			newVariable.setValue(String.valueOf(value));
		} else if (value instanceof BigDecimal) {
			BigDecimal convertedValue = (BigDecimal) value;
			newVariable.setType("Double");
			newVariable.setValue(convertedValue.toString());
		} else {
			return notSupported(name, origin.getName(), value);
		}

		// the id needs to be unique for each new variable instance but consistent so that version updates get overridden
    newVariable.setId(origin.getId() + "_" + newVariable.getName());

		return Optional.of(newVariable);
	}

	private Optional<OffsetDateTime> parsePossibleDate(String dateAsString) {
		try {
			return Optional.of(OffsetDateTime.parse(dateAsString, DATE_TIME_FORMATTER));
		} catch (DateTimeParseException e) {
			return Optional.empty();
		}
	}

	private static Optional<PluginVariableDto> notSupported(String name, String originName, Object value) {
		logger.warn(
			"variable-attribute '{}' of '{}' with type {} and value '{}' is not supported yet",
			name, originName, value.getClass().getSimpleName(), value
		);
		return Optional.empty();
	}
}
