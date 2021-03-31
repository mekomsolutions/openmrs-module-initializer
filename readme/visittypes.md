## Domain 'visittypes'
The **visittypes** subfolder contains CSV import files for saving visit types in bulk. This is a possible example of its content:
```bash
visittypes/
  ├──visittypes.csv
  └── ...
```
There is currently only one format for the visit type CSV line, here are the possible headers with a sample data set:

|<sub>Uuid</sub> |<sub>Void/Retire</sub> |<sub>Name</sub> | <sub>Description</sub> | <sub>_order:1000</sub> |
| - | - | - | - | - |
|<sub>32176576-1652-4835-8736-826eb0237482</sub>|<sub></sub>| <sub>General</sub> | <sub>A General Visit</sub> |<sub></sub>|

Headers that start with an underscore such as `_order:1000` are metadata headers. The values in the columns under those headers are never read by the CSV parser.

Let's review some important headers.

###### Header `Name` *(mandatory)*
This is _not_ a localized header.
<br/>The  name is a secondary identifier to access a visit type, it will be used to attempt fetching the visit type if no UUID is provided.

###### Header `Description`
A description is used to give more information about the visit type.

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).
