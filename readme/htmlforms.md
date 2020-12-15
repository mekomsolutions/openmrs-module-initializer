## Domain 'htmlforms'
The **htmlforms** subfolder contains [HTML Forms](https://wiki.openmrs.org/display/docs/Html+Form+Entry+Module) XML definition files.
Each of these XML definition files (which have a ".xml" extension) double as both the htmlform schema and layout, as well as the
definition for how these forms should be saved to the database.  All files with a .xml extension will be included.  For example:

```bash
htmlforms/
  └── form1.xml
  └── form2.xml
```

HTML Forms loaded by this domain are configured through special attributes within the <htmlform> tag in a given XML definition file.
The attributes supported are exactly the same as those supported by the htmlformentryui module.  They are best illustrated by example
as follows:

###### XML file example:
```xml
<htmlform
		formUuid="203fa4f8-28f3-11eb-bc37-0242ac110002"
		formName="Test Form 1"
		formDescription="Test Form With All Attributes"
		formVersion="1.3"
		formEncounterType="61ae96f4-6afe-4351-b6f8-cd4fc383cce1"
		htmlformUuid="26ddfe02-28f3-11eb-bc37-0242ac110002"
>
	Date: <encounterDate/>
	Location: <encounterLocation/>
	Provider: <encounterProvider role="Provider"/>
	Weight: <obs conceptId="5089"/>
	<submit/>
</htmlform>
```
It is considered best practice to specify all of the above attributes for clarity.  However, it should be noted that doing so
will update not just the htmlform, but also the underlying "Form" that this htmlform relates to.

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).
