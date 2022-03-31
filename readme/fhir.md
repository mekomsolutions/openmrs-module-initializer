## Domain 'fhirconceptsources'

The **fhirconceptsources** subfolder contains CSV import files for defining concept source URLs for any concept sources in
your dictionary. In OpenMRS, we tend to refer to concepts using mappings, e.g., SNOMED CT:1234. In FHIR, these mappings are
represented as two properties: a `system`, defined by a URL, and a code. The corresponding FHIR representation of the
previous mapping would be:

```json
{
  "system": "http://snomed.info/sct",
  "code": "1234"
}
```

By adding entries to this table, you enable the FHIR API to return concepts mapped to that concept source as responses
wherever concepts can be displayed.

This is a possible example of its contents:
```bash
fhirconceptsources/
  ├──conceptsources.csv
  └── ...
```

The format of this CSV should be as follows:

| <sub>Uuid</sub> |<sub>Void/Retire</sub> | <sub>Concept source name</sub> | <sub>Url</sub> | <sub>_order:1000</sub> |
| - | - | - | - | - |
| <sub>befca738-1704-4a47-9f6a-0fcacf786061</sub> | | <sub>CIEL</sub> | <sub>https://api.openconceptlab.org/orgs/CIEL/sources/CIEL</sub> | |

Headers that start with an underscore such as `_order:1000` are metadata headers. The values in the columns under those headers are never read by the CSV parser.

###### Header `Concept source name`

This is the name of the concept source (and also the name of the entry in the fhir_concept_source table). It's usually just
the human-readable name of the concept source. This is the unique identifier for the FHIR concept source and is *required*
for every entry.

###### Header `Url`

This is the URL of the code system in FHIR. For terminologies identified
[in the FHIR CodeSystem registry](https://www.hl7.org/fhir/terminologies-systems.html), this should be the preferred URL for
that code system, e.g. SNOMED CT is "http://snomed.info/sct". If the code system is not defined by HL7 or that table, then
the code systems own preferred URL should be used, e.g., for CIEL we tend to use
"https://api.openconceptlab.org/orgs/CIEL/sources/CIEL".

## Domain 'fhirpatientidentifiersystems'

The **fhirpatientidentifiersystems** subfolder contains CSV import files for defining identifier system URLs for any patient
identifier types in your OpenMRS instance. These system identifiers are used in data exchange to disambiguate between different
identifiers a patient may have. If none are provided, the identifier type UUID is used as the "system" for these identifiers.

This is a possible example of its contents:
```bash
fhirpatientidentifiersystems/
  ├──identifiersystems.csv
  └── ...
```

The format of this CSV should be as follows:

| <sub>Uuid</sub> |<sub>Void/Retire</sub> | <sub>Patient identifier type name</sub> | <sub>Url</sub> | <sub>_order:1000</sub> |
| - | - | - | - | - |
| <sub>87c87473-b394-430b-93d3-b46d0faca26e</sub> | | <sub>OpenMRS ID</sub> | <sub>http://openmrs.org/identifier</sub> | |

Headers that start with an underscore such as `_order:1000` are metadata headers. The values in the columns under those headers are never read by the CSV parser.

