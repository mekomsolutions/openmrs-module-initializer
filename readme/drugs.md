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

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).