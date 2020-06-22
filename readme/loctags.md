## Domain 'locationtags'
The **locationtags** subfolder contains CSV import files for saving location tags in bulk.
This is a possible example of its content:
```bash
locationstags/
  ├──locationtags.csv
  └── ...
```
There is currently only one format for the location tag CSV line,
here are the possible headers with a sample data set:

| <sub>Uuid                                </sub> | <sub>Void/Retire</sub> | <sub>Name       </sub> | <sub>Description</sub> |
|--------------------------------------|-------------|--------------------------|-------------|
| <sub>b03e395c-b881-49b7-b6fc-983f6bddc7fc</sub> | <sub>           </sub> | <sub>Yoga Location</sub> | <sub>           </sub> |
| <sub>                                    </sub> | <sub>           </sub> | <sub>Nice Sofa    </sub> | <sub>           </sub> |

<br/>

###### Header `Name` *(mandatory)*
This is _not_ a localized header.

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see
[here](../api/src/test/resources/testAppDataDir/configuration).