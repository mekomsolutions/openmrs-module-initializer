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

### Concept Name Updating vs Concept Name Deleting + Creating
The concepts domain loader will attempt to match incoming concept names with existing concept names when possible.  It will first try to match on
the concept name UUID if one is specified.  If no explicit UUID is specified it will try to match on the exact combination of name text, name type and locale.

If a matching name is found, the loader will update this existing name.  If a matching name is not found, the loader will save a new name.
Any existing concept names that were not matched are voided in a accordance with the WYSIWYG principle.

NOTE: OpenMRS does not allow changing the name text of an existing `ConceptName` entity.  If a UUID matches and the name does not for an existing concept name, the
processing will fail for the row.  One should assign a new UUID when changing a concept name, which will result in a new `ConceptName` entity to be created and for the existing `ConceptName` entity to be voided.

### Implicit Generation of Concept Names UUIDs
When no UUID is explicitely defined for a concept name, one is generated implicitly by Initializer in a determinsitic way.
Implicit concept names UUIDs are generated from the following seed information:
1. The concept UUID,
1. The concept name text value,
1. The concept name type (FSN, short name or synonym),
1. The concept name locale.

Each combination of those four arguments always produce the same concept name UUID.


### Concept Headers

###### Headers `Fully specified name:<locale>`, `Short name:<locale>`, `Index term <n>:<locale>`, `Synonym <n>:<locale>`, and `Description:<locale>`
Those are locale specific headers that need to indicate the locale for the values of their column.
<br/>For example for a column to contain short names in English (locale 'en') simply name the header `Short name:en`. The same logic applies for the other locale specific headers.

At least one fully specified name **must** be provided for a concept.  All other concept names are optional.
The fully specified name is a secondary identifier for the concepts domain, it will be used to attempt fetching the concept if no UUID is provided.
A concept may contain one short name per Locale. A concept may contain more than one index term and synonym per Locale.

The **concept name type** to associate with a given name is inferred from the concept name header, namely either `FULLY_SPECIFIED`, `SHORT`, `INDEX_TERM` or _none_ (which implicitly means that the name is a synonym.)

To allow for more than one `Index term` and `Synonym` while maintaining unique column headers, one simply needs to add an indexing suffix `<n>` to the header prior to the locale.
Some examples:

* `Fully specified name:en`
* `Fully specified name:fr`
* `Short name:en`
* `Index term 1:fr`
* `Synonym 1:en`
* `Synonym 2:en`

###### Headers `Fully specified name:<locale>:Preferred`, `Short name:<locale>:Preferred`, `Index term <n>:<locale>:Preferred` and `Synonym <n>:<locale>:Preferred`

By default, the first name found for a given locale in a particular CSV row will be marked as being **locale preferred**.
If one wants to indicate that a different localized name should be locale preferred, this can be done via a `preferred` (case insensitive) column associated with the name column.
For example, to indicate that the concept name added as `Synonym 1:en` should be locale preferred, one should set a truthy value:

| <sub>Synonym 1:en</sub> | <sub>Synonym 1:en:Preferred</sub> |
|--------------|------------------------|
| <sub>Diabetes</sub>     | <sub>TRUE</sub>                   |

###### Headers `Fully specified name:<locale>:Uuid`, `Short name:<locale>:Uuid`, `Index term <n>:<locale>:Uuid` and `Synonym <n>:<locale>:Uuid`
One may specify the uuid to associate with a given concept name in order to fully control references to this concept name.
This is done by adding a a `uuid` column associated with the name column. For example, to associate a fixed UUID with
the `Synonym 1:en` name, one would add a column with the uuid as a value named:

| <sub>Synonym 1:en</sub> | <sub>Synonym 1:en:Uuid</sub> |
|--------------|------------------------|
| <sub>Diabetes</sub>     | <sub>d4bce1d7-1a1b-4108-b87f-e630e43c7374</sub>                   |

###### Header `Description:<locale>`
Similarly as with concept names the description is localised.
###### Header `Data class` *(mandatory)*
###### Header `Data type` *(mandatory)*
###### Header `Version` *(optional)*

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
