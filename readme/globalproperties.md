## Domain 'globalproperties'
The **globalproperties** subfolder contains XML configuration files that specify which global properties to override. Note that existing global properties will be overridden and missing ones will be created.
This is a possible example of how the configuration subfolder may look like:
```bash
globalproperties/
  ├── gp_core.xml
  ├── gp_coreapps.xml
  └── ...
```
There can be as many XML files as desired. One may be enough in most cases, but providing multiples files is also a possibility if the implementation requires to manage them by modules, areas or categories. Beware that the behaviour will be undefined iif a global property is overridden in several places. 

###### Global properties XML configuration file example:
```xml
<config>
  <globalProperties>
    <globalProperty>
      <property>addresshierarchy.i18nSupport</property>
      <value>true</value>
    </globalProperty>
    <globalProperty>
      <property>locale.allowed.list</property>
      <value>en, km_KH</value>
    </globalProperty>
  </globalProperties>
</config>
```
The above XML configuration will set **addresshierarchy.i18nSupport** to `true` and **locale.allowed.list** to `"en, km_KH"`.

##### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).