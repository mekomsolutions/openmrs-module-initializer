## Domain 'autogenerationoptions'

The **autogenerationoptions** subfolder contains CSV configuration files that help
modify and create autogeneration options. It should be possible in most cases to
configure them via a single CSV configuration file, however there can be as
many CSV files as desired.

This is a possible example of how the configuration subfolder may look like:
```bash
autogenerationoptions/
  └── autogenerationoptions.csv
```
The CSV configuration allows to either modify existing autogeneration options or to
create new autogeneration options. Here is a sample CSV:

| <sub>Uuid</sub>                                 | <sub>Identifier Type</sub> | <sub>Location</sub>      | <sub>Identifier Source</sub>                    | <sub>Manual Entry Enabled</sub> | <sub>Auto Generation Enabled</sub> | <sub>Sort Weight</sub> | <sub>_order:2000</sub> |
|--------------------------------------|-----------------|---------------|--------------------------------------|----------------------|-------------------------|-------------|-------------|
| eade77b6-3365-47ed-9ee3-2324598629eb | Legacy ID       |  | 9eca4f4e-707f-4lb8-8289-2f9b6e93803f | true                 | false                   |             |             |



###### Header `Identifier Type` *(mandatory)*
The **patient identifier type** associated with the autogeneration option. The patient identifier type can provided by:

* Name, eg. `"Legacy ID"`
* UUID, eg. `"f0f8ba64b-ea57-4a41-b33c-9dfc59b0c60a"`

###### Header `Identifier Source` *(mandatory)*
The **identifier source** associated with the autogeneration option. The identifier source can be set by providing the source `"UUID"`.


###### Header `Location`
The **location** associated with the autogeneration option. The location can be provided by:

* Name, eg. `"Isolation Ward"` 
* UUID, eg. `"eade77b6-3365-47ed-9ee3-2324598629eb"`

###### Header `Manual Entry Enabled`
Can either be `true` or `false`. Determines whether identifier generation is manual.

###### Header `Auto Generation Enabled` 
Can either be `true` or `false`. Determines whether identifier generation is automatic.

**Note**: The 'autogeneration option' domain doesn't support the `"Void/Retire"` column. If a value is provided, the line will be processed but the `"Void/Retire"` column will be ignored. 

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see
[here](../api/src/test/resources/testAppDataDir/configuration).