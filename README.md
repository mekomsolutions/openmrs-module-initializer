# OpenMRS Initializer module
The Initializer module is an API-only module that processes the content of the **configuration** folder when it is found inside OpenMRS' application data directory:
<pre>
  .
   \_ modules/
   \_ openmrs.war
   \_ openmrs-runtime.properties
   \_ ...
   \_ <b>configuration/</b>
</pre>
The configuration folder is subdivided into 'domain specific' subfolders:
```
  configuration/
    \_ addresshierarchy/
    \_ concepts/
    \_ idgen/
    \_ globalproperties/
    \_ metadatasharing/
```  
Each domain-specific subfolder contains the metadata and configuration information that is relevant to the subfolder's domain.
Please see below for details about each supported domain:

---

#### Domain 'addresshierarchy'
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

---

#### Domain 'concepts'
The **concept** subfolder contains CSV import files for saving concepts in bulk. This is a possible example of its content:
```
  concepts/
    \_ diagnoses.csv
    \_ findings.csv
    \_ misc.csv
    \_ ...
```
The way those CSV files are processed is controlled by a reserved part of the CSV file header line that holds metadata about the CSV file itself. Here is an example of a header line:

| <sub>Uuid</sub> | <sub>Fully specified name:en</sub> | <sub>Short name:en</sub> | <sub>Description:en</sub> | ... | <sub>_version:base</sub> | <sub>_order:1000</sub> |
| - | - | - | - | - | - | - |

Some headers start with an underscore such as `_version:base`, indicating that they are metadata headers. The values in the columns under those headers are never read by the CSV parser.
<br/>Let's review some important headers.

##### Header `Uuid`
If the value under this header is missing, the concept will be created with a newly generated UUID.
If the value under this header is provided, the initializer will attempt to retrieve any existing concept that may already exist with this UUID. And if the concept already exists, it will be modified and resaved according to the CSV line. If the concept doesn't exist, then a new concept will be created with the UUID specified on the CSV line.

##### Headers `Fully specified name`, `Short name` and `Description`
Those are locale specific headers, they are never used as such because they always need to indicate the locale for the values in their column.
For a column to contain short names in English (locale 'en') name the header `Short name:en`. The same logic applies for the other locale specific headers.

##### Header `_version:*`
###### Version `_version:base`
This guides the CSV parser to use the _base_ line processor. Here is an example of valid base concepts definitions:

| <sub>Uuid</sub>  | <sub>Fully specified name:en</sub> | <sub>Short name:en</sub> | <sub>Description:en</sub> | <sub>Data class</sub>  | <sub>Data type</sub> |
| - | - | - | - | - | - |
| | <sub>Nationality</sub> | <sub>Nat.</sub> | <sub>The status of belonging to a particular nation.</sub> | <sub>Question</sub> | <sub>Text</sub> |
| <sub>db2f4fc4-..</sub>| <sub>Language</sub> | <sub>Lang.</sub> | <sub>The method of human communication.</sub> | <sub>Question</sub> | <sub>Text</sub> |
###### Version `_version:nested`
This guides the CSV parser to use the _nested_ line processor that adds new columns to the base line processor to specify lists of concept answers or lists of concept members.

Here is an example of the additional columns that are processed:

| ... | <sub>Answers</sub> | <sub>Members</sub> | ... |
| - | - | - | - |
| ... | <sub>CONCEPT_NAME; source:134; db2f4fc4-..</sub> | | ... |
| ... | | <sub>CONCEPT_NAME; source:134; db2f4fc4-..</sub> | ... |

As the example suggests, it is possible to provide lists of concepts identifiers to fill the values of the columns 'answers' or 'members' under the form of concept names (eg. "CONCEPT_NAME"), concept mappings (eg. "source:134") and concept UUIDs (eg. "db2f4fc4-.."). The concepts that could not be fetched through their provided identifier will fail the creation of the concept from the CSV line altogether, and the parser will jump to the next CSV line.
<br/> Finally concept CSV lines that provide a list of set members are automatically considered to be concept _sets_.

**NOTE** In the current implementation the listing order of the concepts in the CSV file does matter since unexisting concepts will fail the CSV line processing. It is recommended to take this into account and to insert CSV lines for concepts with nested lists low enough in the CSV file so that all nested concepts are found when the CSV line is being processed.

###### Version `_version:mappings`
This guides the CSV parser to use the _mappings_ line processor that adds the column 'same as mappings' to the base line processor to specify lists of concept mappings following the format `source:code` (eg. "CIEL:1234").

Here is an example of the additional columns that are processed:

| ... | <sub>Same as concept mappings</sub> | ... |
| - | - | - |
| ... | <sub>ICD-10-WHO:T45.9; CIEL:122226; Cambodia:115</sub> | ... |

###### Version `_version:nested_mappings`

This version adds both the nested _and_ the mappings line processors to the base line processor.

##### Header `_order:*`
This metadata header specifies the order of loading of the CSV file. In many cases the creation of concepts relies on the existence of other concepts, and this is the use case that is covered by this metadata header. For example `_order:1000` indicates that all CSV files with an order smaller than 1,000 will be processed _before_ this file.
<br/> If the order metadata cannot be parsed or is missing, then the file will be processed _after_ all the ordered CSV files. However if several CSV files have no order defined, then the loading order between them is undefined. 

---

#### Domain 'idgen'
The **idgen** subfolder contains XML configuration files that help modify and create identifier sources. It should be possible in most cases to configure them via a single XML configuration file, however there can be as many XML files as desired.
This is a possible example of how the configuration subfolder may look like:
```
  idgen/
    \_ idgen.xml
```
The XML configuration allows to either modify exisiting identifier sources or to create new identifier sources. At the moment the only possible modification allowed is to _retire_ exisiting sources.

###### Idgen XML configuration file example:
```xml
<config>
  <identifierSources>
    <identifierSource>
      <uuid>c1d8a345-3f10-11e4-adec-0800271c1b75</uuid>
      <retired>true</retired>
    </identifierSource>
    <identifierSource>
      <uuid>c1d90956-3f10-11e4-adec-0800271c1b75</uuid>
      <retired>true</retired>
    </identifierSource>
    <sequentialIdentifierGenerator>
      <name>Autogenerated IDs source</name>
      <description>Source used for the autogeneration of OpenMRS IDs.</description>
      <identifierType>
        <name>PATIENTIDENTIFIERTYPE_1_OPENMRS_ID</name>
      </identifierType>
      <firstIdentifierBase>001000</firstIdentifierBase>
      <minLength>7</minLength>
      <maxLength>7</maxLength>
      <baseCharacterSet>0123456789</baseCharacterSet>
    </sequentialIdentifierGenerator>
  </identifierSources>
</config>
```
The above XML configuration will retire the identifier sources whose UUIDs are `c1d8a345-3f10-11e4-adec-0800271c1b75` and `c1d90956-3f10-11e4-adec-0800271c1b75` ; and will create a new `SequentialIdentifierGenerator` with the specified properties. When creating a new identifier source, pay special attention to the way it is linked to its `PatientIdentifierType`. This is done through the _name_ of the patient identifier type (that must be unique in OpenMRS.)

---

#### Domain 'globalproperties'
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

---

#### Domain 'metadatasharing'
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
* Bulk creation and saving of concepts provided through CSV files in  **configuration/concepts**.
* Overrides global properties provided through XML configuration files in **configuration/globalproperties**.
* Modifies (retire) or create identifier sources as specified in  **configuration/idgen**.
* Imports MDS packages provided as .zip files in **configuration/metadatasharing**.