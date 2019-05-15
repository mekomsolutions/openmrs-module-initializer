## Domain 'roles'
The **roles** subfolder contains CSV import files for saving roles in bulk. This is a possible example of its content:
```bash
roles/
  ├──roles.csv
  └── ...
```
There is currently only one format for the role CSV line, here are the possible headers with a sample data set:

|<sub>Uuid</sub> |<sub>Role name</sub> |<sub>Description</sub> | <sub>Inherited roles</sub> | <sub>Privileges</sub> | <sub>_order:1000</sub> |
| - | - | - | - | - | - |
|<sub>79e05171-afcb-47a3-84ec-3f7df078628f</sub>|<sub>Organizational: Clinician</sub>| <sub>A doctor having direct contact with patients rather than being involved with theoretical or laboratory studies.</sub> | <sub>Application: Records Allergies; Application: Uses Patient Summary</sub> | <sub>Add Allergies; Add Patient</sub> |

Headers that start with an underscore such as `_order:1000` are metadata headers. The values in the columns under those headers are never read by the CSV parser.


Let's review some important headers.

###### Header `role name`
This is _not_ a localized header.
<br/>The role name is a primary identifier to access a role, therefore the role name cannot be edited once the role has been created.

###### Header `Inherited roles`
A list of roles to inherit from. This list is made of a semicolon `;` separated list of role names.

###### Header `Privileges`
The list of privileges that this role contains. This list is made of a semicolon `;` separated list of privilege names.


#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).