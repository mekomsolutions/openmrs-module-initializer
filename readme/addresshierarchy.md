## Domain 'addresshierarchy'
The **addresshierarchy** subfolder contains Address Hierarchy configuration files. The `addressConfiguration.xml` file contains column information and the file containing the Address Hierarchy entries e.g. `addresshierarchy.csv`. The subfolder also contains i18n messages files that are necessary in populating the address/location i18n cache. The messages files can be similarly defined as those loaded by the [messageproperties](readme/messageproperties.md) domain.

```bash
addresshierarchy/
  ├── addressConfiguration.xml
  ├── addresshierarchy.csv
  ├── messages_en.properties
  └── messages_fr.properties
```

## Requirements
* You will need to install [Address Hierarchy Module](https://addons.openmrs.org/show/org.openmrs.module.addresshierarchy) version 2.17.0 and above for this domain to load correctly. 

### Further examples:
* Find out more how Address Hierarchy is used and configured in OpenMRS on the wiki documentaion: https://wiki.openmrs.org/display/docs/Address+Hierarchy+Module
* Please also look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).
