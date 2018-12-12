## Domain 'personattributetypes'
The **personattributetypes** subfolder contains CSV import files for saving person attribute types in bulk. This is a possible example of its content:
```bash
personattributetypes/
  ├── registration_pat.csv
  └── ...
```
There is currently only one format for the person attribute type CSV line, here are the possible headers:

| <sub>Uuid</sub> | <sub>Void/Retire</sub> | <sub>Name</sub> | <sub>Description</sub> | <sub>Format</sub> | <sub>Foreign uuid</sub> | <sub>Searchable</sub> | <sub>_order:1000</sub> |
| - | - | - | - | - | - | - | - |

Headers that start with an underscore such as `_order:1000` are metadata headers. The values in the columns under those headers are never read by the CSV parser.
<br/>Let's review some important headers.

###### Header `Name`
This is _not_ a localized header.

###### Header `Format`
Here are the possible values for this column: `java.lang.Boolean`, `java.lang.Character`, `java.lang.Float`, `java.lang.Integer`, `java.lang.String`, `org.openmrs.Concept`, `org.openmrs.Drug`, `org.openmrs.Encounter`, `org.openmrs.Location`, `org.openmrs.Patient`, `org.openmrs.Person`, `org.openmrs.ProgramWorkflow`, `org.openmrs.Provider`, `org.openmrs.User`, `org.openmrs.util.AttributableDate`.

###### Header `Foreign uuid`
When the header `Format` refers to an OpenMRS class (such as `org.openmrs.Concept` for example), `Foreign uuid` should point to the UUID of an existing instance of that class. 

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).