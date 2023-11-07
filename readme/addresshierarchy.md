## Domain 'addresshierarchy'
The **addresshierarchy** subfolder contains configuration files expected by the Address Hierarchy module.

Below is a typical content of the 'addresshierarchy' configuration subfolder:
```bash
addresshierarchy/
  ├── addressConfiguration.xml
  ├── addresshierarchy.csv
  ├── messages_en.properties
  └── messages_fr.properties
```
###### File `addresshierarchy.csv` *(mandatory)*

It contains the address hierarchy entries themselves. This is typically a large file since it may contain thousands of lower level entries in a given country. Eg.:
```csv
Pays, Province, Commune
Haiti,Artibonite,Anse Rouge
Haiti,Artibonite,Desdunes
Haiti,Artibonite,Dessalines
...
Haiti,Sud-Est,Marigot
Haiti,Sud-Est,Thiotte
```
**NOTE:** This file may or may not contain a header row.

###### File `addressConfiguration.xml` *(mandatory)*
This file defines both:

1. The [address template](https://wiki.openmrs.org/x/OgTX) and
2. A mapping of the address template fields to the level of the address hierarchy entries that it can be populated with. This mapping occurs differently depending on whether the address level entries CSV file may or may not have a header row.

**Option 1: if the address level entries CSV file has a header row**, it maps the address template fields (`field`) with the address hierachy levels (`nameMapping`) defined as headers in `addresshierarchy.csv`. Here is an example how the address template field `STATE_PROVINCE` is mapped with the address hierarchy entries provided for `Province`:

```xml
<addressComponent>
  <field>STATE_PROVINCE</field>
  <nameMapping>Province</nameMapping>
  <sizeMapping>40</sizeMapping>
  <requiredInHierarchy>true</requiredInHierarchy>
</addressComponent>
```

**Option 2: if  the address level entries CSV file doesn't have a header row**, it maps the address template fields <u>in the order in which they are found in the address template</u>.

###### I18n `.properties` files *(optional)*
* The i18n `.properties` files are used to populate the address/location i18n cache. The messages files can be similarly defined as those loaded by the [messageproperties](readme/messageproperties.md) domain.

#### Requirements
* The [Address Hierarchy module](https://addons.openmrs.org/show/org.openmrs.module.addresshierarchy) version 2.17.0 or above must be installed.

### Further examples:
* Find out more how the Address Hierarchy module at https://wiki.openmrs.org/display/docs/Address+Hierarchy+Module
* Find out more about the address template at https://wiki.openmrs.org/x/OgTX
* A full configuration example at https://github.com/openmrs/ozone-distro-cambodia/tree/main/configs/openmrs_config/addresshierarchy
* Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).
