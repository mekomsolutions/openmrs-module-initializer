## Initializer Validator

### In a nutshell
The Initializer Validator is a standalone fatjar to make dry runs of your OpenMRS configs and to report on any errors. This enables developers and implementers to be warned well ahead of time that a config would fail when loaded on _real_ OpenMRS instances.

### How it works
The Initializer Validator replays OpenMRS configs in a Spring context-sensitive test environment, very much like any OpenMRS Spring context-sensitive test. This means that the data (or metadata in this case) is loaded in a real database, in fact an instance of MariaDB. This ensures that the dry run occurs in an environment that is very close to the real app runtime setup.

### How to make a dry run
1) Build Initializer with the `validator` profile

```bash
mvn clean package -P validator
```
2) Locate a config that you want to validate, eg. at **/Users/mksd/repos/openmrs-config-acme/configuration**.
3) Run it (use absolute paths for arguments):

```bash
java -jar ./validator/target/initializer-validator-2.5.0.jar \
  --config-dir=/Users/mksd/repos/openmrs-config-acme/configuration
```
4) That's it! The dry run will either pass or fail.
<br/>In case of failures all the relevant logs can be analysed at **./validator/target/initializer.log**.

### Dry run examples
##### On top of CIEL
```bash
java -jar ./validator/target/initializer-validator-2.5.0.jar \
  --config-dir=/Users/mksd/repos/openmrs-config-acme/configuration \
  --ciel-file=/Users/mksd/Downloads/openmrs_concepts_2.2_20200927.sql
```
##### Skipping some domains
```bash
java -jar validator/target/initializer-validator-2.5.0.jar \
  --config-dir=/Users/mksd/repos/openmrs-config-acme/configuration \
  --domains='!metadatasharing,privileges,roles'
```
##### Including only some domains
```bash
java -jar validator/target/initializer-validator-2.5.0.jar \
  --config-dir=/Users/mksd/repos/openmrs-config-acme/configuration \
  --domains='concepts,locations'
```
##### Excluding some files in a domain
```bash
java -jar validator/target/initializer-validator-2.5.0.jar \
  --config-dir=/Users/mksd/repos/openmrs-config-acme/configuration \
  --exclude.concepts='*diags*,*interventions*'
```
In the above example all 'concepts' domain files matching the [wildcard patterns](https://docs.oracle.com/cd/E23389_01/doc.11116/e21038/conditions.htm#BABEJGAH) `*diags*` and `*interventions*` will be filtered out.
##### Unsafe mode
```bash
java -jar ./validator/target/initializer-validator-2.5.0.jar \
  --config-dir=/Users/mksd/repos/openmrs-config-acme/configuration --unsafe
```
The unsafe mode will break as soon as the first loading error occurs, triggering the validation to stop short. This mode is particularly suitable for CI processes that just need to fail early.
##### CLI arguments
Just run the fatjar with no arguments (or with the `--help` argument) to get a list of all possible options:

```bash
java -jar validator/target/initializer-validator-2.5.0.jar
```
#### Note
Make sure to have the Docker Engine installed before running the validator. Please refer to the [installation docs](https://docs.docker.com/engine/install) for details.
