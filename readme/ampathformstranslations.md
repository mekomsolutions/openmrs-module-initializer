## Domain 'ampathformstranslations'
The **ampathtranslationsforms** subfolder contains AMPATH Translation Forms JSON schema files. Each JSON file defines the translations for forms. For example,

```bash
ampathformstranslations/
  ├── form1_translations_fr.json
  └── form2_translations_en.json
```

**Note:** You will need a translation file for ALL languages you want to be able to switch between. For example, if you want to be able to switch between both French and English, you would need a file for BOTH those languages, as shown above. If you only had a fr.json file, then even when you set your language preference to English, you would still be seeing that form only in French. 

###### JSON file example:
```json
{
    "uuid": "c5bf3efe-3798-4052-8dcb-09aacfcbabdc",
    "form": "Form 1",
    "form_name_translation": "Formulaire 1",
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
* The `form` attribute must be provided with an existing form name for the translations to load successfully. The translations form resources get names following the following pattern `<form_name>_translations_<language>`.
* The `form_name_translation` attribute is treated as the localized name for the form in the translation locale. 

###### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).
