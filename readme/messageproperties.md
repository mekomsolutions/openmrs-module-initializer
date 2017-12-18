## Domain 'messageproperties'
The **messageproperties** subfolder allows to drop in message properties files for I18N support. This is a possible example of its content:
```
  messageproperties/
    \_ metadata_en.properties
    \_ metadata_km_KH.properties
    \_ ...
```
There can be as many message properties internationalization files as needed.
This domain differs from most others since nothing from its configuration is persisted in database, everything is stored in the runtime memory upon starting the Initializer.

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).