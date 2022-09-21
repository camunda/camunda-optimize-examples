# Optimize RPA Monitoring Sample - UiPath

Optimize can be used for monitoring end-to-end processes even in scenarios where the Camunda Engine is not used for 
orchestrating the process end-to-end. The functionality in Optimize is called Event-Based Processes and it allows you 
to ingest process-related events from any external system of your choice.

This example demonstrates how you can send events from UiPath to Optimize’s REST API in order to monitor your UiPath Bots.
This can be useful if you want to get an overview of UiPath bot execution or if the bots are part of an end-to-end 
process that you want to monitor.

The following diagram should give you a better understanding of how this works:
![Event Ingestion Architecture][1]
In this example, we walk you step-by-step through the configuration of event-based processes in Optimize and show you 
how you can send events from UiPath bots to Optimize REST API. Once the events are ingested in Optimize, you can use 
the usual functionality within the Optimize User Interface to map the UiPath events to a BPMN process diagram.

## Step 1: Configure Event-Based Processes in Optimize
If you are using Event-Based Processes for the first time, you need to activate it for your user in the system
 configuration as well as define a REST API secret. The details can be found in the [Optimize documentation][2].
 
Alternatively you may use the provided `docker-compose.yml` file for a quick-start and preconfigured 
Optimize docker environment.
 
## Step 2: Download and Install UiPath Studio
In order to run this example, you need a local installation of UiPath Studio. Currently, UiPath Studio is 
only supported on Windows machines.

In this example, we won’t explain in detail how to install UiPath Studio, but here is  the general documentation from 
UiPath that you can follow Install [UiPath Studio][3] 

If you have an account for UiPath Orchestrator Cloud you will find a link to the Studio Installer, too.

## Step 3: Create a new Project in UiPath Studio

Once you have UiPath Studio installed, you can create a new automation project in UiPath Studio:

![New Project][4]

## Step 4: Install UiPath Web Activities Package

In order to send REST Calls from your UiPath bot you need to once install the official UiPath Web Activities Package.
You can do so by clicking the “Manage Packages” button in the main menu.

![Manage Packages][5]

Once you see the Manage Packages Overlay you need to search for ‘uipath.web.activities’.
The UiPath.Web.Activites package will show up and you need to follow the instructions on the screen to install it.

![UiPath Web Activities][6]

## Step 5: Add HTTP Request Activity

Once you added the Web Activities package to UiPath Studio, you can go ahead and search for the “HTTP Request” Activity 
in the Activities panel on the left-hand side of the screen:

![UiPath HTTP Request Activity][7]

Simply drag and drop the activity from the Activities panel into your sequence in the middle of the screen where it says “Drop Activity here”.

All the details about the HTTP Request Activity can also be found in the [UiPath documentation][8].

## Step 6: Configure HTTP Request Activity

Once you have dropped the activity into the sequence the HTTP Request Wizard will open.
Make sure you specify the local Optimize URL including the access_token you chose above in Step 1.
Additionally, it is important to set the request method to POST and access response as JSON as seen in the screenshot below:

![UiPath HTTP Request Activity Config][9]

Once you click on Ok you need to further configure the details of your request in the right-hand properties panel:

![UiPath HTTP Request Activity Config Properties][10]

You need to change the following properties:

Common:
* Change Display Name to “Process Start”

Options:
* Change BodyFormat to “application/json”
* Change Body by clicking on the … next to the input field to:

  ```
  "{""specversion"":""1.0"",""id"":""random123"",""source"":""uipath"",""traceid"":""instance1"",""type"":""processstart""}"
  ```
  
  ![UiPath HTTP Request Activity Config Body][11]
  
  Please note that in our example the ‘traceid’ and ‘id’ properties are fixed and not dynamic. For production usage, 
  you will need to refer to variables instead that are automatically updated since the traceid needs to be unique for 
  every new start of your UiPath process (but the same for events within the process run) and the id needs to be unique 
  for every single event that you send.
  
* Change Headers by clicking on the … next to the input field to:

  ![UiPath HTTP Request Activity Config Header][12]
  

## Step 7: Duplicate HTTP Request Activity

As a next step simply duplicate the HTTP Activity by right-clicking on it and choosing “Copy”. Afterward, just 
right-click and paste the activity into the sequence:

![UiPath HTTP Request Copy][13]

Next, rename the activity to “Process End” in the Properties Panel.

![UiPath HTTP Request Rename][14]

After, edit the body by clicking again on the three dots next to the input field. The final body should look like this:

```
"{""specversion"":""1.0"",""id"":""random1234"",""source"":""uipath"",""traceid"":""instance1"",""type"":""processend""}"
```

![UiPath HTTP Request ProcessEnd][15]

## Step 8: Run Sequence in UiPath

Now you are ready to run the whole sequence using the UiPath Debug Mode, simply click on “Debug File in the main menu”.

![UiPath Debug Run][16]

## Step 9: Open Optimize and map the Event-Based Process

Within Optimize you need to create a new Event-Based Process and add External Events as an Event Source.
Once you have done that you can map the two UiPath events from your bots to steps in a process. For simplicity reasons, 
we are using just a BPMN start and a BPMN end event.

Once you have mapped the events by using the checkboxes in the table below, you can save and publish the process so that
 you can use it for reporting.
 
![Optimize Event Based Process][17]

Once the process has been published, you can create a new report and use the process you just designed for all different
 kinds of reports.
 
![Optimize Event Based Process Report][18]

## Full Sample project

In the subfolder `SampleUiPathProject` you can find a complete UiPath project that was created following this guide.

[1]: ./docs/component_overview.png
[2]: https://docs.camunda.io/docs/self-managed/optimize-deployment/configuration/setup-event-based-processes/#event-based-process-configuration
[3]: https://docs.uipath.com/installation-and-upgrade/docs/studio-install-studio
[4]: ./docs/uipath-new-project.png
[5]: ./docs/uipath-manage-packages.png
[6]: ./docs/uipath-web-activities.png
[7]: ./docs/uipath-http-request-activity.png
[8]: https://docs.uipath.com/activities/docs/http-client
[9]: ./docs/uipath-http-request-activity-config.png
[10]: ./docs/uipath-http-request-activity-config-properties.png
[11]: docs/uipath-http-request-activity-config-properties-body-process-start.png
[12]: ./docs/uipath-http-request-activity-config-properties-header.png
[13]: ./docs/uipath-http-request-activity-config-copy.png
[14]: ./docs/uipath-http-request-activity-config-rename.png
[15]: docs/uipath-http-request-activity-config-properties-body-process-end.png
[16]: ./docs/uipath-debug-run.png
[17]: ./docs/optimize-event-based-process.png
[18]: ./docs/optimize-report.png