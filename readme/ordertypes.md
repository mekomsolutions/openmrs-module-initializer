## Domain 'ordertypes'

The **ordertypes** subfolder contains CSV configuration files that help modify and create order types. It should be possible in most cases to configure them via a single CSV configuration file, however there can be as many CSV files as desired.
This is a possible example of how the configuration subfolder may look like:
```bash
ordertypes/
  └── ordertypes.csv
```
The CSV configuration allows to either modify exisiting order types or to create new order types, here are the possible headers:

| <sub>Uuid</sub> | <sub>Void/Retire</sub> | <sub>Name</sub>  | <sub>Description</sub> | <sub>Java class name</sub> | <sub>Parent</sub> |
| - | - | - | - | - | - |

###### Headers `Name` and `Description`
The order type **name** is mandatory and must be provided. The description is optional, however it is best practise to provide a rich and meaningful description.

###### Header `Java class name`
Can be org.openmrs.Order class or it's sub-classes (such as `org.openmrs.DrugOrder` or org.openmrs.TestOrder for example)

###### Header `Parent`
This is a pointer to the parent order type. Both order type UUIDs and names are supported to refer to another order type.

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).