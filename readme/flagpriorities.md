## Domain 'flagpriorities'
The **Flag Priorities** subfolder contains CSV import files for saving Priority entities which are used to determine the visual styling and importance level of patient flags. Below is a possible example of its content:

```bash
flagpriorities/
  ├──priorities.csv
  └── ...
```
Here are the possible headers with a sample data set:

| <sub>Uuid</sub>                                 | <sub>name</sub> | <sub>style</sub> | <sub>rank</sub> | <sub>description</sub> |
|--------------------------------------|-------------|----------------|----------------|----------------|
| <sub>526bf278-ba81-4436-b867-c2f6641d060a</sub> |   <sub>High Priority</sub>         | <sub>color:red</sub> | <sub>1</sub> | <sub>High priority flags</sub> |
| <sub>627bf278-ba81-4436-b867-c2f6641d060b</sub> |   <sub>Medium Priority</sub>         | <sub>color:orange</sub> | <sub>2</sub> | <sub>Medium priority flags</sub> |

Let's review the headers as below

###### Header `UUID` *(optional)*
This unique identifier represents the different priorities.

###### Header `Name` *(required)*
This is the descriptive name of the priority.

###### Header `Rank` *(required)*
This is an integer value representing the priority rank. Lower numbers indicate higher priority.

###### Header `Description` *(optional)*
A description of the priority.

###### Header `Style` *(optional)*
LEGACY: Only used for RefApp 2.x. This is the CSS style string applied to flags with this priority (e.g., "color:red", "background-color:yellow"). In OpenMRS 3+, this is configured on the frontend.

#### Requirements
* The [patientflags module](https://github.com/openmrs/openmrs-module-patientflags) version 3.0 or higher must be installed
* The OpenMRS version must be 2.2 or higher

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api-2.4/src/test/resources/testAppDataDir/configuration).

