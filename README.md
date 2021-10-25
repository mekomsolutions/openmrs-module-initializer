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
  ├── appointmentspecialities/
  ├── appointmentservicedefinitions/
  ├── appointmentservicetypes/
  ├── attributetypes/
  ├── autogenerationoptions/
  ├── bahmniforms/
  ├── conceptclasses/
  ├── conceptsources/
  ├── concepts/
  ├── datafiltermappings/
  ├── drugs/
  ├── encountertypes/
  ├── globalproperties/
  ├── htmlforms/
  ├── idgen/
  ├── jsonkeyvalues/
  ├── locations/
  ├── locationtagmaps/
  ├── locationtags/
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
  ├── providerroles/ 
  ├── relationshiptypes/
  └── roles/
   
```  
Each domain-specific subfolder contains OpenMRS metadata configuration files that pertains to the domain.

### Objectives
* This module loads an OpenMRS configuration consisting of OpenMRS metadata.
* CSV files are the preferred format, however a number of metadata domains rely on other file formats. See the list [below](#supported-domains-and-default-loading-order) for details.
* Initializer processes all configuration files upon starting up.
* Initializer produces a checksum file for each processed configuration file. A file will never be processed again until its checksum has changed.
  * See more info [here](readme/checksums.md) about checksums.
* Each line of those CSV files represents an **OpenMRS metadata entity to be created, edited or retired**.
* Each line of those CSV files follows the WYSIWYG principle.

### Supported domains and default loading order
We suggest to go through the following before looking at the specifics for each supported domain:
* [Conventions for CSV files](readme/csv_conventions.md)

This is the list of currently supported domains in their loading order:
1. [Localization Message Properties (.properties files)](readme/messageproperties.md)
1. [Generic JSON key-values (JSON files)](readme/jsonkeyvalues.md)
1. [Metadata Sharing Packages (ZIP files)](readme/mds.md)
1. [Visit Types (CSV files)](readme/visittypes.md)
1. [Patient Identifier Types (CSV files)](readme/pit.md)
1. [Relationship Types (CSV files)](readme/relationshiptypes.md)
1. [Location Tags (CSV files)](readme/loctags.md)
1. [Privileges (CSV files)](readme/priv.md)
1. [Encounter Types (CSV files)](readme/et.md)
1. [Encounter Roles (CSV files)](readme/encounterroles.md)
1. [Roles (CSV files)](readme/roles.md)
1. [Global Properties (XML files)](readme/globalproperties.md)
1. [Attribute Types (CSV files)](readme/atttypes.md)
1. [Provider Roles (CSV files)](readme/providerroles.md)
1. [Locations (CSV files)](readme/loc.md)
1. [Location Tag Maps (CSV files)](readme/loctagmaps.md)
1. [Bahmni Forms (JSON Files)](readme/bahmniforms.md)
1. [Concept Classes (CSV files)](readme/conceptclasses.md)
1. [Concept Sources (CSV files)](readme/conceptsources.md)
1. [Concepts (CSV files)](readme/concepts.md)
1. [Programs (CSV files)](readme/prog.md)
1. [Program Worklows (CSV files)](readme/prog.md)
1. [Program Worklow States (CSV files)](readme/prog.md)
1. [Person Attribute Types (CSV files)](readme/pat.md)
1. [Identifier Sources (CSV files)](readme/idgen.md)
1. [Autogeneration Options (CSV files)](readme/autogenerationoptions.md)
1. [Drugs (CSV files)](readme/drugs.md)
1. [Order Frequencies (CSV files)](readme/freqs.md)
1. [Order Types (CSV files)](readme/ordertypes.md)
1. [Bahmni Appointment Specialities (CSV files)](readme/appointmentspecialities.md)
1. [Bahmni Appointment Service Definitions (CSV files)](readme/appointmentservices.md#domain-appointmentservicedefinitions)
1. [Bahmni Appointment Service Types (CSV files)](readme/appointmentservices.md#domain-appointmentservicetypes)
1. [Data Filter Entity-Basis Mappings (CSV files)](readme/datafiltermappings.md)
1. [Metadata Sets (CSV files)](readme/mdm.md#domain-metadatasets)
1. [Metadata Set Members (CSV files)](readme/mdm.md#domain-metadatasetmembers)
1. [Metadata Term Mappings (CSV files)](readme/mdm.md#domain-metadatatermmappings)
1. [HTML Forms (XML files)](readme/htmlforms.md)

### How to try it out?
Build the master branch and install the built OMOD to your OpenMRS instance:
```bash
git clone https://github.com/mekomsolutions/openmrs-module-initializer/tree/master
cd openmrs-module-initializer
mvn clean package
```

##### Runtime requirements & compatibility
* OpenMRS Core 2.1.1 (*required*)
* HTML Form Entry 4.6.0 (*compatible*)
* ID Gen 4.3 (*compatible*)
* Metadata Sharing 1.2.2 (*compatible*)
* Metadata Mapping 1.3.4 (*compatible*)
* Bahmni Appointments 1.2-beta (*compatible*)
* Data Filter 1.0.0 (*compatible*)
* Bahmni I.e Apps 1.0.0 (*compatible*)
* Bahmni Core 0.93 (*compatible*)

### How to test out your OpenMRS configs?
See the [Initializer Validator README page](readme/validator.md).

### Finer control of domains loading at app runtime
See the [documentation on Initializer's runtime properties](readme/rtprops.md).

### Get in touch
* On [OpenMRS Talk](https://talk.openmrs.org/)
  * Sign up, start a conversation and ping us with the mention [`@MekomSolutions`](https://talk.openmrs.org/g/MekomSolutions) in your post. 
* On Slack:  
  * Join the [Initializer channel](https://openmrs.slack.com/archives/CPC20CBFH) and ping us with a `@Mekom` mention.

### Report an issue
https://github.com/mekomsolutions/openmrs-module-initializer/issues

----

### Releases notes

#### Version 2.3.0
* (_Enhancement_) Concept name UUIDs are univoquely seeded from 1) the concept UUID and 2) the concept name information, see [here](readme/concepts.md#implicit-handling-of-concept-names). This version runs a Liquibase changeset that forces a reload of the concept domain in order to update concept names accordingly.

#### Version 2.2.0
* 'attributetypes' domain to support Bahmni program attribute types.
* 'program' domain to support `Name` and `Description` headers.
* CSV parsers to actually fill _new_ objects marked to be retired or voided before creating them as retired/voided entities.
* Added a runtime property to define the loading startup mode for the activator OpenMRS config loading process.
* Existing attributes and location tags which are not specified in CSV headers are no longer removed.  
* Bulk creation and editing of concept sources provided as CSV files in **configuration/conceptsources**.
* Bulk creation and editing of encounter roles using CSV files in **configuration/encounterroles**.
* (_For devs._) Domain directory names and loading orders are implied from the base `Domain` enum.
* Bulk creation and editing of relationship types provided through CSV files in **configuration/relationshiptypes**.
* Bulk creation and editing of provider roles using CSV files in **configuration/providerroles**.
* Bulk creation and editing of location tag maps using CSV files in **configuration/locationtagmaps**.

#### Version 2.1.0
* (_Bug fix_) Locations with invalid parent references to throw an `IllegalArgumentException`.
* (_For devs._) Introduced `CsvFailingLines` for a better management of the outcome of `CsvParser#process`.
* Introduced safe and unsafe API modes to suit either app runtime loading or early failure loading for CI.
* (_For devs._) Introduced `BaseFileLoader` and `BaseInputStreamLoader` as part of a better streamlined loading framework.
* _Initialize Validator_ a standalone fatjar to make dry runs of OpenMRS configs.
* Nested structures of configuration files are supported.
* Added a runtime property to define an inclusion or exclusion list of domains.
* Added a runtime property to specify wildcard patterns filters for each domain.
* Added a runtime property to toggle off the generation of the checksums.
* Improved logging output with [ASCII Tables for Java](https://github.com/freva/ascii-table).
* Bulk creation and edition of ID Gen's autogeneration options provided through CSV files in **configuration/autogenerationoptions**.
* Support associating location tags to locations using boolean `Tag|` headers.
* Bulk creation and edition of location tags provided through CSV files in **configuration/locationtags**.
* Bulk creation and edition of Bahmni forms provided as JSON schema definitions in **configuration/bahmniforms**.
* Bulk creation and edition of htmlforms provided as XML schema definitions in **configuration/htmlforms**.
* Bulk creation and edition of Bahmni appointment service types provided through CSV files in **configuration/appointmentservicetypes**.
* Renaming domain: `appointmentsservicesdefinitions` → `appointmentservicedefinitions`.
* Renaming domain: `appointmentsspecialities` → `appointmentspecialities`.

#### Version 2.0.0
* (_For devs._) Support for conditional loading of domains based on the runtime availability of OpenMRS modules.
* Bulk creation and edition of programs provided through CSV files in **configuration/programs**.
* Bulk creation and edition of program workflows provided through CSV files in **configuration/programworkflows**.
* Bulk creation and edition of program workflow states provided through CSV files in **configuration/programworkflowstates**.
* Bulk creation and edition of privileges provided through CSV files in **configuration/privileges**.
* Bulk creation and edition of roles provided through CSV files in **configuration/roles**.
* Bulk creation and edition of metadata terms mappings provided through CSV files in **configuration/metadatatermmappings**.
* Bulk creation and edition of encounter types provided through CSV files in **configuration/encountertypes**.
* Bulk creation and edition of Bahmni appointments specialities provided through CSV files in **configuration/appointmentsspecialities**.
* Bulk creation and edition of Bahmni appointments services definitions provided through CSV files in **configuration/appointmentsservicesdefinitions**.
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
