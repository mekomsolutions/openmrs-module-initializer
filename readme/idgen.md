## Domain 'idgen'

The **idgen** subfolder contains CSV configuration files that help modify and create identifier sources. It should be possible in most cases to configure them via a single CSV configuration file, however there can be as many CSV files as desired.
This is a possible example of how the configuration subfolder may look like:
```bash
idgen/
  └── idgen.csv
```
The CSV configuration allows to either modify exisiting identifier sources or to create new identifier sources, here are the possible headers:

| <sub>Uuid</sub> | <sub>Void/Retire</sub> | <sub>Identifier type</sub>  | <sub>Name</sub> | <sub>Description</sub> | <sub>Prefix</sub> | <sub>Suffix</sub> | <sub>First identifier base</sub> | <sub>Min length</sub> | <sub>Max length</sub> | <sub>Base character set</sub>|
| - | - | - | - | - | - | - | - | - | - | - |

The current implementation mainly focuses on [`SequentialIdentifierGenerator`](https://github.com/openmrs/openmrs-module-idgen/blob/448573a0d360a6929893d89c062f9968e5fbcf0e/api/src/main/java/org/openmrs/module/idgen/SequentialIdentifierGenerator.java) as identifier source type, hence the suggested use of `Prefix`, `Suffix`, `First identifier base`, `Min length`, `Max length` and `Base character set` headers.

###### Header `Identifier type`
This is the reference to the underlying identifier type, both an identifier type name or UUID can be provided. 

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).