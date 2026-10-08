## Domain 'medicationsideeffects'
The **Medication Side Effects** subfolder contains CSV import files for saving known side effects of medications. Each row links a side effect to a `Drug` (at the drug/formulation level, not the concept), classified as *common* or *serious*. The effect itself is represented either by a coded `Concept` or as free text, and may carry a recommended clinical action.

```bash
medicationsideeffects/
  ├──medication_side_effects.csv
  └── ...
```
Below are the possible headers with a sample data set:

| <sub>Uuid</sub>                                 | <sub>Drug Uuid</sub>                             | <sub>Classification</sub> | <sub>Side Effect Concept Uuid</sub>              | <sub>Side Effect Text</sub>          | <sub>Recommended Action</sub>            | <sub>Notes</sub>          |
|-------------------------------------------------|--------------------------------------------------|---------------------------|--------------------------------------------------|--------------------------------------|------------------------------------------|---------------------------|
| <sub>7341f6c7-8b0a-4d4d-aae0-ecc5d1aa611c</sub> | <sub>dfd36a48-1946-454c-bc04-8dc7cada7120</sub>  | <sub>common</sub>         | <sub>5978AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA</sub>  | <sub></sub>                          | <sub></sub>                              | <sub>Nausea</sub>         |
| <sub>6ef54985-ce83-4af1-9cfe-73e87724470a</sub> | <sub>dfd36a48-1946-454c-bc04-8dc7cada7120</sub>  | <sub>serious</sub>        | <sub></sub>                                      | <sub>Signs of liver injury</sub>     | <sub>Stop and seek urgent care</sub>     | <sub>Free-text effect</sub>|

Let's review the headers below.

###### Header `Uuid` *(optional)*
The UUID of the side-effect record. If empty a random UUID is generated; if provided, the row updates the existing record with that UUID (or creates one with that UUID).

###### Header `Void/Retire` *(optional)*
When set to `true`, the rest of the row is ignored and the existing record (identified by `Uuid`) is voided. See [CSV conventions](csv_conventions.md).

###### Header `Drug Uuid` *(required)*
The UUID of an existing `Drug`. The Initializer module does not create drugs; the referenced drug must already exist (see [Drugs](drugs.md)).

###### Header `Classification` *(required)*
One of `common` or `serious` (case-insensitive).

###### Header `Side Effect Concept Uuid` *(required if `Side Effect Text` is empty)*
The UUID of an existing `Concept` describing the effect (typically used for coded, common effects).

###### Header `Side Effect Text` *(required if `Side Effect Concept Uuid` is empty)*
Free-text description of the effect (typically used for serious effects). **At least one** of `Side Effect Concept Uuid` or `Side Effect Text` must be provided on each row (both may be set).

###### Header `Recommended Action` *(optional)*
Free-text recommended clinical action for this effect.

###### Header `Notes` *(optional)*
Free-text internal notes.

#### Requirements
* The [medicationsideeffects module](https://github.com/openmrs/openmrs-module-medicationsideeffects) must be installed; the loader is activated only when it is present.
* The OpenMRS version must be 2.4.0 or higher.