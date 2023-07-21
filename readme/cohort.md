## Domain 'cohorttypes'

The **cohorttypes** subfolder contains CSV import files for defining cohort types, used by the cohort module.

This is a possible example of its contents:
```bash
cohorttypes/
  ├──cohorttypes.csv
  └── ...
```

The format of this CSV should be as follows:

| <sub>Uuid</sub> |<sub>Void/Retire</sub> | <sub>Name</sub> | <sub>Description</sub> | <sub>_order:1000</sub> |
| - | - | - | - | - |
| <sub>3ab0118c-ba0c-42df-ac96-c573c72eed5e</sub> | | <sub>System List</sub> | <sub>System-Generated Lists</sub> | |

Headers that start with an underscore such as `_order:1000` are metadata headers. The values in the columns under those headers are never read by the CSV parser.

###### Header `Name`

This is *required* for every entry. This will be the name of the cohort type, wherever that is used.

###### Header `Description`

This is *required* for every entry. This will be the description of the cohort type, wherever that is used.

## Domain 'cohortattributetypes'

The **cohortattributetypes** subfolder contains CSV import files for defining cohort Attribute types, used by the cohort module.

This is a possible example of its contents:
```bash
cohorttypes/
  ├──cohortattributetypes.csv
  └── ...
```

The format of this CSV should be as follows:

| <sub>Uuid</sub> |<sub>Void/Retire</sub> | <sub>Name</sub> | <sub>Description</sub> | <sub>Datatype classname</sub> | <sub>_order:1000</sub> |
| - | - | - | - | - | - |
| <sub>09790099-9190-429d-811a-aac9edb8d98e</sub> | | <sub>Control</sub> | <sub>This is a control group.</sub> | <sub>org.openmrs.customdatatype.datatype.FreeTextDatatype</sub> |

Headers that start with an underscore such as `_order:1000` are metadata headers. The values in the columns under those headers are never read by the CSV parser.

###### Header `Name`

This is *required* for every entry. This will be the name of the cohort attribute type, wherever that is used.

###### Header `Description`

This is *required* for every entry. This will be the description of the cohort attribute type, wherever that is used.

###### Header `Datatype classname` *(mandatory)*
This points to the Java type of the attribute that is defined by the attribute type. [Here](https://github.com/openmrs/openmrs-core/tree/2.1.1/api/src/main/java/org/openmrs/customdatatype/datatype) is the list of possible values.

