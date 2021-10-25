## Domain 'concepts'
The **concepts** subfolder contains CSV import files for saving concepts in bulk. This is a possible example of its content:
```bash
concepts/
  ├── diagnoses.csv
  ├── findings.csv
  ├── misc.csv
  └──  ...
```
The way those CSV files are processed is controlled by a reserved part of the CSV file header line that holds metadata about the CSV file itself. Here is an example of a header line:

| <sub>Uuid</sub> | <sub>Void/Retire</sub> | <sub>Fully specified name:en</sub> | <sub>Short name:en</sub> | <sub>Description:en</sub> | ... | <sub>_version:1</sub> | <sub>_order:1000</sub> |
| - | - | - | - | - | - | - | - |


#### Implicit Handling of Concept Names
Concept names (whether fully specified names, short names or synonyms) specified through their ad-hoc column on a CSV line that defines a concept end up being saved as their own separate `ConceptName` entities. In other words concept names are treated as second order metadata that is saved along when the concept itself is saved. This means that they have their own UUID assigned behind the scenes _implicitly_.

Each concept name UUID is however univoquely generated from the following seed information:
1. The concept UUID,
1. The concept name _value_,
1. The concept name type (FSN, short name or synonym),
1. The concept name locale.

Each combination of those four arguments always produce the same concept name UUID.

#### Concept Headers

##### Localized headers: `Fully specified name`, `Short name`, `Index term`, `Synonym`, and `Description`
Those are locale specific headers, they are never used as such because they always need to indicate the locale for the values of their column.
<br/>For example for a column to contain short names in English (locale 'en') simply name the header `Short name:en`. The same logic applies for the other locale specific headers.

##### Concept name headers: `Fully specified name`, `Short name`, `Index term`, `Synonym`

At least 1 Fully Specified Name **must** be specified for a Concept.  All other Concept Names are optional.
The fully specified name is a secondary identifier for the concepts domain, it will be used to attempt fetching the concept if no UUID is provided.

**Concept Name Types**
The Concept Name Type to associate with a given name is inferred from the Concept Name header.
A Concept may contain 1 `Fully specified name` and one `Short name` per Locale.  However, a Concept may contain more than one `Index term` and `Synonym` per Locale.
To allow for more than one `Index term` and `Synonym` while maintaining unique column headers, one simply needs to add a suffix to the header prior to the locale.
Examples:

* `Fully specified name:en`
* `Fully specified name:fr`
* `Short name:en`
* `Index term 1:fr`
* `Synonym 1:en`
* `Synonym 2:en`

**Locale Preferred**
By default, the first name found in a given Locale in a particular CSV row will be marked as Locale preferred.
If one wants to indicate that a different localized name should be locale preferred, this can be done via a `preferred` column associated with the name column.
For example, to indicate that the Concept Name added as `Synonym 1:en` should be locale preferred, you should add with a value of TRUE:

* `Synonym 1:en:preferred`

**Uuid**
One may specify the uuid to associate with a given Concept Name in order to have more control over this value.  
This is done by adding a a `uuid` column associated with the name column. For example, to associate a fixed UUId with
the `Synonym 1:en` name, one would add a column with the uuid as a value named:

* `Synonym 1:en:uuid`

**Identifying Concept Name updates vs deleting and adding**
The Concept loader will attempt to match incoming Concept Names with existing Concept Names if possible.  It will first try to match on
`uuid` if one is specified for a given Concept Name.  If no explicit uuid is specified it will try to match on exact name text and Locale.

If a matching name is found, the loader will update this existing name.  If a matching name is not found, the loader will save a new name.
Any existing concept names that were not matched are voided.

NOTE: OpenMRS does not allow changing the name text of an existing Concept Name.  If a uuid matches and the name does not, for an existing Concept Name,
processing will fail for the row.  One should choose a new uuid for the changed concept name, which will result in a new Concept Name created and the 
existing Concept Name getting voided during processing.

###### Header `Description` (localized)
###### Header `Data class` *(mandatory)*
###### Header `Data type` *(mandatory)*

Here is an example of valid CSV to define basic concepts:

| <sub>Uuid</sub>  | <sub>Fully specified name:en</sub> | <sub>Short name:en</sub> | <sub>Description:en</sub> | <sub>Data class</sub>  | <sub>Data type</sub> |
| - | - | - | - | - | - |
| | <sub>Nationality</sub> | <sub>Nat.</sub> | <sub>The status of belonging to a particular nation.</sub> | <sub>Question</sub> | <sub>Text</sub> |
| <sub>db2f4fc4-..</sub>| <sub>Language</sub> | <sub>Lang.</sub> | <sub>The method of human communication.</sub> | <sub>Question</sub> | <sub>Text</sub> |

###### Header `Answers` *(optional)*
To provide a semicolon-separated list of answer concepts to the concept to be created or edited.
###### Header `Members` *(optional)*
To provide a semicolon-separated list of concepts to be members of the concept set to be created or edited. Note that the concept will be marked to be a set as soon as set members are provided.

Here is an example of the 'nested' columns:

| ... | <sub>Answers</sub> | <sub>Members</sub> | ... |
| - | - | - | - |
| ... | <sub>CONCEPT_NAME; source:134; db2f4fc4-..</sub> | | ... |
| ... | | <sub>CONCEPT_NAME; source:134; db2f4fc4-..</sub> | ... |

As the example suggests, it is possible to provide lists of concepts identifiers to fill the values of the columns 'answers' or 'members' under the form of concept names in OpenMRS' _default_ locale (eg. `"Hypertension"`), concept _same-as_ mappings (eg. `"CIEL:117399"`) and concept UUIDs (eg. `"211b7c44-e346-47ab-866c-7ac638cd5352"`).
The concepts that could not be fetched through their provided identifier will fail the creation of the concept from the CSV line altogether, and the parser will continue to the next CSV line.

This also the case for concepts referenced by names that do not exist in the default locale, even though they may exist in other allowed locales. Eg. if the default locale is 'en' and that the hypertension concept is referenced by its 'es' name "hipertensión", then it will not be fetched and this will result in an error.

**NOTE** In the current implementation the listing order of the concepts in the CSV file does matter since unexisting concepts will fail the CSV line processing. It is recommended to take this into account and to insert CSV lines for concepts with nested lists low enough in the CSV file so that all nested concepts are found when the CSV line is being processed.

###### Header `Same as mappings`
To provide a semicolumn-separated list of concept mappings for the concept to be created or edited.

Here is an example of the 'mappings' columns:

| ... | <sub>Same as mappings</sub> | ... |
| - | - | - |
| ... | <sub>ICD-10-WHO:T45.9; CIEL:122226; Cambodia:115</sub> | ... |

###### Attribute headers
Concepts support *attributes*. The values for those attributes can be set under ad-hoc headers starting with the special prefix `Attribute|`. The value indicated on a CSV line will be resolved to its final value based on the type of the attribute. Let us look at an example:

| ... | <sub>Attribute\|Last Audit Date</sub> | ... |
| - | - | - |
| ... | <sub>2017-05-15</sub> | ... |

This attribute points to an attribute type identified by "`Last Audit Date`". The attribute type identifier (a name here) suggests that it might be an attribute of custom datatype `Date`. This means that its value, represented by the string `2017-05-15`, will eventually be resolved as the Java date `Mon May 15 00:00:00 2017` set as an attribute of the concept described by the CSV line.

Below are the headers specific to concepts numeric:
###### Header `Absolute low`
###### Header `Critical low`
###### Header `Normal low`
###### Header `Normal high`
###### Header `Critical high`
###### Header `Absolute high`
###### Header `Units`
###### Header `Allow decimals`
###### Header `Display precision`

**NOTE** The concept will be considered as a candidate to be a concept numeric if and only if its data type is set to `Numeric`.

Below are the headers specific to concepts complex:
###### Header `Complex data handler` *(mandatory for concepts complex)*
This should be the class name of a valid complex data handler class at runtime. Eg. `ImageHandler`, `BinaryDataHandler`, ... etc.
See [here](https://github.com/openmrs/openmrs-core/tree/95641959f3a15ba5ae1a23d694114b9dbc466f12/api/src/main/java/org/openmrs/obs/handler) for the list of handlers shipped with Core 2.x.

**NOTE** The concept will be considered as a candidate to be a concept complex if and only if its data type is set to `Complex`.

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).
