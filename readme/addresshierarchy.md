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
###### Address Entries CSV File *(mandatory)*

This file is usually named `addresshierarchy.csv`, however this name is configurable.

It contains the address hierarchy entries themselves. This is typically a large file since it usually contains thousands of lower level entries. Here is an sample snippet for Haiti with the entries down to the third _commune_ level. We see on each row `Pays,Département,Commune` (country, district, borough):
```csv
Haiti,Artibonite,Anse Rouge
Haiti,Artibonite,Desdunes
Haiti,Artibonite,Dessalines
...
Haiti,Sud-Est,Marigot
Haiti,Sud-Est,Thiotte
```
**NOTE:** This file provides raw entries, without a header row.

The entries levels will be mapped, following their order of enumeration in the file, to the fields in the address template, see below.

###### File `addressConfiguration.xml` *(mandatory)*
This file defines the [address template](https://wiki.openmrs.org/x/OgTX). Example, for Haiti:
```xml
<addressConfiguration>
  <wipe>true</wipe>
  <addressComponents>
    <addressComponent>
      <field>COUNTRY</field>
      ...
    </addressComponent>
    <addressComponent>
      <field>STATE_PROVINCE</field>
      ...
    </addressComponent>
    <addressComponent>
      <field>CITY_VILLAGE</field>
      ...
    </addressComponent>
    <addressComponent>
      <field>ADDRESS_2</field>
      ...
    </addressComponent>
  </addressComponents>
  <addressHierarchyFile>
    <filename>addresshierarchy.csv</filename>
    <entryDelimiter>,</entryDelimiter>
    ...
  </addressHierarchyFile>
</addressConfiguration>```
```
In this Haiti example, the configuration loader will map address field and hierarchy levels as such because of the enumeration order in each of the two files:

| Address field    | Hierarchy level |
|------------------|-----------------|
| `COUNTRY`        | `Pays`          |
| `STATE_PROVINCE` | `Département`   |
| `CITY_VILLAGE`   | `Commune`       |
| `ADDRESS_2`      | -               |

###### I18n `.properties` files *(optional)*
* The i18n `.properties` files are used to populate the address/location i18n cache. The messages files can be similarly defined as those loaded by the [messageproperties](readme/messageproperties.md) domain. The message properties are usable as actual Location or Address names. 

For example, typical Location lines  could look something like;

```
 ______________________________________ _____________ ___________________________ ___________________________ _______________________ _________________ _____
| Uuid                                 | Void/Retire | Name                      | Parent                    | Tag|Facility Location | Address 1       | ... |
|--------------------------------------|-------------|---------------------------|---------------------------|-----------------------|-----------------|-----|
| a03e395c-b881-49b7-b6fc-983f6bddc7fc | false       | addresshierarchy.cambodia |                           | TRUE                  | Paradise Street | ... |
| cbaaaab4-d960-4ae9-9b6a-8983fbd947b6 | true        | addresshierarchy.meanchey | addresshierarchy.cambodia |                       |                 | ... |
 -------------------------------------- ------------- --------------------------- --------------------------- ----------------------- ----------------- -----
```
And typical Address Hierarchy entries could look something like;

```
 ___________________________ ___________________________ _____
| addresshierarchy.cambodia | addresshierarchy.meanchey | ... |
| addresshierarchy.cambodia | addresshierarchy.banteay  | ... |
 --------------------------- --------------------------- -----
```
Where the `addresshierarchy.cambodia`, `addresshierarchy.meanchey` and `addresshierarchy.banteay` become candidates for the `.properties` files to allow for name localization.

#### Requirements
* The [Address Hierarchy module](https://addons.openmrs.org/show/org.openmrs.module.addresshierarchy) version 2.17.0 or above must be installed.

### Further examples:
* Find out more how the Address Hierarchy module at https://wiki.openmrs.org/display/docs/Address+Hierarchy+Module
* Find out more about the address template at https://wiki.openmrs.org/x/OgTX
* A full configuration example for Haiti at https://github.com/mekomsolutions/openmrs-config-haiti/tree/master/configuration/addresshierarchy
* Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).
