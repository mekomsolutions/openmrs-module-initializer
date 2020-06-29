## Domain 'locationtagmaps'
The **locationtagmaps** subfolder contains CSV import files for assigning location tags to locations.
This is a possible example of its content:
```bash
locationstagmappings/
  ├──locationtagmappings.csv
  └── ...
```
There is currently only one format for the location tag map CSV line.
Here are the possible headers with a sample data set:

| <sub>Void/Retire</sub> | <sub>Location Name</sub> | <sub>Location Tag Name    </sub> |
|---------------------|--------------------------|----------------------------------|
| <sub>           </sub> | <sub>Front Desk   </sub> | <sub>Login Location       </sub> |
| <sub>           </sub> | <sub>Front Desk   </sub> | <sub>Registration Location</sub> |
| <sub>           </sub> | <sub>Clinic       </sub> | <sub>Login Location       </sub> |

<br/>

Both `Location Name` and `Location Tag Name` are mandatory. Neither is localized.

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see
[here](../api/src/test/resources/testAppDataDir/configuration).