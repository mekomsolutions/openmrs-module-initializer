## Domain 'proceduretypes'

The **proceduretypes** subfolder contains CSV configuration files that create, edit, or retire emrapi `ProcedureType` metadata.

This domain requires the **emrapi** module at version **3.4.0** or above (`ProcedureType` was added in emrapi 3.4.0). emrapi 3.4+ in turn requires OpenMRS Core 2.8.0 or above. On a stack that does not satisfy these conditions, the loader is silently skipped.

A typical layout:
```bash
proceduretypes/
  └── proceduretypes.csv
```

Sample CSV:

| <sub>Uuid</sub> | <sub>Void/Retire</sub> | <sub>Name</sub> | <sub>Description</sub> | <sub>_order:1000</sub> |
| --- | --- | --- | --- | --- |
| | | <sub>Appendectomy</sub> | <sub>Surgical removal of the appendix.</sub> | |
| <sub>aaa1a367-3047-4833-af27-b30e2dac9028</sub> | | <sub>Cholecystectomy</sub> | <sub>Surgical removal of the gallbladder.</sub> | |
| <sub>439559c2-a3a4-4a25-b4b2-1a0299e287ee</sub> | <sub>TRUE</sub> | <sub>Discontinued Procedure</sub> | <sub>Procedure no longer offered.</sub> | |

###### Headers `Name` and `Description`
Procedure type lookup uses `Uuid` first; only when `Uuid` is blank does the parser fall back to `Name` (a typo'd UUID will not silently rebind to a same-named row). `Name` is mandatory on every non-retire row — blank values are rejected and the row is reported in the failure summary. `Description` is optional.

#### Further examples:
See the test configuration folder: [api-2.8/src/test/resources/testAppDataDir/configuration/proceduretypes](../api-2.8/src/test/resources/testAppDataDir/configuration/proceduretypes).
