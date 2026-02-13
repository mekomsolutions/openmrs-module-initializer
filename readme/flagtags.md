## Domain 'flagtags'
The **Flag Tags** subfolder contains CSV import files for saving Tag entities which are used to categorize and filter patient flags. Below is a possible example of its content:

```bash
flagtags/
  ├──tags.csv
  └── ...
```
Here are the possible headers with a sample data set:

| <sub>Uuid</sub>                                 | <sub>name</sub> | <sub>roles</sub> | <sub>display points</sub> | <sub>description</sub> |
|--------------------------------------|-------------|----------------|----------------|----------------|
| <sub>526bf278-ba81-4436-b867-c2f6641d060a</sub> |   <sub>HIV</sub>         | <sub>Clinician;Nurse</sub> | <sub>Patient Summary</sub> | <sub>Tags for HIV-related flags</sub> |
| <sub>627bf278-ba81-4436-b867-c2f6641d060b</sub> |   <sub>Clinical</sub>         | <sub></sub> | <sub>Patient Summary;Patient Dashboard</sub> | <sub>General clinical flags</sub> |

Let's review the headers as below

###### Header `UUID` *(optional)*
This unique identifier represents the different tags.

###### Header `Name` *(required)*
This is the descriptive name of the tag.

###### Header `Roles` *(optional)*
A semi-colon separated list of Role identifiers (UUID or name). Flags associated with this tag will be visible to users with these roles.

###### Header `Description` *(optional)*
A description of the tag.

###### Header `Display Points` *(optional)*
LEGACY: Only used for RefApp 2.x. A semi-colon separated list of DisplayPoint identifiers (UUID or name). Flags associated with this tag will be displayed at these display points in RefApp 2.x. In OpenMRS 3+, this is configured on the frontend.

#### Requirements
* The [patientflags module](https://github.com/openmrs/openmrs-module-patientflags) version 3.0 or higher must be installed
* The OpenMRS version must be 2.2 or higher

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api-2.4/src/test/resources/testAppDataDir/configuration).

