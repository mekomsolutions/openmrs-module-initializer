# OpenMRS Initializer module
### Introduction
The Initializer module is an API-only module that processes the content of the **configuration** folder when it is found inside OpenMRS' application data directory:

<pre>
.
├── modules/
├── openmrs.war
├── openmrs-runtime.properties
├── ...
└── <b>configuration/</b>
</pre>
The configuration folder is subdivided into _domain_ subfolders:
```bash
configuration/
  ├── addresshierarchy/
  ├── concepts/
  ├── drugs/
  ├── globalproperties/
  ├── idgen/
  ├── jsonkeyvalues/
  ├── locations/
  ├── messageproperties/
  ├── metadatamappings/ 
  ├── metadatasharing/ 
  ├── personattributetypes/ 
  ├── privileges/ 
  ├── programs/ 
  ├── programworkflows/
  ├── programworkflowstates/
  └── roles/
```  
Each domain-specific subfolder contains the metadata and configuration information that is relevant to the subfolder's domain. Although several file types are supported for providing metadata, CSV files are the preferred format and all domain should aim at being covered through parsing CSV files.

### Objectives
* This module allows to preload an OpenMRS installation with **maintained and versioned data and metadata**.
* Initializer processes **CSV files** upon startup.
* Each line of those CSV files represents an **OpenMRS object to be created or edited**.

Even though using CSV files is the preferred approach, some data or metadata domains rely on other file formats to be imported. You will encounter those other formats in the list below.

### Supported domains and loading order
We suggest to go through the following before looking at specific import domains:
* [Conventions for CSV files](readme/csv_conventions.md)

This is the list of currently supported domains in respect to their loading order:
1. [Message properties key-values (.properties files)](readme/messageproperties.md)
1. [Generic JSON key-values (JSON files)](readme/jsonkeyvalues.md)
1. [Metadata Sharing packages (ZIP files)](readme/mds.md)
1. [Metadata Mappings (CSV files)](readme/mdm.md)
1. [Privileges (CSV files)](readme/priv.md)
1. [Roles (CSV files)](readme/roles.md)
1. [Global properties (XML files)](readme/globalproperties.md)
1. [Locations (CSV files)](readme/loc.md)
1. [Concepts (CSV files)](readme/concepts.md)
1. [Programs (CSV files)](readme/prog.md)
1. [Programs worklows (CSV files)](readme/prog.md)
1. [Programs worklow states (CSV files)](readme/prog.md)
1. [Person attribute types (CSV files)](readme/pat.md)
1. [Identifier sources (CSV files)](readme/idgen.md)
1. [Drugs (CSV files)](readme/drugs.md)
1. [Order Frequencies(CSV files)](readme/freqs.md)

### How to try it out?
Build the master branch and install the built OMOD to your OpenMRS instance:
```bash
git clone https://github.com/mekomsolutions/openmrs-module-initializer/tree/master
cd openmrs-module-initializer
mvn clean install
```
##### Runtime requirements & compatibility
* Core 1.11.9

### Quick facts
Initializer enables to achieve the OpenMRS backend equivalent of Bahmni Config for Bahmni Apps. It facilitates the deployment of implementation-specific configurations without writing any code, by just filling the **configuration** folder with the needed metadata and in accordance to Initializer's available implementations.

### Get in touch
Find us on [OpenMRS Talk](https://talk.openmrs.org/): sign up, start a conversation and ping us with the mentions starting with @mks.. in your message.

----

### Releases notes

#### Version 1.2.0
* Support for conditional loading of domains based on the runtime availability of OpenMRS modules.
* Bulk creation and edition of programs provided through CSV files in  **configuration/programs**.
* Bulk creation and edition of program workflows provided through CSV files in  **configuration/programworkflows**.
* Bulk creation and edition of program workflow states provided through CSV files in  **configuration/programworkflowstates**.
* Bulk creation and edition of privileges provided through CSV files in  **configuration/privileges**.
* Bulk creation and edition of roles provided through CSV files in  **configuration/roles**.
* Bulk creation and edition of metadata terms mappings provided through CSV files in  **configuration/metadatamappings**.

#### Version 1.1.0
* Bulk creation and edition of drugs provided through CSV files in  **configuration/locations**.

#### Version 1.0.1
* Loads i18n messages files from **configuration/addresshierarchy** and **configuration/messageproperties**.
* Bulk creation and edition of concepts provided through CSV files in  **configuration/concepts**.<br/>This covers: basic concepts, concepts with nested members or answers and concepts with multiple mappings.
* Bulk creation and edition of drugs provided through CSV files in  **configuration/drugs**.
* Overrides global properties provided through XML configuration files in **configuration/globalproperties**.
* Modifies (retire) or create identifier sources through CSV files in **configuration/idgen**.
* Exposes runtime key-values configuration parameters through JSON files in **configuration/jsonkeyvalues**.
* Bulk creation and edition of person attribute types provided through CSV files in  **configuration/personattributetypes**.
* Imports MDS packages provided as .zip files in **configuration/metadatasharing**.
* Bulk creation and edition of order frequencies provided through CSV files in  **configuration/orderfrequencies**.