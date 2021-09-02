## Domain 'providerroles'
The **providerroles** subfolder contains CSV import files for saving Provider Roles in bulk. 
This domain requires that an implementation is running the providermanagement module.
This is a possible example of its content:
```bash
providerroles/
  ├──providerroles.csv
  └── ...
```
The CSV line supports the usual OpenMRS metadata headers:

###### Header `Uuid` *(mandatory)*
This uniquely identifies the Provider Role and must be a valid UUID.  This column is required.

###### Header `Name` *(mandatory)*
Provider Role names are *required* but are not used as unique identifies for the Provider role.

###### Header `Description`
Provider Role description is *optional*

In addition to these headers, the CSV supports adding Sets of several additional properties.
These need to be added with unique column headers that start with the following prefixes, based on the properties you wish to set:

###### Header Prefix `Supervisee Provider Role`

These must refer to the UUID of the Provider Role that you wish to reference.  You can include 0-N Supervisee Provider Roles
by adding 0-N columns whose Headers start with "Supervisee Provider Role".

For example, if you only have a maximum of 1 of these, you can add a single column named "Supervisee Provider Role".  If
you have more than one of these you wish to associate, you can have columns named "Supervisee Provider Role 1", "Supervisee Provider Role 2", 
and so on.

###### Header Prefix `Relationship Type`

These must refer to the UUID of the Relationship Type that you wish to reference.  You can include 0-N Relationship Types
by adding 0-N columns whose Headers start with "Relationship Type".

For example, if you only have a maximum of 1 of these, you can add a single column named "Relationship Type".  If
you have more than one of these you wish to associate, you can have columns named "Relationship Type 1", "Relationship Type 2",
and so on.

###### Header Prefix `Provider Attribute Type`

These must refer to the UUID of the Provider Attribute Type that you wish to reference.  You can include 0-N Provider Attribute Types
by adding 0-N columns whose Headers start with "Provider Attribute Type".

For example, if you only have a maximum of 1 of these, you can add a single column named "Provider Attribute Type".  If
you have more than one of these you wish to associate, you can have columns named "Provider Attribute Type 1", "Provider Attribute Type 2",
and so on.

###### Metadata headers

Headers that start with an underscore such as `_order:1000` are metadata headers. The values in the columns under those headers are never read by the CSV parser.

---

#### Examples:
Please see the following file for an example: [providerRoles.csv](../api/src/test/resources/testAppDataDir/configuration/providerroles/providerroles.csv).