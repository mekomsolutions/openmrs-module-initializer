## Domain 'liquibase'
The **liquibase** subfolder contains Liquibase changelog files for the purpose of updating difficult to predetermine metadata before they can be consumed safely. The underlying changelog files may contain or reference other files defining database changesets. This is how this domain may look like:

```bash
liquibase/
  └── liquibase.xml
  └── concepts.xml
```
The purpose for this domain is to provide updates on metadata and not any database schema. This for example, could be used to ensure all desired metadata is deterministic in nature with known `id`s/`uuid`s before it's/they are usable/reference-able.

###### liquibase.xml configuration file example:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog/1.9" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog/1.9
                  http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-1.9.xsd">
	<include file="concepts.xml" relativeToChangelogFile="true"/>

	<!-- Encounter Types -->
	<changeSet id="jUnit-2022-02-22-12-51" author="test">
		<comment>
			Set encounter type 'Admission' UUID
		</comment>
		<update tableName="encounter_type">
			<column name="uuid" value="13c7556b-e868-4612-a631-bfdbed24c9f0" />
			<where>name='Admission'</where>
		</update>
	</changeSet>
</databaseChangeLog>
```
The above changelog illustrates a typical use-case, where Liquibase changesets could be defined in the main `liquibase.xml` file and other files changelog files referenced from this **main** file using the `<include file="[otherLiquibaseFileName].xml" relativeToChangelogFile="true"/>`. Notice that all referenced files in this way should have `relativeToChangelogFile` set to `true` otherwise the Liquibase changesets would fail to load. Changelog files can be further organized in sub-folders and referenced/included accordingly.

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).