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
Initializer enables to achieve the OpenMRS equivalent of Bahmni Config. It facilitates the deployment of implementation-specific configurations without writing any code, by just filling the **configuration** folder with the needed metadata.

### Get in touch
Find us on [OpenMRS Talk](https://talk.openmrs.org/): sign up, start a conversation and ping us with the mentions starting with @mks.. in your message.

----

### Releases notes

#### Version 1.0
##### New features
* Loads i18n messages files from **configuration/addresshierarchy**.