## Domain 'systemtasks'
The **systemtasks** subfolder contains CSV import files for saving System Tasks in bulk.
This domain requires that an implementation is running the [tasks module](https://github.com/openmrs/openmrs-module-tasks).

This is a possible example of its content:
```bash
systemtasks/
  ├──systemtasks.csv
  └── ...
```
The CSV line supports the following headers:

###### Header `Uuid` *(mandatory)*
This uniquely identifies the System Task and must be a valid UUID.

###### Header `Void/Retire`
Set to `true` to retire the system task.

###### Header `Name` *(mandatory)*
A unique identifier/code for the system task (e.g., `vital-check`, `follow-up`).

###### Header `Title` *(mandatory)*
The human-readable title of the system task.

###### Header `Description`
A longer description of what the system task involves.

###### Header `Priority`
The priority level for the task. Must be one of: `HIGH`, `MEDIUM`, or `LOW`.

###### Header `Default Assignee Role`
The default provider role to assign this task to. Can be specified as either:
- A UUID of the provider role
- The name of the provider role

This requires the [providermanagement module](https://github.com/openmrs/openmrs-module-providermanagement) to be installed.

###### Header `Rationale`
An explanation of why this task is important or necessary.

###### Metadata headers

Headers that start with an underscore such as `_order:1000` are metadata headers. The values in the columns under those headers are never read by the CSV parser.

---

#### Examples:
Please see the following file for an example: [systemtasks.csv](../api/src/test/resources/testAppDataDir/configuration/systemtasks/systemtasks.csv).
