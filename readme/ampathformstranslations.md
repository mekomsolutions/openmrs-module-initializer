## Domain 'ampathformstranslations'
The **ampathtranslationsforms** subfolder contains AMPATH Translation Forms JSON schema files. Each JSON file defines the translations for forms. For example,

```bash
ampathformstranslataions/
  └── global-fr.json
  └── form1-fr.json
  └── form2-es.json
```

###### JSON file example:
```json
{
    "uuid": "c5bf3efe-3798-4052-8dcb-09aacfcbabdc",
    "description": "French Translations for Form 1",
    "language": "fr",
    "translations": {
      "Encounter": "Encontre",
      "Other": "Autre",
      "Child": "Enfant"
    }
}
```

**Note** The uuid must match the identifiers specified in the form's schema, as shown [here](../readme/ampathforms.md). in the translations property.

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).
