package org.camunda.optimize.examples;

import org.camunda.optimize.plugin.importing.variable.PluginVariableDto;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ComplexVariableFlattenerTests {

	@Test
	public void testStringWithDigitsOnly() {
		String json = "{\"zipcode\":\"24114\"}";

		ComplexVariableFlattener uut = new ComplexVariableFlattener();
		List<PluginVariableDto> variables = uut.adaptVariables(createVariable("var", json));

		assertAll("variables", //
				() -> assertEquals(1, variables.size()), //
				() -> assertEquals("24114", variables.get(0).getValue()), //
				() -> assertEquals("String", variables.get(0).getType()));

	}

	@Test
	public void testSimpleString() {
		String json = "\"Hello\"";

		ComplexVariableFlattener uut = new ComplexVariableFlattener();
		List<PluginVariableDto> variables = uut.adaptVariables(createVariable("var", json));

		assertAll("variables", //
				() -> assertEquals(1, variables.size()), //
				() -> assertEquals("var", variables.get(0).getName()), //
				() -> assertEquals("String", variables.get(0).getType()));
	}

	@Test
	public void testSimpleArray() {
		String json = "[0, 1, 2]";

		ComplexVariableFlattener uut = new ComplexVariableFlattener();
		List<PluginVariableDto> variables = uut.adaptVariables(createVariable("var", json));

		assertAll("variables", //
				() -> assertEquals(1, variables.size()), //
				() -> assertEquals("var._listsize", variables.get(0).getName()), //
				() -> assertEquals("Long", variables.get(0).getType()), //
				() -> assertEquals(3L, Long.parseLong(variables.get(0).getValue())));
	}

	@Test
	public void testWithISO8601Timestamp() {
		String json = "{\"name\":\"Doe\",\"vorname\":\"John\",\"geburtsdatum\":\"1977-06-02T00:00:00.000+01:00\"}";

		ComplexVariableFlattener uut = new ComplexVariableFlattener();
		List<PluginVariableDto> variables = uut.adaptVariables(createVariable("var", json));

		assertAll("variables", //
				() -> assertEquals(3, variables.size()), //
				() -> assertEquals("var.name", variables.get(0).getName()), //
				() -> assertEquals("Date", variables.get(2).getType()));
	}

	@Test
	public void testIgnoreNull() {
		String json = "{\"geschaftsfeldKey\":\"000\",\"kennzeichenAuffaelligkeitenKey\":null}";

		ComplexVariableFlattener uut = new ComplexVariableFlattener();
		List<PluginVariableDto> variables = uut.adaptVariables(createVariable("var", json));

		assertEquals(1, variables.size());
	}

	@Test
	public void testBoolean() {
		String json = "{\"isProduction\":true}";

		ComplexVariableFlattener uut = new ComplexVariableFlattener();
		List<PluginVariableDto> variables = uut.adaptVariables(createVariable("var", json));

		assertAll("variables", //
				() -> assertEquals(1, variables.size()), //
				() -> assertEquals("Boolean", variables.get(0).getType()));
	}

	@Test
	public void testLong() {
		String json = "{\"anzahl\":1000}";

		ComplexVariableFlattener uut = new ComplexVariableFlattener();
		List<PluginVariableDto> variables = uut.adaptVariables(createVariable("var", json));

		assertAll("variables", //
				() -> assertEquals("Double", variables.get(0).getType()), //
				() -> assertEquals(1000.0, Double.parseDouble(variables.get(0).getValue())));
	}

	@Test
	public void testDecimal() {
		String json = "{\"average\":3.1415}";

		ComplexVariableFlattener uut = new ComplexVariableFlattener();
		List<PluginVariableDto> variables = uut.adaptVariables(createVariable("var", json));

		assertAll("variables", //
				() -> assertEquals("Double", variables.get(0).getType()), //
				() -> assertEquals(3.1415, Double.parseDouble(variables.get(0).getValue())));
	}

	@Test
	public void testBigLong() {
		String json = "{\"count\":300000000}";

		ComplexVariableFlattener uut = new ComplexVariableFlattener();
		List<PluginVariableDto> variables = uut.adaptVariables(createVariable("var", json));

		assertAll("variables", //
				() -> assertEquals("Double", variables.get(0).getType()), //
				() -> assertEquals(30_0000_000, Double.parseDouble(variables.get(0).getValue())));
	}

	@Test
	public void testLongString() {
		String json = "{\"very-long-string\":\"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\"}";

		ComplexVariableFlattener uut = new ComplexVariableFlattener();
		List<PluginVariableDto> variables = uut.adaptVariables(createVariable("var", json));

		assertEquals(4000, variables.get(0).getValue().length());
	}

	@Test
	public void testCollection() {
		String json = "{\"list\": [1,2,3,4]}";

		ComplexVariableFlattener uut = new ComplexVariableFlattener();
		List<PluginVariableDto> variables = uut.adaptVariables(createVariable("var", json));

		assertAll("variables", //
				() -> assertEquals(1, variables.size()), //
				() -> assertEquals(4, Long.parseLong(variables.get(0).getValue())), //
				() -> assertEquals("var.list._listsize", variables.get(0).getName()), //
				() -> assertEquals("Long", variables.get(0).getType()));

	}

	@Test
	public void testEqualId() {
		String json = "{\"foo\":\"bar\"}";

		ComplexVariableFlattener uut = new ComplexVariableFlattener();
		List<PluginVariableDto> variables1 = uut.adaptVariables(createVariable("var", json));
		List<PluginVariableDto> variables2 = uut.adaptVariables(createVariable("var", json));
		assertEquals(variables1.get(0).getId(), variables2.get(0).getId());
	}

	private static List<PluginVariableDto> createVariable(String name, String json) {
		PluginVariableDto pluginVariableDto = new PluginVariableDto();
		pluginVariableDto.setType("Object");
		pluginVariableDto.setValueInfo(Collections.singletonMap("serializationDataFormat", "application/json"));
		pluginVariableDto.setName(name);
		pluginVariableDto.setValue(json);
		pluginVariableDto.setVersion(1L);
		pluginVariableDto.setId("b71a3a4e-77fa-11ea-8520-005056a016ea");
		pluginVariableDto.setProcessInstanceId("b71a133b-77fa-11ea-8520-005056a016ea");

		return Collections.singletonList(pluginVariableDto);
	}

	@SuppressWarnings("unused")
	private static void dumpResult(List<PluginVariableDto> list) {
		list.stream() //
				.map(v -> v.getName() + ": " + v.getValue() + " (" + v.getType() + ")") //
				.forEach(System.out::println);
	}
}
