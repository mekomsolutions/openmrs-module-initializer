## Domain 'patientidentifiertypes'
The **patientidentifiertypes** subfolder contains CSV import files for saving patient identifier types in bulk. This is a possible example of its content:
```bash
patientidentifiertypes/
  ├── patientidentifiertypes.csv
  └── ...
```
There is currently only one format for the patient identifier type CSV line, here are the possible headers:

| <sub>Uuid</sub> | <sub>Void/Retire</sub> | <sub>Name</sub> | <sub>Description</sub> | <sub>Required</sub> | <sub>Format</sub> | <sub>Format description</sub> | <sub>Validator</sub> | <sub>Location behavior</sub> | <sub>Uniqueness behavior</sub> | <sub>_order:1000</sub> |
| - | - | - | - | - | - | - | - | - | - |

Headers that start with an underscore such as `_order:1000` are metadata headers. The values in the columns under
those headers are never read by the CSV parser.

Let's review some important headers.

###### Header `Name`
Mandatory. This is _not_ a localized header.

###### Header `Required`
A true/false whether every patient MUST have this patient identifier type.
Note that a blank or missing value will trigger an exception:
```
org.hibernate.PropertyValueException:
  not-null property references a null or transient value : org.openmrs.PatientIdentifierType.required
```
This requires to actively set the value to a true-able (`TRUE`, `1`, `Yes`, ...) or a false-able value (`False`, `0`, `No`, ...).

###### Header `Format`
A regular expression defining what the identifier text should contain.

###### Header `Format description`
Textual explanation of the "Format" regular expression.

###### Header `Validator`
The full class name of an IdentifierValidator.

###### Header `Location behavior`
Specifies the way that location may be applicable for a particular Patient Identifer Type. Valid options are
`NOT_USED` and `REQUIRED`.

###### Header `Uniqueness behavior`
Valid options are
- `LOCATION`: Identifiers should be unique only across a location if the identifier's location property is not null
- `NON_UNIQUE`: Duplicate identifiers are allowed
- `UNIQUE`: Identifiers should be globally unique

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).
