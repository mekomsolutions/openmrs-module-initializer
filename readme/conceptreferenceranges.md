## Domain 'conceptreferenceranges'

The **conceptreferenceranges** subfolder contains CSV configuration files that help manage ConceptReferenceRange entity.

This is a possible example of how the configuration subfolder may look like:

```bash
concepts/
  └── concepts_referenceranges.csv
```

Here is a sample CSV:

| Uuid                                 | Concept Numeric Uuid | Absolute low                     | Critical low                                    | Normal low                      | Normal high | Absolute high | Absolute high | Criteria | 
|--------------------------------------|-|----------------------------------|-------------------------------------------------|---------------------------------|------------------------------------|-|-------------------------| - |
| bc059100-4ace-4af5-afbf-2da7f3a34acf | 3f8f0ab7-c240-4b68-8951-bb7020be01f6 | 60                               | 70                                              | 80                              | 120 | 130 | 150                     | $patient.getAge() > 3 |


Summery of the set of available headers.

###### Header `Uuid`
Uuid of the conceptReferenceRange entity.

###### Header `Concept Numeric Uuid` *(mandatory)*
UUID of ConceptNumeric.

###### Header `Absolute low` *(mandatory)*
Absolute low of referenceRange.

###### Header `Critical low` *(mandatory)*
Critical low of referenceRange.

###### Header `Normal low` *(mandatory)*
Normal low of referenceRange.

###### Header `Normal high`
Normal high of referenceRange.

###### Header `Critical high`
Critical high of referenceRange.

###### Header `Absolute high` *(mandatory)*
Absolute high of referenceRange.

###### Header `Criteria` *(mandatory)*
Criteria.

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).