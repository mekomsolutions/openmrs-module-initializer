## Initializer Validator

### In a nutshell
The Initializer Validator is a standalone fatjar to make dry runs of your OpenMRS configs and report back on any errors. This enables developers and implementers to be warned well ahead of time that a config would fail when loaded on _real_ OpenMRS instances.

### How it works
The Initializer Validator replays OpenMRS configs in a Spring context-sensitive test environment, very much alike any OpenMRS Spring context-sensitive test. This means that the data (or metadata in this case) is loaded in a real database, in fact an instance of MariaDB. This ensures that the dry run occurs in an environment that is very close to the real app runtime setup.

### How to make a dry run
1) Build Initializer with the `validator` profile
```bash
mvn clean package -P validator
```
2) Locate a config that you want to validate, eg. at **/Users/mksd/repos/openmrs-config-acme/configuration**.
3) Run it (use absolute paths for arguments):
```bash
java -jar ./validator/target/initializer-validator-2.1.0-SNAPSHOT.jar \
  --config-dir=/Users/mksd/repos/openmrs-config-acme/configuration
```
4) That's it! The dry run will either pass or fail.
<br/>In case of failures all the relevant logs can be analysed at **./validator/target/initializer.log**.

### Examples on how to make a dry run
#### On top of CIEL
```bash
java -jar ./validator/target/initializer-validator-2.1.0-SNAPSHOT.jar \
  --config-dir=/Users/mksd/repos/openmrs-config-acme/configuration \
  --ciel-path=/Users/mksd/Downloads/openmrs_concepts_2.2_20200927.sql
```
#### Skipping some domains
```bash
java -jar validator/target/initializer-validator-2.1.0-SNAPSHOT.jar \
  --config-dir=/Users/mksd/repos/openmrs-config-acme/configuration \
  --domains='!metadatasharing,privileges,roles'
```
#### With only some domains
```bash
java -jar validator/target/initializer-validator-2.1.0-SNAPSHOT.jar \
  --config-dir=/Users/mksd/repos/openmrs-config-acme/configuration \
  --domains='concepts,locations'
```
#### Excluding some files in a domain
```bash
java -jar validator/target/initializer-validator-2.1.0-SNAPSHOT.jar \
  --config-dir=/Users/mksd/repos/openmrs-config-acme/configuration \
  --exclude.concepts='*diags*,*interventions*'
```
In the above example all 'concepts' domain files matching the wildcard patterns `*diags*` and `*interventions*` will be filtered out.
#### CLI arguments
Just run the fatjar with no arguments to get a list of all possible options:
```bash
java -jar validator/target/initializer-validator-2.1.0-SNAPSHOT.jar
```
### Kwown issues

