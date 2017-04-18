# OpenMRS Initializer module
The Initializer module is an API-only module that processes the content of the **configuration** folder when it is found inside OpenMRS' application data directory:
```
  .
   \_ modules/
   \_ openmrs.war
   \_ openmrs-runtime.properties
   \_ ...
   \_ configuration/
```
The configuration folder is subdivided into 'domain specific' subfolders:
```
  configuration/
    \_ addresshierarchy/
    \_ globalproperties/
    \_ metadatasharing/
    \_ ...
```  
Each domain-specific subfolder contains the metadata and configuration information that is relevant to the subfolder's domain.
Please see below for details about each supported domain:

#### 'addresshierarchy' subfolder
The **addresshierarchy** subfolder contains all the address hierarchy metadata. This is a possible example of its content:
```
  addresshierarchy/
    \_ addressConfiguration.xml
    \_ addresshierarchy.csv
    \_ addresshierarchy_en.properties
    \_ addresshierarchy_km_KH.properties
```
This is a mixed scenario since the Address Hierarchy module's activator itself can handle most of the provided configuration and metadata: **addressConfiguration.xml** (the actual configuration file) and **addresshierarchy.csv** (the CSV import file containing all address hierarchy geographies.)
The Initializer module will take care of loading the address hierarchy entries translations for use cases where the Address Hierarchy module must support i18n.

#### 'globalproperties' subfolder
The **globalproperties** subfolder contains XML configuration files that specify which global properties to override. Note that existing global properties will be overridden and missing ones will be created.
This is a possible example of how the configuration subfolder may look like:
```
  globalproperties/
    \_ gp_core.xml
    \_ gp_coreapps.xml
    \_ ...
```
There can be as many XML files as desired. One may be enough in most cases, but providing multiples files is also a possibility if the implementation requires to manage them by modules, areas or categories. Beware that the behaviour will be undefined iif a global property is overridden in several places. 

###### Global properties XML configuration file example:
```xml
<config>
  <globalProperties>
    <globalProperty>
      <property>addresshierarchy.i18nSupport</property>
      <value>true</value>
    </globalProperty>
    <globalProperty>
      <property>locale.allowed.list</property>
      <value>en, km_KH</value>
    </globalProperty>
  </globalProperties>
</config>
```
The above XML configuration will set **addresshierarchy.i18nSupport** to `true` and **locale.allowed.list** to `"en, km_KH"`.

#### 'metadatasharing' subfolder
The **metadatasharing** subfolder contains all the Metadata Sharing (MDS) packages as .zip files to be imported. This is a possible example of its content:
```
  metadatasharing/
    \_ PatientIdentifierType.zip
    \_ PersonAttributeType.zip
    \_ ...
```
There can be as many MDS packages as desired. Providing multiples .zip files allows to split the metadata to be imported by areas, categories or any other segmentation that the implementors deem relevant.
<br/>They will all be imported following the 'prefer theirs' rule, meaning that the the metadata shipped with the packages is considered being the master metadata. Existing objects will be overwritten, missing objects will be created... etc.
<br/>MDS packages are a convenient way to bring in metadata, especially while other methods have not yet been implemented. However when otherwise possible, other ways should be preferred.

### How to try it out?
Build the master branch and install the built OMOD to your OpenMRS instance:
```
git clone https://github.com/mekomsolutions/openmrs-module-initializer/tree/master
cd openmrs-module-initializer
mvn clean install
```
##### Runtime requirements & compatibility
* Core 1.11.8

### Quick facts
Initializer enables to achieve the OpenMRS backend equivalent of Bahmni Config for Bahmni Apps. It facilitates the deployment of implementation-specific configurations without writing any code, by just filling the **configuration** folder with the needed metadata and in accordance to Initializer's implementation.

### Get in touch
Find us on [OpenMRS Talk](https://talk.openmrs.org/): sign up, start a conversation and ping us with the mentions starting with @mks.. in your message.

----

### Releases notes

#### Version 1.0
##### New features
* Loads i18n messages files from **configuration/addresshierarchy**.
* Overrides global properties provided through XML configuration files in **configuration/globalproperties**.
* Imports MDS packages provided as .zip files in **configuration/metadatasharing**.