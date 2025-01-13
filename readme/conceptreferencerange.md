## Domain 'conceptreferencerange'

The **conceptreferencerange** subfolder contains CSV configuration files that help
modify and create concept reference ranges. It should be possible in most cases to
configure them via a single CSV configuration file, however there can be as
many CSV files as desired.

This is a possible example of how the configuration subfolder may look like:
```bash
conceptreferencerange/
  └── conceptreferencerange.csv
```
The CSV configuration allows to either modify existing concept reference ranges or to
create new concept reference ranges. Here is a sample CSV:

| **<sub>Uuid</sub>**                             | **<sub>Concept Numeric Uuid</sub>**             | **<sub>Absolute Low</sub>** | **<sub>Critical Low</sub>** | **<sub>Normal Low</sub>** | **<sub>Normal High</sub>** | **<sub>Critical High</sub>** | **<sub>Absolute High</sub>** | **<sub>Criteria</sub>**          |
|-------------------------------------------------|-------------------------------------------------|-----------------------------|-----------------------------|---------------------------|----------------------------|------------------------------|------------------------------|----------------------------------|
| <sub>bc059100-4ace-4af5-afbf-2da7f3a34acf</sub> | <sub>a09ab2c5-878e-4905-b25d-5784167d0216</sub> | <sub>-100.5</sub>           | <sub>-85.7</sub>            | <sub>-50.3</sub>          | <sub>45.1</sub>            | <sub>78</sub>                | <sub>98.8</sub>              | <sub>$patient.getAge() > 3</sub> |

There is only one mandatory header specific to this domain:

###### Header `Concept Numeric Uuid`
The Concept Numeric UUID for the concept reference range is mandatory and must correspond to an existing concept numeric. 

#### Further examples:
Please look at the test configuration folder for sample import files for concept reference ranges, see
[here](../api-2.7/src/test/resources/testAppDataDir/configuration/conceptreferencerange/conceptreferencerange.csv).