## Domain 'ordertypes'

The **ordertypes** subfolder contains CSV configuration files that help modify and create order types. It should be possible in most cases to configure them via a single CSV configuration file, however there can be as many CSV files as desired.
This is a possible example of how the configuration subfolder may look like:
```bash
ordertypes/
  └── ordertypes.csv
```
The CSV configuration allows to either modify exisiting order types or to create new order types, here are the possible headers with a typical set of values:

| <sub>Uuid</sub> | <sub>Void/Retire</sub> | <sub>Name</sub>  | <sub>Description</sub> | <sub>Java class name</sub> | <sub>Concept classes</sub>  | <sub>Parent</sub> |
| - | - | - | - | - | - | - |
| <sub>8189dbdd-3f10-11e4-adec-0800271c1b75</sub> | - | <sub>Radiology Order</sub>  | <sub>An order for radiology tests</sub> | <sub>org.openmrs.Order</sub> | <sub>Radiology Item;51200d71-5fdb-4ece-bc3d-c792068c6455</sub> | - |

###### Headers `Name` and `Description`
The order type **name** is mandatory and must be provided. The description is optional, however it is best practise to provide a rich and meaningful description.

###### Header `Java class name`
This can be `org.openmrs.Order` class or any of its subclasses (such as `org.openmrs.DrugOrder` or `org.openmrs.TestOrder`.)

###### Header `Concept classes`
An array of identifiers of concept classes that are associated with the order type. Those identifiers can be concept classes names or UUIDs.

###### Header `Parent`
This is a pointer to the parent order type. Both order type UUIDs and names are supported to refer to the parent order type.

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).