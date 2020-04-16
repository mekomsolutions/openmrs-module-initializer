## Domain 'bahmniforms'
The **bahmniforms** subfolder contains [Bahmni Forms 2.0](https://bahmni.atlassian.net/wiki/spaces/BAH/pages/683966468/Forms+2.0) JSON definition files.


This is how this domain may look like:
```bash
bahmniforms/
  └── form1.json
```

###### JSON file example:
```json
{
  "formJson" : {
          "name" : "test",
          "resources" : [
              {
                  "value" : "{\"name\":\"test\",\"id\":1,\"uuid\":\"89ecd6e7-0b51-454a-b4ab-defd67f59299\",\"defaultLocale\":\"en\",\"controls\":[{\"translationKey\":\"LABEL_2\",\"type\":\"label\",\"value\":\"update label\",\"properties\":{\"location\":{\"column\":0,\"row\":0}},\"id\":\"2\"},{\"type\":\"obsControl\",\"label\":{\"translationKey\":\"TEST_UPLOAD_3\",\"id\":\"3\",\"units\":\"\",\"type\":\"label\",\"value\":\"test_upload\"},\"properties\":{\"mandatory\":false,\"notes\":false,\"addMore\":false,\"hideLabel\":false,\"controlEvent\":false,\"location\":{\"column\":0,\"row\":1}},\"id\":\"3\",\"concept\":{\"name\":\"test_upload\",\"uuid\":\"37f33bce-8349-4eaf-8c09-fff7ff1ee5f5\",\"datatype\":\"Complex\",\"conceptClass\":\"Misc\",\"conceptHandler\":\"ImageHandler\",\"answers\":[],\"properties\":{\"allowDecimal\":null}},\"units\":null,\"hiNormal\":null,\"lowNormal\":null,\"hiAbsolute\":null,\"lowAbsolute\":null}],\"events\":{},\"translationsUrl\":\"/openmrs/ws/rest/v1/bahmniie/form/translations\"}",
                  "dataType" : "org.bahmni.customdatatype.datatype.FileSystemStorageDatatype",
                  "uuid" : "ecb6002f-d0a0-4c25-8b0e-2da78ab4aab4"
              }
          ]
      },
      "translations" : [
          {
              "locale" : "en",
              "labels" : {
                  "LABEL_2" : "just a label"
              },
              "concepts" : {
                  "TEST_UPLOAD_3" : "test_upload"
              },
              "formName" : "test",
              "version" : "1"
          }
      ]
}
```

The easiest way to retrieve such a JSON file in the first place is by exporting the form using the Form Builder [export tool](https://bahmni.atlassian.net/wiki/spaces/BAH/pages/239796253/Import+Export).

Note: the unique identifier for Bahmni Forms is their `name`. The `uuid` field is not taken into account.

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).
