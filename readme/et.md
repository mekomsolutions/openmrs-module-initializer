## Domain 'encountertypes'

The **encountertypes** subfolder contains CSV configuration files that help modify and create encounter types. It should be possible in most cases to configure them via a single CSV configuration file, however there can be as many CSV files as desired.
This is a possible example of how the configuration subfolder may look like:
```bash
encountertypes/
  └── encountertypes.csv
```
The CSV configuration allows to either modify exisiting encounter types or to create new encounter types. Here is a sample CSV:

|<sub>Uuid</sub>                                 | <sub>Void/Retire</sub> | <sub>Name</sub>                      | <sub>Description</sub>                                                   | <sub>View privilege</sub>            | <sub>Edit privilege</sub>            | <sub>_order:1000</sub> |
|--------------------------------------|-------------|---------------------------|---------------------------------------------------------------|---------------------------|---------------------------|-------------|
|                                      |             | <sub>Triage Encounter</sub>          | <sub>An encounter for triaging patients.</sub>                           |                           |                           |             |
| <sub>aaa1a367-3047-4833-af27-b30e2dac9028</sub> |             | <sub>Medical History Encounter</sub> | <sub>An interview about the patient medical history.</sub>               |                           |                           |             |
| <sub>439559c2-a3a4-4a25-b4b2-1a0299e287ee</sub> |             | <sub>X-ray Encounter</sub>           | <sub>An encounter during wich X-rays are performed on the patient.</sub> | <sub>Can: View X-ray encounter</sub> | <sub>Can: Edit X-ray encounter</sub> |             |

Let's review some important headers.

###### Headers `Name` and `Description`
The encounter type **name** is mandatory and must be provided. The description is optional, however it is best practise to provide a rich and meaningful description.

###### Headers `View privilege` and `Edit privilege`
Respectively the privileges to view and edit the _data_ recorded during encounters of the type described by the CSV line.
Those fields are optional, but if a reference to a privilege is provided and that the privilege cannot be found, then the CSV line will not be processed and the encounter type will not be created or edited.

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).