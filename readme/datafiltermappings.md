## Domain 'datafiltermappings'

The **datafiltermappings** subfolder contains CSV configuration files that help manage Data Filter entity to basis mappings. This domain differs from most other domains in the fact that each line of the CSV does not *directly* represent a flattened version of a database entity. Each line allows to configure a mapping between a Data Filter entity and a Data Filter basis in a table format. This is the reason why there is no UUID header for this domain.

This is a possible example of how the configuration subfolder may look like:
```bash
datafiltermappings/
  └── mappings.csv
```
The CSV configuration allows to either grant access to an entity through a basis or revoke access of an entity through a basis. Here is a sample CSV:

| <sub>Void/Retire</sub>| <sub>Entity UUID</sub> | <sub>Entity class</sub> | <sub>Basis UUID</sub> | <sub>Basis class</sub> | 
|-|-|-|-|-|
| | <sub>3f8f0ab7-c240-4b68-8951-bb7020be01f6</sub> | <sub>org.openmrs.Role</sub> | <sub>787ec1bd-19a6-4577-9121-899588795737</sub> | <sub>org.openmrs.Program</sub> |
| <sub>TRUE</sub> | <sub>4604e928-96bf-4e2c-be08-cbbc40dd000c</sub> | <sub>org.openmrs.Privilege</sub> | <sub>a03e395c-b881-49b7-b6fc-983f6bddc7fc</sub> | <sub>org.openmrs.Location</sub> |

The above example shows how to
1. Grant access to the `Role` UUID'd `3f8f0ab7-c240-4b68-8951-bb7020be01f6` on the basis of the `Program` UUID'd `787ec1bd-19a6-4577-9121-899588795737`.
2. Revoke access to the `Privilege` UUID'd `4604e928-96bf-4e2c-be08-cbbc40dd000` on the basis of the `Location` UUID'd `a03e395c-b881-49b7-b6fc-983f6bddc7fc`.

Entities and bases are referrenced to by their UUIDs, and their types are specified as their class name (eg. `org.openmrs.Role`).

Let us summarise the set of available headers.

###### Header `Void/Retire` 
If `true` the entity access will be granted, if `false` or empty the the entity access will be revoked. This header is mandatory.

###### Header `Entity UUID` 
The entity UUID. This header is mandatory.

###### Header `Entity class` 
The entity class name. This header is mandatory.

###### Header `Basis UUID` 
The basis UUID. This header is mandatory.

###### Header `Basis class` 
The basis class name. This header is mandatory.

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).