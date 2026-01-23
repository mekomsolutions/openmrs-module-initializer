# OpenMRS Initializer module

- [Introduction](#introduction)
- [Goals](#goals)
- [Supported domains and default loading order](#supported-domains-and-default-loading-order)
- [Try it out](#try-it-out)
  * [Runtime compatibility](#runtime-compatibility)
  * [Test your OpenMRS configs](#test-your-openmrs-configs)
  * [Finer control of domains loading at runtime](#finer-control-of-domains-loading-at-runtime)
  * [Setting up and controlling logging](#setting-up-and-controlling-logging)
- [Get in touch](#get-in-touch)
- [Releases notes](#releases-notes)
    + [Version 2.10.0](#version-2100)
    + [Version 2.9.0](#version-290)
    + [Version 2.8.0](#version-280) 
    + [Version 2.7.0](#version-270)
    + [Version 2.6.0](#version-260)
    + [Version 2.5.2](#version-252)
    + [Version 2.5.1](#version-251)
    + [Version 2.5.0](#version-250)
    + [Version 2.4.0](#version-240)
    + [Version 2.3.0](#version-230)
    + [Version 2.2.0](#version-220)
    + [Version 2.1.0](#version-210)
    + [Version 2.0.0](#version-200)
    + [Version 1.1.0](#version-110)
    + [Version 1.0.1](#version-101)

<small><i><a href='http://ecotrust-canada.github.io/markdown-toc/'>(Table of contents generated with markdown-toc)</a></i></small>

## Introduction
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
  ├── ampathforms/
  ├── ampathformstranslations/
  ├── appointmentspecialities/
  ├── appointmentservicedefinitions/
  ├── appointmentservicetypes/
  ├── attributetypes/
  ├── autogenerationoptions/
  ├── bahmniforms/
  ├── billableservices/
  ├── cashpoints/
  ├── cohorttypes/
  ├── cohortattributetypes/
  ├── conceptclasses/
  ├── conceptsources/
  ├── concepts/
  ├── conceptsets/
  ├── conceptreferencerange/
  ├── datafiltermappings/
  ├── dispositions/
  ├── drugs/
  ├── encountertypes/
  ├── encounterroles/
  ├── fhirconceptsources/
  ├── fhirpatientidentifiersystems/
  ├── globalproperties/
  ├── htmlforms/
  ├── idgen/
  ├── jsonkeyvalues/
  ├── liquibase/
  ├── locations/
  ├── locationtagmaps/
  ├── locationtags/
  ├── messageproperties/
  ├── metadatasetmembers/ 
  ├── metadatasets/ 
  ├── metadatasharing/ 
  ├── metadatatermmappings/
  ├── ocl/
  ├── orderfrequencies/
  ├── ordertypes/
  ├── paymentmodes/
  ├── patientidentifiertypes/ 
  ├── personattributetypes/ 
  ├── privileges/ 
  ├── programs/ 
  ├── programworkflows/
  ├── programworkflowstates/
  ├── providerroles/
  ├── queues/
  ├── relationshiptypes/
  └── roles/
   
```  
Each domain-specific subfolder contains OpenMRS metadata configuration files that pertains to the domain.

## Goals
* This module loads an OpenMRS configuration consisting of OpenMRS metadata.
* CSV files are the preferred format, however a number of metadata domains rely on other file formats. See the list [below](#supported-domains-and-default-loading-order) for details.
* Initializer processes all configuration files upon starting up.
* Initializer produces a checksum file for each processed configuration file. A file will never be processed again until its checksum has changed.
  * See more info [here](readme/checksums.md) about checksums.
* Each line of those CSV files represents an **OpenMRS metadata entity to be created, edited or retired**.
* Each line of those CSV files follows the WYSIWYG principle.

## Supported domains and default loading order
We suggest to go through the following before looking at the specifics for each supported domain:
* [Conventions for CSV files](readme/csv_conventions.md)

This is the list of currently supported domains in their loading order:
1. [Liquibase Changelog (XML file)](readme/liquibase.md)
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
1. [Address Hierarchy (XML, CSV, .properties files)](readme/addresshierarchy.md)
1. [Bahmni Forms (JSON Files)](readme/bahmniforms.md)
1. [Concept Classes (CSV files)](readme/conceptclasses.md)
1. [Concept Sources (CSV files)](readme/conceptsources.md)
1. [Open Concept Lab (ZIP Files)](readme/ocl.md)
1. [Concepts (CSV files)](readme/concepts.md)
1. [Billable Services (CSV files)](readme/billableservices.md)
1. [Cash Points (CSV files)](readme/cashpoints.md)
1. [Payment Modes (CSV files)](readme/paymentmodes.md)
1. [Concept Sets and Answers (CSV files)](readme/conceptsets.md)
1. [Concept Reference Ranges (CSV files)](readme/conceptreferencerange.md)
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
1. [Queues (CSV files)](readme/queues.md#domain-queues)
1. [Data Filter Entity-Basis Mappings (CSV files)](readme/datafiltermappings.md)
1. [Metadata Sets (CSV files)](readme/mdm.md#domain-metadatasets)
1. [Metadata Set Members (CSV files)](readme/mdm.md#domain-metadatasetmembers)
1. [Metadata Term Mappings (CSV files)](readme/mdm.md#domain-metadatatermmappings)
1. [Cohort Types (CSV files)](readme/cohort.md#domain-cohorttypes)
1. [Cohort Attribute Types (CSV files)](readme/cohort.md#domain-cohortattributetypes)
1. [FHIR Concept Sources (CSV files)](readme/fhir.md#domain-fhirconceptsources)
1. [FHIR Patient Identifier Systems (CSV Files)](readme/fhir.md#domain-fhirpatientidentifiersystems)
1. [AMPATH Forms (JSON files)](readme/ampathforms.md)
1. [AMPATH Forms Translations (JSON files)](readme/ampathformstranslations.md)
1. [HTML Forms (XML files)](readme/htmlforms.md)
1. [Disposition Config (json file)](readme/dispositions.md)

## Try it out
Build the master branch and install the built OMOD to your OpenMRS instance:
```bash
git clone https://github.com/mekomsolutions/openmrs-module-initializer/tree/master
cd openmrs-module-initializer
mvn clean package
```

### Runtime compatibility
* OpenMRS Core 2.1.1 (*required*)
* Bahmni Appointments 1.2.1 (*compatible*)
* Bahmni Core 0.93 (*compatible*)
* Bahmni I.e Apps 1.1.0 (*compatible*)
* Billing 1.1.0 (*compatible*)
* Data Filter 1.0.0 (*compatible*)
* HTML Form Entry 4.0.0 (*compatible*)
* ID Gen 4.3 (*compatible*)
* Metadata Sharing 1.2.2 (*compatible*)
* Metadata Mapping 1.3.4 (*compatible*)
* Open Concept Lab 1.2.9 (*compatible*)

### Test your OpenMRS configs
See the [Initializer Validator README page](readme/validator.md).

### Finer control of domains loading at runtime
See the [documentation on Initializer's runtime properties](readme/rtprops.md).

### Setting up and controlling logging
See the [documentation on Initializer's logging properties](readme/rtprops.md#logging-properties).

## Helpful Resources
* UUID Generator website: [uuidgenerator.net](https://www.uuidgenerator.net)
* YouTube Tutorial on Initializer Config File Basics: [English version](https://youtu.be/Nerhs2ANq98), [French version](https://youtu.be/olzM4NOmyNk). Specifically this video will walk through real example config files for locations, drugs, forms, and form translations, with live-time editing shown. We also explain *why* and *how* to use config files, and we look at several other example demo files for reference.

  <a href="https://youtu.be/Nerhs2ANq98">
  <img src="https://github.com/user-attachments/assets/8dfa3627-2cd1-42c6-a391-a7e024c6a362" alt="Video Tutorial" width="560" height="325">
</a>

## Get in touch
* On [OpenMRS Talk](https://talk.openmrs.org/)
  * Sign up, start a conversation and ping us with the mention [`@MekomSolutions`](https://talk.openmrs.org/g/MekomSolutions) in your post. 
* On Slack:  
  * Join the [Initializer channel](https://openmrs.slack.com/archives/CPC20CBFH) and ping us with a `@Mekom` mention.
* Report an issue:
  * https://github.com/mekomsolutions/openmrs-module-initializer/issues

----

## Releases notes
#### Version 2.11.0
* Support for 'billing' (billableservices, paymentmodes, cashpoints) for Billing V2
* Removed support for Billing V1 (1.x) versions

#### Version 2.10.0
* Support enhanced methods for loading htmlforms when running htmlformentry 5.5.0+
* Support loading drug ingredients within the drug domain, for compatible OpenMRS versions.
* Initializer message source should not throw an Exception if locale is not found for resource.
* Moved the form name translation loader to the preload function to support loading form names on instance restarts.
* Added video tutorial to the README covering the fundamentals of modifying and managing Iniz configuration files.

#### Version 2.9.0
* Fix for InitializerSerializer to ensure compatibility with OpenMRS version 2.7.0+
* Fix for processing attributes to ensure compatibility with OpenMRS version 2.7.0+
* Fix collisions between unit tests

#### Version 2.8.0
* Ampath forms translation files will now generate checksums.
* Enhancement to ensure that when an Ampath forms file is loaded, a new resource with the existing Ampath forms translations is created.
* Added support for 'billing' (billableservices, paymentmodes, cashpoints) domains.
* Added support for 'conceptreferencerange' domain.

#### Version 2.7.0
* Added support for 'queues' domain.
* Added support for 'addresshierarchy' domain.
* Fix for Liquibase Loader to ensure compatibility with OpenMRS versions 2.5.5+.
* Fix for OCL Loader to ensure it throws an Exception if the OCL import fails.
* Fix for Validator to not encounter failure upon repeated execution on the same JVM process.
* Fix for null config directory path in `DeleteDomainChecksumsChangeset`.

#### Version 2.6.0
* Added support for 'cohorttypes' and 'cohortattributetypes' domains.

#### Version 2.5.2
* Updated versions of Validator's metadatamapping to 1.6.0 and metadatasharing to 1.9.0.

#### Version 2.5.1
* Enhanced Validator to prevent random failures because of `LazyInitializationException`, and better teardown cleanup for uses with Maven plugins such as [openmrs-packager-maven-plugin](https://github.com/openmrs/openmrs-contrib-packager-maven-plugin) 1.7.0.

#### Version 2.5.0
* Added support for AMPATH Forms translations, see https://github.com/mekomsolutions/openmrs-module-initializer/issues/180 and https://github.com/mekomsolutions/openmrs-module-initializer/issues/221
* Fix for Message Source when system default language is not English, see https://github.com/mekomsolutions/openmrs-module-initializer/issues/212
* Logging now uses the configured level as a minimum.
* Added support for [drug reference maps](https://github.com/mekomsolutions/openmrs-module-initializer/issues/219) on the drugs domain.

#### Version 2.4.0
* Added support for 'fhirconceptsources' domain.
* Added support for 'fhirpatientidentifiersystems' domain.
* Enhancement to ensure that reloading Concept CSVs does not clear Members/Answers if those columns aren't part of CSV file.
* 'concepts' domain to support a new expandable `MAPPINGS` header, thereby discouraging the older `Same as mappings`.
* Concept references expanded to allow use of concept names in locales other than the default system locale.

#### Version 2.3.0
* Added configuration options for logging.
* Added support for OpenMRS 2.4.0+
* Added support for a 'liquibase' domain to support loading custom changesets.
* 'ocl' domain to support loading concepts, concept sets and answers from OCL export files in **configuration/ocl**.
* (_Enhancement_) Concept name UUIDs are seeded from 1) the concept UUID and 2) the concept name information, see [here](readme/concepts.md#implicit-handling-of-concept-names). This version runs a Liquibase changeset that forces a reload of the concept domain in order to update concept names accordingly.
* Backward-compatible overhaul of the concepts domain that provides full flexibility for managing concept names.
* Bulk loading of metadata entities i18n display messages specified under `display:xy` headers.
* (_For devs._) Introduced a pre-loading mechanism to `BaseFileLoader` that allows checksums-independent loading of transient information out of the config files before the actual metadata are loaded. Each loader controls whether its pre-loader throws on error or is allowed to fail. By default pre-loaders are allowed to fail.
* Added support for setting the version property in the 'concepts' domain.
* Bulk creation and editing of concept set members and concept answers using CSV files in **configuration/conceptsets**.
* Added support enabling any Initializer runtime property value to also be specified from a system property.
* Bulk creation and edition of AMPATH forms provided as JSON schema definitions in **configuration/ampathforms**.
* Enhancement to Initializer's custom message source to support improved handling of fallback locales and overrides between messages properties file from core, modules, and Initializer 'messageproperties' domain files.
* Added support for remote sources and identifier pools to the 'idgen' domain.
* Enhancement to ensure predictable loading order of files within a domain if no explicit order is specified, based on alphabetical ordering of filenames.
* Enhancement to the 'messageproperties' domain to enable specifying the order in which two files in the same locale should be loaded.

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
* Bulk creation and edition of bahmni forms provided JSON files in **configuration/bahmniforms**.
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
