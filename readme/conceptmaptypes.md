## Domain 'conceptmaptypes'

The **conceptmaptypes** subfolder contains CSV configuration files that help
modify and create concept map types. It should be possible in most cases to
configure them via a single CSV configuration file, however there can be as
many CSV files as desired.

This is a possible example of how the configuration subfolder may look like:
```bash
conceptmaptypes/
  └── conceptmaptypes.csv
```
The CSV configuration allows to either modify existing concept map types or to
create new concept map types. Here is a sample CSV:

|<sub>Uuid</sub> | <sub>Void/Retire</sub> | <sub>Name</sub> | <sub>Description</sub> | <sub>Is hidden</sub> |
|----------------|------------------------|-----------------|------------------------|----------------------|
|                | <sub>true</sub>        | <sub>is-parent-to</sub> | | |
|                |                        | <sub>NARROWER-THAN</sub> | <sub>Target is narrower than the source</sub> | |
|                |                        | <sub>INTERNAL-ONLY</sub> | <sub>Not shown to end users</sub> | <sub>true</sub> |
| <sub>d3a5e8b1-6c2f-4e7a-9b0d-1f2a3c4d5e6f</sub> | | <sub>ASSOCIATED-WITH</sub> | <sub>Loosely associated concepts</sub> | |
| <sub>1ccba764-49d6-11e0-8fed-18a905e044dc</sub> | | <sub>RELATED-TO</sub> | <sub>Renames the existing 'related-to' map type</sub> | |

Existing map types are matched by UUID first, then by name. Providing the UUID
of an existing map type therefore allows renaming it.

###### Header `Name`
The concept map type **name** is mandatory. It is not localized.

###### Header `Description`
Optional.

###### Header `Is hidden`
Optional. When `true` the map type is flagged as hidden, which keeps it out of
user-facing map type pickers. Any other value, including a blank cell, sets the
map type to not hidden. Following the WYSIWYG rule of CSV lines, a blank cell
un-hides an existing hidden map type.

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see
[here](../api/src/test/resources/testAppDataDir/configuration).
