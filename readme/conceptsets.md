## Domain 'conceptsets'
The **conceptsets** subfolder contains CSV import files for saving concept set members and concept answers in bulk.
This is a possible example of its content:
```bash
conceptsets/
  ├──conceptsets.csv
  └── ...
```
Each CSV line maps an association between a `Concept` that is the set or question, and the `Member` which is either
the set member or the concept answer.  Both types support associating a specific `Sort Weight` for ordering.  

Note that this domain does not affect existing concept answers or set members that are not directly referenced.
The domain does not remove existing concept answers and set members if they are not present in the conceptsets CSV file.
Rather, the domain only acts upon each Concept Answer or Set Member that is directly referenced.
So as an example if the concepts domain first associates 3 answers with a concept, and that the conceptsets domain subsequently
includes a single concept answer row for this same concept, the existing answers that are not related to 
this row are left unaffected.  The following are possible results:

* The resulting concept has 2 answers - if the conceptsets row voids an existing answer.
* The resulting concept has 3 answers - if the conceptsets row updates the sort weight of an existing answer.
* The resulting concept has 4 answers - if the conceptsets row adds a new answer.

###### Header `Concept` *(mandatory)*
This uniquely identifies the concept which should contain the member, either as a set member or as an answer.
One can specify either a UUID, a fully specified name, or a mapping in the form of `SOURCE:CODE`.

###### Header `Member` *(mandatory)*
This uniquely identifies the concept in the set, either as a set member or as an answer member.
One can specify either a UUID, a fully specified name, or a mapping in the form of `SOURCE:CODE`.

###### Header `Member Type` (optional)
For clarity, one can specify either `Q-AND-A` or `CONCEPT-SET` (case-insensitive) to denote exactly what type of
association this is intended to create.  If this is not specified, then the system will try to infer it from the
concept itself.  If the concept is a set, it will add it as a set member.  Otherwise, it will be added as an answer.

###### Header `Sort Weight` (optional)
If specified, this will ensure the saved set member or concept answer has the given sort weight assigned to it.
If not specified, the set member or concept answer will have this auto-assigned by the `ConceptService` API.

---

#### Examples:
Please see the following file for an example: [concept_sets.csv](../api/src/test/resources/testAppDataDir/configuration/conceptsets/concept_sets.csv).
