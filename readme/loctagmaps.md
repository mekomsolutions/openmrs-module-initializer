## Domain 'locationtagmaps'
The **locationtagmaps** subfolder contains CSV import files for associating locations with location tags in bulk. 
This is a possible example of its content:
```bash
locationtagmaps/
  ├──locationtagmaps.csv
  └── ...
```
There is currently only one format for the locationtagmaps CSV line, here are the possible headers with a sample data set:

| <sub>Location</sub>                             | <sub>Login Location</sub> | <sub>Facility Location</sub> | <sub>b03e395c-b881-49b7-b6fc-983f6bddc7fc</sub> |
|-------------------------------------------------|---------------------------|------------------------------|-------------------------------------------------|
| <sub>a03e395c-b881-49b7-b6fc-983f6bddc7fc</sub> | <sub>TRUE</sub>           | <sub>false</sub>             |                                                 |
| <sub>OPD Room</sub>                             |                           | <sub>FALSE</sub>             | <sub>true</sub>                                 |

<br/>Let's review some important headers.

#### Header `Location` *(mandatory)*
This should refer to either the uuid or the name of an existing Location that you wish to associate tags with

#### Tag headers

All other headers should refer to either the uuid or the name of an existing Location Tag that you wish to assign / unassign.
The rules around how Location Tags are added to or removed from Locations are as follows:

* If a particular Location Tag is not present in the CSV as a header, then no tags of this type will be changed on a Location (existing tags of this type will be preserved).
* If a particular Location Tag is present in the CSV as a header
  * Any true value (true, TRUE) will ensure the LocationTag is associated with the Location
  * Any non-true value (including empty values) will ensure the tag is not associated with the Location

## Further examples
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).