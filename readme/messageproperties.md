## Domain 'messageproperties'
The **messageproperties** subfolder allows to drop in message properties files for i18n support. This is a possible example of its content:
```bash
messageproperties/
  ├── metadata_en.properties
  ├── metadata_km_KH.properties
  └── ...
```
There can be as many message properties internationalization files as needed.
This domain differs from most others since nothing from its configuration is persisted in database, everything is stored in the runtime memory upon starting the Initializer.

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).