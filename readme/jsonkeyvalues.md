## Domain 'jsonkeyvalues'
The **jsonkeyvalues** subfolder contains JSON configuration files that help provide custom configurations at runtime. Those JSON files are simple key values pairs, very much like global properties but that won't be persisted. This is how this domain may look like:
```bash
jsonkeyvalues/
  └── config.json
```
We envision its typical use as providing _identifiers_ of objects that are needed to configure other objects. For instance a report definition may depend on a certain concept, and although the report definition itself may never change, the concept used in the report definition may be different from an implementation to another.

###### JSON key-values configuration file example:
```json
{
  "main.report.diagnose.concept.uuid": "4421da0d-42d0-410d-8ffd-47ec6f155d8f",
  "main.report.chiefcomplaint.concept.fsn": "CHIEF COMPLAINT",
  "main.report.finding.concept.mapping": "Cambodia:123",
  "main.report.fathername.pat.uuid": "9eca4f4e-707f-4bb8-8289-2f9b6e93803c",
  "main.report.mothername.pat.name": "Mother name",
  "main.report.active": "true"
}
```
The above configuration illustrates that a report (referred to as "main report") needs to know about three concepts: one defining the diagnoses, one defining the chief complaints and one defining the findings. And a JSON key-values config file can be used to expose those concepts to our distribution at runtime. For example:
```java
InitializerService is = Context.getService(InitializerService.class);

Concept conceptDiagnoses = is.getConceptFromKey("main.report.diagnose.concept.uuid");
Concept conceptChiefComplaint = is.getConceptFromKey("main.report.chiefcomplaint.concept.fsn");
Concept conceptFinding = is.getConceptFromKey("main.report.findings.concept.mapping");

PersonAttributeType patFatherName = is.getPersonAttributeTypeFromKey("main.report.fathername.pat.uuid");
PersonAttributeType patMotherName = is.getPersonAttributeTypeFromKey("main.report.mothername.pat.name");

Boolean isActive = is.getBooleanFromKey("main.report.active");
...
```
`Concept` instances can be fetched by UUID, names or concept mappings.
`PersonAttributeType` instances can be fetched by UUID or name.

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).