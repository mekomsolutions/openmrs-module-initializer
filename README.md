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
  ├── appointmentsspecialities/
  ├── appointmentsservicesdefinitions/
  ├── attributetypes/
  ├── autogenerationoptions/
  ├── bahmniforms/
  ├── conceptclasses/
  ├── concepts/
  ├── datafiltermappings/
  ├── drugs/
  ├── encountertypes/
  ├── globalproperties/
  ├── idgen/
  ├── jsonkeyvalues/
  ├── locations/
  ├── messageproperties/
  ├── metadatasetmembers/ 
  ├── metadatasets/ 
  ├── metadatasharing/ 
  ├── metadatatermmappings/ 
  ├── patientidentifiertypes/ 
  ├── personattributetypes/ 
  ├── privileges/ 
  ├── programs/ 
  ├── programworkflows/
  ├── programworkflowstates/
  └── roles/
   
```  
Each domain-specific subfolder contains the metadata and configuration information that is relevant to the subfolder's domain. Although several file types are supported for providing metadata, CSV files are the preferred format and all domain should aim at being covered through parsing CSV files.

### Objectives
* This module allows to preload an OpenMRS installation with **maintained and versioned metadata**.
* CSV files are the preferred format, however a number of metadata domains rely on other file formats. See the list [below](#supported-domains-and-default-loading-order) for details.
* Initializer processes all configuration files upon starting up.
* Initializer produces a checksum file for each processed file. A file will never be processed again until its checksum has changed.
* Each line of those CSV files represents an **OpenMRS object to be created, edited or retired**.
* Each line of those CSV files follows the WYSIWYG principle.

### Supported domains and default loading order
We suggest to go through the following before looking at specific import domains:
* [Conventions for CSV files](readme/csv_conventions.md)

This is the list of currently supported domains in respect to their loading order:
1. [Message properties key-values (.properties files)](readme/messageproperties.md)
1. [Generic JSON key-values (JSON files)](readme/jsonkeyvalues.md)
1. [Metadata Sharing packages (ZIP files)](readme/mds.md)
1. [Patient identifier types (CSV files)](readme/pit.md)
1. [Privileges (CSV files)](readme/priv.md)
1. [Encounter Types (CSV files)](readme/et.md)
1. [Roles (CSV files)](readme/roles.md)
1. [Global properties (XML files)](readme/globalproperties.md)
1. [Attribute types (CSV files)](readme/atttypes.md)
1. [Locations (CSV files)](readme/loc.md)
1. [Bahmni Forms (JSON Files)](readme/bahmniforms.md)
1. [Concept classes (CSV files)](readme/conceptclasses.md)
1. [Concepts (CSV files)](readme/concepts.md)
1. [Programs (CSV files)](readme/prog.md)
1. [Programs worklows (CSV files)](readme/prog.md)
1. [Programs worklow states (CSV files)](readme/prog.md)
1. [Person attribute types (CSV files)](readme/pat.md)
1. [Identifier sources (CSV files)](readme/idgen.md)
1. [Auto Generation Options (CSV files)](readme/autogenerationoptions.md)
1. [Drugs (CSV files)](readme/drugs.md)
1. [Order Frequencies (CSV files)](readme/freqs.md)
1. [Order Types (CSV files)](readme/ordertypes.md)
1. [Bahmni Appointments Specialities (CSV files)](readme/appointmentsspecialities.md)
1. [Bahmni Appointments Service Definitions (CSV files)](readme/appointmentsservicesdefinitions.md)
1. [Data Filter entity-basis mappings (CSV files)](readme/datafiltermappings.md)
1. [Metadata Sets (CSV files)](readme/mdm.md#domain-metadatasets)
1. [Metadata Set Members (CSV files)](readme/mdm.md#domain-metadatasetmembers)
1. [Metadata Term Mappings (CSV files)](readme/mdm.md#domain-metadatatermmappings)

### How to try it out?
Build the master branch and install the built OMOD to your OpenMRS instance:
```bash
git clone https://github.com/mekomsolutions/openmrs-module-initializer/tree/master
cd openmrs-module-initializer
mvn clean package
```
##### Runtime requirements & compatibility
* OpenMRS Core 2.1.1 (*required*)
* ID Gen 4.3 (*compatible*)
* Metadata Sharing 1.2.2 (*compatible*)
* Metadata Mapping 1.3.4 (*compatible*)
* Bahmni Appointments 1.2-beta (*compatible*)
* Data Filter 1.0.0 (*compatible*)
* Bahmni I.e Apps 1.0.0 (*compatible*)

### Quick facts
Initializer enables to achieve the OpenMRS backend equivalent of Bahmni Config for Bahmni Apps. It facilitates the deployment of implementation-specific configurations without writing any code, by just filling the **configuration** folder with the needed metadata and in accordance to Initializer's available implementations.

### Get in touch
Find us on [OpenMRS Talk](https://talk.openmrs.org/): sign up, start a conversation and ping us with the mention `@MekomSolutions` in your message. Or find us on the [Initializer OpenMRS Slack channel](https://openmrs.slack.com/archives/CPC20CBFH).

----

### Releases notes

#### Version 2.1.0
* Bulk creation and edition of ID Gen's autogeneration options provided through CSV files in **configuration/autogenerationoptions**.

#### Version 2.0.0
* Support for conditional loading of domains based on the runtime availability of OpenMRS modules.
* Bulk creation and edition of programs provided through CSV files in **configuration/programs**.
* Bulk creation and edition of program workflows provided through CSV files in **configuration/programworkflows**.
* Bulk creation and edition of program workflow states provided through CSV files in **configuration/programworkflowstates**.
* Bulk creation and edition of privileges provided through CSV files in **configuration/privileges**.
* Bulk creation and edition of roles provided through CSV files in **configuration/roles**.
* Bulk creation and edition of metadata terms mappings provided through CSV files in **configuration/metadatatermmappings**.
* Bulk creation and edition of encounter types provided through CSV files in **configuration/encountertypes**.
* Bulk creation and edition of Bahmni appointments specialities provided through CSV files in **configuration/appointmentsspecialities**.
* Bulk access management of Data Filter entity to basis mappings provided through CSV files in **configuration/datafiltermappings**.
* Bulk creation and edition of attribute types provided through CSV files in **configuration/attributetypes**.
* Support location attributes.
* Bulk creation and edition of patient identifier types provided through CSV files in **configuration/patientidentifiertypes**.
* Bulk creation and edition of metadata terms mappings provided through CSV files in **configuration/metadatasets**.
* Bulk creation and edition of metadata terms mappings provided through CSV files in **configuration/metadatasetmembers**.
* Bulk creation and edition of bahmni forms provided JSON files in **configuration/bahmniforms**
* Support concept attributes.

#### Version 1.1.0
* Bulk creation and edition of drugs provided through CSV files in **configuration/locations**.

#### Version 1.0.1
* Loads i18n messages files from **configuration/addresshierarchy** and **configuration/messageproperties**.
* Bulk creation and edition of concepts provided through CSV files in **configuration/concepts**.<br/>This covers: basic concepts, concepts with nested members or answers and concepts with multiple mappings.
* Bulk creation and edition of drugs provided through CSV files in **configuration/drugs**.
* Overrides global properties provided through XML configuration files in **configuration/globalproperties**.
* Modifies (retire) or create ID Gen's identifier sources through CSV files in **configuration/idgen**.
* Exposes runtime key-values configuration parameters through JSON files in **configuration/jsonkeyvalues**.
* Bulk creation and edition of person attribute types provided through CSV files in **configuration/personattributetypes**.
* Imports MDS packages provided as .zip files in **configuration/metadatasharing**.
* Bulk creation and edition of order frequencies provided through CSV files in **configuration/orderfrequencies**.
