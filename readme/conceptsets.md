## Domain 'conceptsets'
The **conceptsets** subfolder contains CSV import files for saving Concept Set Members and Concept Answers in bulk.
This is a possible example of its content:
```bash
conceptsets/
  ├──conceptsets.csv
  └── ...
```
Each CSV line maps an association between a `Concept` that is the Set or Question, and the `Member` which is either
the Set Member or the Concept Answer.  Both types support associating a specific `Sort Weight` for ordering.

###### Header `Concept` *(mandatory)*
This uniquely identifies the Concept which should contain the member, either as a Set Member or as an Answer.
One can specify either a UUID, a Fully-Specified Name, or a Mapping in the form of SOURCE:CODE.

###### Header `Member` *(mandatory)*
This uniquely identifies the Concept in the Set, either as a Set Member or as an Answer member.
One can specify either a UUID, a Fully-Specified Name, or a Mapping in the form of SOURCE:CODE.

###### Header `Member Type` (optional)
For clarity, one can specify either `Q-AND-A` or `CONCEPT-SET` (case-insensitive) to denote exactly what type of
association this is intended to create.  If this is not specified, then the system will try to infer it from the
Concept itself.  If the Concept is a Set, it will add it as a set Member.  Otherwise, it will be added as an Answer.

###### Header `Sort Weight` (optional)
If specified, this will ensure the saved Set Member or Concept Answer has the given sort weight assigned to it.
If not specified, the Set Member or Concept Answer will have this auto-assigned by the Concept Service API.

###### Metadata headers

Headers that start with an underscore such as `_order:1000` are metadata headers. 
The values in the columns under those headers are never read by the CSV parser.

---

#### Examples:
Please see the following file for an example: [concept_sets.csv](../api/src/test/resources/testAppDataDir/configuration/conceptsets/concept_sets.csv).