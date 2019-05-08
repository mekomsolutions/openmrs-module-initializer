## Domain 'metadatamappings'
The **metadatamappings** subfolder contains CSV import files for saving metadata terms mappings in bulk. This is a possible example of its content:
```bash
metadatamappings/
  ├──metadataterms.csv
  └── ...
```
There is currently only one format for the CSV line, here are the possible headers with a sample data set:

| <sub>Uuid</sub> |<sub>Void/Retire</sub> | <sub>Mapping source</sub> | <sub>Mapping code</sub>  | <sub>Metadata class name</sub>  | <sub>Metadata Uuid</sub> | <sub>_order:1000</sub> |
| - | - | - | - | - | - | - |
| <sub>21e24b36-f9e3-4b0e-986d-9899665597f7</sub> | | <sub>org.openmrs.module.emrapi</sub> | <sub>emr.primaryIdentifierType</sub> | <sub>org.openmrs.PatientIdentifierType</sub>  | <sub>264c9e75-77da-486a-8361-31558e051930</sub>  | |

Headers that start with an underscore such as `_order:1000` are metadata headers. The values in the columns under those headers are never read by the CSV parser.

Let's review some important headers.

###### Header `Mapping source`
A logical grouping of metadata terms mappings.

###### Header `Mapping code`
The code part of the metadata term mapping. That is a unique code, in the scope of the source, that points to and represents the metadata. Together the mapping source and the mapping code form the metadata term mapping that identify univocally a piece of metadata.

###### Header `Metadata class name`
The complete Java class name of the metadata object, eg. `org.openmrs.PatientIdentifierType`, `org.openmrs.Location`, ... etc.

###### Header `Metadata Uuid`
The UUID of the metadata object represented by the metadata term mapping.

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).