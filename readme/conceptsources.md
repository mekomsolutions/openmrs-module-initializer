## Domain 'conceptsources'

The **conceptsources** subfolder contains CSV configuration files that help
modify and create concept sources. It should be possible in most cases to
configure them via a single CSV configuration file, however there can be as
many CSV files as desired.

This is a possible example of how the configuration subfolder may look like:
```bash
conceptsources/
  └── conceptsources.csv
```
The CSV configuration allows to either modify existing concept sources or to
create new concept sources. Here is a sample CSV:

|<sub>Uuid</sub> | <sub>Void/Retire</sub> | <sub>Name</sub> | <sub>Description</sub> | <sub>HL7 Code</sub> | <sub>Unique ID</sub> |
|----------------|------------------------|-----------------|------------------------|---------------------|----------------------|
|                | <sub>true</sub>        | <sub>Ministry Code</sub> | <sub>A source to be retired</sub> | |
|                |                        | <sub>New concept source with random UUID</sub> | <sub>Has some description too</sub> | |
|                |                        | <sub>SNOMED CT</sub> | <sub>SNOMED Preferred mapping</sub> | <sub>SCT</sub> | |
|                |                        | <sub>RadLex</sub>    | <sub>Radiology Terms</sub> | <sub>RADLEX</sub> | <sub>2.16.840.1.113883.6.256</sub>

Both `name` and `description` are mandatory headers. Neither is localized.

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see
[here](../api/src/test/resources/testAppDataDir/configuration).