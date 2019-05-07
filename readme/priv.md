## Domain 'privileges'
The **privileges** subfolder contains CSV import files for saving privileges in bulk. This is a possible example of its content:
```bash
privileges/
  ├──privileges.csv
  └── ...
```
There is currently only one format for the privilege CSV line, here are the possible headers with a sample data set:

|<sub>Uuid</sub> |<sub>Privilege name</sub> | <sub>Description</sub> | <sub>_order:1000</sub> |
| - | - | - | - |
|| <sub>Read Attachments</sub> | <sub>Has read access to attachments.</sub> ||
| <sub>9d4cbaeb-9c87-442f-bfdd-0d17402f319f</sub> | <sub>Create Attachments</sub> | <sub>Has write access to attachments.</sub> ||

Headers that start with an underscore such as `_order:1000` are metadata headers. The values in the columns under those headers are never read by the CSV parser.

The privilege domain is somewhat different than other domains in the sense that privileges cannot almost not be edited. This domain will mainly be used to create new privileges.

<br/>Let's review some important headers.

###### Header `Privilege name`
This is _not_ a localized header.
<br/>The privilege name is a primary identifier (alongside its UUID) to access a privilege, therefore the privilege name cannot be edited once the privilege has been created.

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).