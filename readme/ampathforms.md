## Domain 'ampathforms'
The **ampathforms** subfolder contains JSON schema files that are either [AMPATH Forms](https://ampath-forms.vercel.app/) or [O3 Forms](http://json.openmrs.org/form.schema.json). Each JSON file defines the schema for a different form. This is an example of how this folder's content may look like for two forms being defined:

```bash
ampathforms/
  ├── form1.json
  └── form2.json
```

#### General examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).

**NOTE:** When a form file is loaded, a new resource with the existing forms translations is created

### AMPATH forms

The JSON source for these forms can be obtained from [AMPATH Form Builder](https://openmrs-spa.org/formbuilder/) by downloading the form, although it can also be written by hand.

**NOTE:** The [AMPATH Form Builder](https://openmrs-spa.org/formbuilder/) does not include a reference to the encounter type by default. For encounter-based forms, it is important to specify an `"encounter"` field with the name of the encounter type associated with the form, or else Initializer will not be able to properly load the form.

**NOTE:** Like other form engines (and as a result of the form tooling), the unique identifier for a form is its name. As a result, the `uuid` field provided by the Form Builder is usually `"xxxx"` and not used. Instead, the form UUID is determined based on the form name and the form version. Any previous version of the form with the same name, however, will also be replaced with whatever form is loaded by Initializer. It is therefore not recommended to combine Initializer with another mechanism for loading forms into the system, e.g., by using the Form Builder directly.

#### JSON file example:

```json
{
  "name": "Test Form 1",
  "description": "Test 1 Description",
  "version": "1",
  "published": true,
  "retired": false,
  "encounter": "Emergency",
  "pages": [
    {
      "label": "Page 1",
      "sections": [
        {
          "label": "Section 1",
          "isExpanded": "true",
          "questions": [
            {
              "label": "Height",
              "type": "obs",
              "questionOptions": {
                "rendering": "number",
                "concept": "5090AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                "max": "",
                "min": "",
                "showDate": "",
                "conceptMappings": [
                  {
                    "type": "CIEL",
                    "value": "5090"
                  },
                  {
                    "type": "AMPATH",
                    "value": "5090"
                  },
                  {
                    "type": "PIH",
                    "value": "5090"
                  }
                ]
              },
              "id": "Ht"
            }
          ]
        }
      ]
    }
  ],
  "processor": "EncounterFormProcessor",
  "referencedForms": []
}
```

### O3 forms

The JSON source for these forms can be obtained from [O3 Form Builder](https://openmrs.atlassian.net/wiki/spaces/docs/pages/151060802/Building+forms+using+the+O3+Form+Builder) by downloading the form, although it can also be written by hand.

**NOTE:** Like other form engines (and as a result of the form tooling), the unique identifier for a form is its name. As a result, the `uuid` field provided by the Form Builder is not used. Instead, the form UUID is determined based on the form name and the form version. Any previous version of the form with the same name, however, will also be replaced with whatever form is loaded by Initializer. It is therefore not recommended to combine Initializer with another mechanism for loading forms into the system, e.g., by using the Form Builder directly.

**NOTE:** Currently the form builder does not export the `retired` and `published` fields for a form when you download the schema.

#### JSON file example:
```json
{
  "name": "O3 form example",
  "description": "O3 form Description",
  "version": "1.0",
  "encounterType": "4c384d33-6fc4-4b99-a3b3-efc285409e7f",
  "pages": [
    {
      "label": "Page 1",
      "sections": [
        {
          "label": "Section 1",
          "isExpanded": "true",
          "questions": [
            {
              "label": "Height",
              "type": "obs",
              "questionOptions": {
                "rendering": "number",
                "concept": "5090AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                "max": "",
                "min": "",
                "showDate": "",
                "conceptMappings": [
                  {
                    "type": "CIEL",
                    "value": "5090"
                  },
                  {
                    "type": "AMPATH",
                    "value": "5090"
                  },
                  {
                    "type": "PIH",
                    "value": "5090"
                  }
                ]
              },
              "id": "Ht"
            }
          ]
        }
      ]
    }
  ],
  "processor": "EncounterFormProcessor",
  "referencedForms": [],
  "uuid": "684fcd83-4a56-4cc3-8199-6c6630a18271"
}
```