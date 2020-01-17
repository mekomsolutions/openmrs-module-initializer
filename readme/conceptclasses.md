## Domain 'conceptclasses'

The **conceptclasses** subfolder contains CSV configuration files that help
modify and create concept classes. It should be possible in most cases to
configure them via a single CSV configuration file, however there can be as
many CSV files as desired.

This is a possible example of how the configuration subfolder may look like:
```bash
conceptclasses/
  └── conceptclasses.csv
```
The CSV configuration allows to either modify existing concept classes or to
create new concept classes. Here is a sample CSV:

|<sub>Uuid</sub>                                 | <sub>Void/Retire</sub> | <sub>Name</sub>                      |
|--------------------------------------|-------------|---------------------------|
|                                      | <sub>true</sub>        | <sub>Procedure</sub>          |
|                                      |             | <sub>New concept class with random UUID</sub> |
| <sub>1dd85dab-f6d6-4bec-bde6-c4cddea92d35</sub> |             | <sub>A concept class with UUID specified</sub>           |

The only one header specific to this domain is `Name`.

###### Header `Name`
The concept class **name** is mandatory.

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see
[here](../api/src/test/resources/testAppDataDir/configuration).