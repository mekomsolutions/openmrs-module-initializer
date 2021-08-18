## Domain 'relationshiptypes'

The **relationshiptypes** subfolder contains CSV configuration files that help modify and create Relationship 
types. It should be possible in most cases to configure them via a single CSV configuration file, however 
there can be as many CSV files as desired.

This is a possible example of how the configuration subfolder may look like:
```bash
relationshiptypes/
  └── relationshiptypes.csv
```
The CSV configuration allows to either modify existing relationship types or to
create new relationship types. Here is a sample CSV:

| <sub>Uuid</sub>                                   | <sub>Void/Retire</sub>    | <sub>Name</sub>           | <sub>Description</sub>                                | <sub>A is to B</sub>  | <sub>B is to A</sub>  | <sub>Weight</sub>     | <sub>Preferred</sub>  | <sub>_order:2000</sub>    |
|-------------------------------------- |-------------  |-------------- |-------------------------------------------    |--------   |-----------    |-----------    |-----------    |-------------  |
| <sub>c86d9979-b8ac-4d8c-85cf-cc04e7f16315</sub>   |               | <sub>Grandparent/Grandchild</sub>   | <sub>The child of one's child, or parent of one's parent</sub>  | <sub>Grandparent</sub>      | <sub>Grandchild</sub>     | <sub>3</sub> | <sub>true</sub> |               |

Please see the [RelationshipType documentation](https://docs.openmrs.org/doc/org/openmrs/RelationshipType.html)
for information about how relationship types should be used in OpenMRS.

###### Header `Name` *(optional)*
The relationship type name.

###### Header `Description` *(mandatory)*
The relationship type description.

###### Header `A is to B` *(mandatory)*
The relationship that one person has to the other.

###### Header `B is to A` *(mandatory)*
The corresponding relationship to `A is to B`.

###### Header `Weight` *(optional)*
An integer. Probably not used for anything.

###### Header `Preferred` *(optional)*
A boolean. Probably not used for anything.


#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see
[here](../api/src/test/resources/testAppDataDir/configuration).
