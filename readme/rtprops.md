## Runtime Properties

The runtime behaviour of Initializer can to a certain extent be controlled by a handful of [OpenMRS runtime properties](https://wiki.openmrs.org/x/zhAz).

### 1) `initializer.domains` _(optional)_
Defines and inclusion or exclusion list of domains as a CSV string of domain names. If this property is unspecified all domains are being processed.
##### Example of inclusion list:
```java
initializer.domains='concepts,locations'
```
##### Example of exclusion list:
Just prefix the list with a negative `!` character:
```java
initializer.domains='!metadatasharing,privileges,roles'
```
This is exactly the same logic as for the Initializer Validator's `--domains` argument.
### 2) `initializer.exclude.<domain>` _(optional)_
##### Example
In the example below all 'concepts' domain files matching the [wildcard patterns](https://commons.apache.org/proper/commons-io/apidocs/org/apache/commons/io/FilenameUtils.html#wildcardMatchOnSystem-java.lang.String-java.lang.String-) `*diags*` and `*interventions*` will be filtered out:
```java
initializer.exclude.concepts='*diags*,*interventions*'
```
This is exactly the same logic as for the Initializer Validator's `--exclude.<domain>` argument.
### 3) `initializer.skip.checksums` _(optional)_
##### Example to skip the generation of checksums:
```java
initializer.skip.checksums=true
```
Omit this property or set it to false to let the checksum files be processed as usual.
<br/>This is the inverse logic as Initializer Validator's `--checksums` argument that _triggers_ the generation of checksums.
