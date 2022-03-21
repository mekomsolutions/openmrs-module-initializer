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

If one needs to configure the order in which message properties files load, for example if one message properties
file contains message codes that should override those in another in the same locale, one can do this by utilizing a 
special message code named "_order".  Two message properties files in the same locale will be loaded in order based
first on the value of any "_order" property defined, and second based on the alphabetical order of the 
absolute path of the file.

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).