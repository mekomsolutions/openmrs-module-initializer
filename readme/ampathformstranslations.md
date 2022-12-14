## Domain 'ampathformstranslations'
The **ampathtranslationsforms** subfolder contains AMPATH Translation Forms JSON schema files. Each JSON file defines the translations for forms. For example,

```bash
ampathformstranslations/
  ├── form1_translations_fr.json
  └── form2_translations_en.json
```

###### JSON file example:
```json
{
    "uuid": "c5bf3efe-3798-4052-8dcb-09aacfcbabdc",
    "form": "Form 1",
    "description": "French Translations for Form 1",
    "language": "fr",
    "translations": {
      "Encounter": "Rencontre",
      "Other": "Autre",
      "Child": "Enfant"
    }
}
```

**NOTE:** 
* The `form` attribute must be provided with an existing form name for the translations to load successfully. The translations form resources get names following the following pattern `<form_name>_translations_<locale>`.
* The UUID must match the identifiers specified in the form's schema, as shown [here](../readme/ampathforms.md) in the translations property, eg:
```json
"translations": {
  "en": ["global-uuid-resource-en", "test-form-en"],
  "fr": ["global-fr-uuid-resource-fr", "test-form-fr"]
}
```

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).
