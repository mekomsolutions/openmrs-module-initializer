## Domain 'relationshiptypes'

The **relationshiptypes** subfolder contains CSV configuration files that help modify and create Relationship 
types. It should be possible in most cases to configure them via a single CSV configuration file, however 
there can be as many CSV files as desired.

This is a possible example of how the configuration subfolder may look like:
```bash
relationshiptypes/
  └── relationshiptypes.csv
```
The CSV configuration allows to either modify existing relationshiptypes or to
create new relationshiptypes. Here is a sample CSV:

| <sub>Uuid</sub>                                   | <sub>Void/Retire</sub>    | <sub>Name</sub>           | <sub>Description</sub>                                | <sub>Weight</sub>     | <sub>Preferred</sub>  | <sub>A is to b</sub>  | <sub>B is to a</sub>  | <sub>_order:2000</sub>    |
|-------------------------------------- |-------------  |-------------- |-------------------------------------------    |--------   |-----------    |-----------    |-----------    |-------------  |
| <sub>c86d9979-b8ac-4d8c-85cf-cc04e7f16315</sub>   |               | <sub>Uncle/Nephew</sub>   | <sub>A relationship of an uncle and his nephew</sub>  |           | <sub>true</sub>       | <sub>Uncle</sub>      | <sub>Nephew</sub>     |               |

###### Header `Name` *(optional)*
The relationship type name.

###### Header `Description` *(mandatory)*
The relationship type description.

###### Header `Weight` *(optional)*
The relationship type weight.

###### Header `Preferred` *(mandatory)*
Relationship type preferred?

###### Header `A is to b` *(mandatory)*
A is to b.

###### Header `B is to a` *(mandatory)*
B is to a.

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see
[here](../api/src/test/resources/testAppDataDir/configuration).
