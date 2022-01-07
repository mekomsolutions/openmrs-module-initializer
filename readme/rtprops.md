## Runtime Properties

The runtime behaviour of Initializer can to a certain extent be controlled by a handful of [OpenMRS runtime properties](https://wiki.openmrs.org/x/zhAz).

As an alternative or complement to runtime properties, any property may also be specified as a system property.
The order of precedence if both are used on the same system is that any property set via a system property will override any value set as a runtime property.
If a system property is specified with an empty value, this will also override the runtime property with this empty value.

Runtime properties can be specified at server startup with -DpropertyName=propertyValue.  They can also be specified in SDK environments by adding these
properties to one's openmrs-server.properties file.

### 1) `initializer.domains` _(optional)_
Defines and inclusion or exclusion list of domains as a CSV string of domain names. If this property is unspecified all domains are being processed.
##### Example of inclusion list:
```
initializer.domains=concepts,locations
```
##### Example of exclusion list:
Just prefix the list with a negative `!` character:
```
initializer.domains=!metadatasharing,privileges,roles
```
This is exactly the same logic as for the Initializer Validator's `--domains` argument.
### 2) `initializer.exclude.<domain>` _(optional)_
##### Example
In the example below all 'concepts' domain files matching the [wildcard patterns](https://commons.apache.org/proper/commons-io/apidocs/org/apache/commons/io/FilenameUtils.html#wildcardMatchOnSystem-java.lang.String-java.lang.String-) `*diags*` and `*interventions*` will be filtered out:
```
initializer.exclude.concepts=*diags*,*interventions*
```
This is exactly the same logic as for the Initializer Validator's `--exclude.<domain>` argument.
### 3) `initializer.skip.checksums` _(optional)_
##### Example to skip the generation of checksums:
```
initializer.skip.checksums=true
```
Omit this property or set it to false to let the checksum files be processed as usual.
<br/>This is the inverse logic as Initializer Validator's `--checksums` argument that _triggers_ the generation of checksums.

### 4) `initializer.startup.load` _(optional)_

Defines the _mode_ by which Initializer should load domains at startup by the Initializer module activator.
Valid options are:

* **continue_on_error** (default).  This is the default behavior if no option is specified.  It instructs Initializer 
  to load in domains at module startup.  If any domains throw an Exception during loading, these are logged and 
  Initializer proceeds to load further domains and complete startup without any startup errors.

* **fail_on_error**.  This mode instructs Initializer to load in domains at module startup.  If any domains throw an 
  Exception during loading, Initializer will log these errors and immediately throw a fatal exception that results in 
  the Initializer module and any dependent modules failing to start successfully.

* **disabled**.  This mode instructs Initializer to **NOT** load in domains at module startup.  This mode may be 
  useful for distributions that wish more control over the exact order and timing of domain loading.