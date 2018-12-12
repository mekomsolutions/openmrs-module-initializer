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

<br/>Let's review some important headers.

##### Localized headers: `Fully specified name`, `Short name` and `Description`
Those are locale specific headers, they are never used as such because they always need to indicate the locale for the values of their column.
<br/>For example for a column to contain short names in English (locale 'en') simply name the header `Short name:en`. The same logic applies for the other locale specific headers.

###### Header `Fully specified name` (localized)
The fully specified name is a secondary identifier for the concepts domain, it will be used to attempt fetching the concept if no UUID is provided.
###### Header `Short name` (localized)
###### Header `Description` (localized)
###### Header `Data class`
###### Header `Data type`

Here is an example of valid CSV to define basic concepts:

| <sub>Uuid</sub>  | <sub>Fully specified name:en</sub> | <sub>Short name:en</sub> | <sub>Description:en</sub> | <sub>Data class</sub>  | <sub>Data type</sub> |
| - | - | - | - | - | - |
| | <sub>Nationality</sub> | <sub>Nat.</sub> | <sub>The status of belonging to a particular nation.</sub> | <sub>Question</sub> | <sub>Text</sub> |
| <sub>db2f4fc4-..</sub>| <sub>Language</sub> | <sub>Lang.</sub> | <sub>The method of human communication.</sub> | <sub>Question</sub> | <sub>Text</sub> |

###### Header `Answers`
To provide a semicolumn-separated list of answer concepts to the concept to be created or edited.
###### Header `Members`
To provide a semicolumn-separated list of concepts to be members of the concept set to be created or edited. Note that the concept will be marked a being a set as soon as set members are provided.

Here is an example of the 'nested' columns:

| ... | <sub>Answers</sub> | <sub>Members</sub> | ... |
| - | - | - | - |
| ... | <sub>CONCEPT_NAME; source:134; db2f4fc4-..</sub> | | ... |
| ... | | <sub>CONCEPT_NAME; source:134; db2f4fc4-..</sub> | ... |

As the example suggests, it is possible to provide lists of concepts identifiers to fill the values of the columns 'answers' or 'members' under the form of concept names (eg. "CONCEPT_NAME"), concept mappings (eg. "source:134") and concept UUIDs (eg. "db2f4fc4-.."). The concepts that could not be fetched through their provided identifier will fail the creation of the concept from the CSV line altogether, and the parser will continue to the next CSV line.

**NOTE** In the current implementation the listing order of the concepts in the CSV file does matter since unexisting concepts will fail the CSV line processing. It is recommended to take this into account and to insert CSV lines for concepts with nested lists low enough in the CSV file so that all nested concepts are found when the CSV line is being processed.

###### Header `Same as mappings`
To provide a semicolumn-separated list of concept mappings for the concept to be created or edited.

Here is an example of the 'mappings' columns:

| ... | <sub>Same as concept mappings</sub> | ... |
| - | - | - |
| ... | <sub>ICD-10-WHO:T45.9; CIEL:122226; Cambodia:115</sub> | ... |

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
###### Header `Complex data handler`
This should be the class name of a valid complex data handler class at runtime. Eg. `ImageHandler`, `BinaryDataHandler`, ... etc.
See [here](https://github.com/openmrs/openmrs-core/tree/95641959f3a15ba5ae1a23d694114b9dbc466f12/api/src/main/java/org/openmrs/obs/handler) for the list of handlers shipped with Core 2.x.

**NOTE** The concept will be considered as a candidate to be a concept complex if and only if its data type is set to `Complex`.

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).