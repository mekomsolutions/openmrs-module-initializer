## Domain 'drugs'
The **drugs** subfolder contains CSV import files for saving drugs in bulk. This is a possible example of its content:
```bash
drugs/
├── drugs.csv
└── ...
```
Here is an example of a drug header line:

| <sub>Uuid</sub> | <sub>Void/Retire</sub> | <sub>Name</sub> | <sub>Description</sub> | <sub>Concept Drug</sub> | ... | <sub>_version:1</sub> | <sub>_order:1000</sub> |
| - | - | - | - | - | - | - | - |

Let's review some important headers.
###### Header `Name`
###### Header `Description`
###### Header `Concept Drug`
The value under this header links to the concept representing the drug. The concept can either be provided by

* Name, eg. `"Cetirizine"` ;
* Concept mapping, eg. `"WHO:R06AE07"` ;
* UUID, eg. `"2bcf7212-d218-4572-8893-25c4b5b71934"`.

**REMARK:** since the `Drug` object requires an underlying concept to be referenced to as part of its definition, it is wise to ensure that the concept indeed exists in the first place. To help work around this possible issue the drugs domain is processed _after_ the concepts domain.

###### Header `Concept Dosage Form`
The value under this header links to the concept representing the dosage form. The same way as for the concept drug its value can be provided as a concept name, concept mapping or UUID.

###### Header `Strength`
Eg. `"10ml"`, `"500mg"` ... etc.

Here is an example of valid basic CSV line to defining a drug:

| <sub>Uuid</sub>  | <sub>Name</sub> | <sub>Concept Drug</sub> | <sub>Concept Dosage Form</sub> | <sub>Strength</sub> |
| - | - | - | - | - |
| | <sub>Cetirizine 10mg Tablet</sub> | <sub>Cetirizine</sub> | <sub>Tablet</sub> | <sub>10mg</sub> |

In this example both `"Cetirizine"` and `"Tablet"` are assumed to be names of _existing_ concepts.

###### Mappings headers
Drugs support one or more mappings to concept reference terms, which are created as DrugReferenceMaps. Each of these mappings are configurable with a mapping type, a source, and a code.  

Important note:  Due to a bug in OpenMRS core, use of Mappings headers will fail if your core version does not meet one of the following minimum versions:
* 2.7.0+
* 2.6.1+
* 2.5.11+
* 2.4.6+
* 2.3.6+

In order to specify mappings on a given concept, the values for those mappings can be set under ad-hoc headers starting with the special prefix `Mappings|`.  Mappings headers should be specified in the following format:

**Option 1:**  Column header specifies the mapping type, but not the source.  In this case, the column values must be one or more source:code pairs.  If specifying multiple source:code pairs, these must be separated by semi-colons.  Example:

| ... | <sub>Mappings\|SAME-AS</sub> | <sub>Mappings\|NARROWER-THAN</sub> |
| - | - | - |
| ... | <sub>CIEL:5089; SNOMED CT:27113001</sub> | <sub>LOINC:3141-9</sub> |

**Option 2:**  Column header specifies both the mapping type and the source.  In this case, the column values must only be the codes.  If specifiying multiple codes for the same mapping type and source, these must be separated by semi-colons, or they can alternatively be added as separate columns.  In the case of adding them as separate columns, and additional suffix is supported on the header to enable each column header to be unique.  Example:

| ... | <sub>Mappings\|SAME-AS\|CIEL</sub> | <sub>Mappings\|SAME-AS\|SNOMED CT</sub> | <sub>Mappings\|SAME-AS\|PIH\|Code</sub> | <sub>Mappings\|SAME-AS\|PIH\|Name</sub> |
| - | - | - | - | - |
| ... | <sub>5089</sub> | <sub>27113001</sub> | <sub>5089</sub> | <sub>WEIGHT (KG)</sub> |

###### Ingredient headers
OpenMRS supports the ability to associated 0-N ingredients with a Drug, where an ingredient is made up of an ingredient (Concept), strength (Double), and units (Concept)

Important note:  Due to a bug in OpenMRS core, use of Ingredient headers will silently fail if your core version does not meet one of the following minimum versions:
* 2.8.1+
* 2.7.6+
See [TRUNK-6411](https://openmrs.atlassian.net/browse/TRUNK-6411) for more information on this bug.

Ingredients may be specified via headers that start with the `Ingredient` prefix, followed by a numeric index.  You may add any number of ingredients by adding additional numeric indices.
These headers should be named as follows, where `N` should be replaced with a numerical index that is unique to each ingredient.

###### Header `Ingredient N`
The value under this header links to the concept representing the ingredient. This can be a concept name (if unique), concept mapping, or UUID.

###### Header `Ingredient N Strength`
The value under this heading is expected to be a numeric quantity.  This can include decimals but is not required.

###### Header `Ingredient N Units`
The value under this header links to the concept representing the units related to the strength. This can be a concept name (if unique), concept mapping, or UUID.

For example, a drug containing 2 ingredients might contain the following ingredient columns:

| ... | <sub>Ingredient 1</sub> | <sub>Ingredient 1 Strength</sub> | <sub>Ingredient 1 Units</sub>  | <sub>Ingredient 2</sub> | <sub>Ingredient 2 Strength</sub> | <sub>Ingredient 2 Units</sub> |
| --- |-------------------------|----------------------------------|--------------------------------|-------------------------|----------------------------------|-------------------------------|
| ... | <sub>Lopinavir</sub>    | <sub>200</sub>                   | <sub>Milligram</sub>           | <sub>Ritonavir<sub>     | <sub>50</sub>                    | <sub>Milligram</sub>          |

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).