## Domain 'visittypes'
The **visittypes** subfolder contains CSV import files for saving visittypes in bulk. This is a possible example of its content:
```bash
visittypes/
  ├──visittypes.csv
  └── ...
```
There is currently only one format for the visit type CSV line, here are the possible headers with a sample data set:

|<sub>Uuid</sub> |<sub>Void/Retire</sub> |<sub>Name</sub> | <sub>Description</sub> | <sub>_order:1000</sub> |
| - | - | - | - | - |
|<sub>9o805171-afcb-444f-84ec-3f7df078628f</sub>|<sub></sub>| <sub>General </sub> | <sub>A General Visit</sub> |<sub></sub>|

Headers that start with an underscore such as `_order:1000` are metadata headers. The values in the columns under those headers are never read by the CSV parser.


Let's review some important headers.

###### Header `Name` *(mandatory)*
This is _not_ a localized header.
<br/>The  Name is a secondary identifier to access a visit type, it will be used to attempt fetching the visit type if no UUID is provided.

###### Header `Description`
A Description is used to give more information about the visit type.

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).