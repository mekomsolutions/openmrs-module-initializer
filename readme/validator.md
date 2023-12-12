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
java -jar ./validator/target/initializer-validator-2.3.2-SNAPSHOT.jar \
  --config-dir=/Users/mksd/repos/openmrs-config-acme/configuration
```
4) That's it! The dry run will either pass or fail.
<br/>In case of failures all the relevant logs can be analysed at **./validator/target/initializer.log**.

### Dry run examples
##### On top of CIEL
```bash
java -jar ./validator/target/initializer-validator-2.3.2-SNAPSHOT.jar \
  --config-dir=/Users/mksd/repos/openmrs-config-acme/configuration \
  --ciel-file=/Users/mksd/Downloads/openmrs_concepts_2.2_2.3.2-SNAPSHOT927.sql
```
##### Skipping some domains
```bash
java -jar validator/target/initializer-validator-2.3.2-SNAPSHOT.jar \
  --config-dir=/Users/mksd/repos/openmrs-config-acme/configuration \
  --domains='!metadatasharing,privileges,roles'
```
##### Including only some domains
```bash
java -jar validator/target/initializer-validator-2.3.2-SNAPSHOT.jar \
  --config-dir=/Users/mksd/repos/openmrs-config-acme/configuration \
  --domains='concepts,locations'
```
##### Excluding some files in a domain
```bash
java -jar validator/target/initializer-validator-2.3.2-SNAPSHOT.jar \
  --config-dir=/Users/mksd/repos/openmrs-config-acme/configuration \
  --exclude.concepts='*diags*,*interventions*'
```
In the above example all 'concepts' domain files matching the [wildcard patterns](https://docs.oracle.com/cd/E23389_01/doc.11116/e21038/conditions.htm#BABEJGAH) `*diags*` and `*interventions*` will be filtered out.
##### Unsafe mode
```bash
java -jar ./validator/target/initializer-validator-2.3.2-SNAPSHOT.jar \
  --config-dir=/Users/mksd/repos/openmrs-config-acme/configuration --unsafe
```
The unsafe mode will break as soon as the first loading error occurs, triggering the validation to stop short. This mode is particularly suitable for CI processes that just need to fail early.
##### CLI arguments
Just run the fatjar with no arguments (or with the `--help` argument) to get a list of all possible options:
```bash
java -jar validator/target/initializer-validator-2.3.2-SNAPSHOT.jar
```
### Known issues

##### mariaDB4j `"dyld: Library not loaded"` on macOS:
```
"dyld: Library not loaded: /usr/local/opt/openssl/lib/libssl.1.0.0.dylib"
```
This will only clearly come out when runnnig the Initializer Validator in `--verbose` mode.

Try the following commands (assuming you are using [Homebrew](https://brew.sh/)):

1. Uninstall openssl:
```bash
brew uninstall --ignore-dependencies openssl
```
2. Install openssl 1.0.x:
```bash
brew tap-new company/team; brew extract --version 1.0.2t openssl company/team; brew install company/team/openssl@1.0.2t
```
3. Link it to the expected path:
```bash
ln -s /usr/local/Cellar/openssl@1.0.2t/1.0.2t /usr/local/opt/openssl
```
This replaces the openssl vesion currently installed with version 1.0.2t. To undo this, just upgrade openssl: `brew upgrade openssl`.

###### References:
* '[Mac OS X MariaDB 10.3.13 binaries status unclear - working or not? If NOK, how to fix?](https://github.com/vorburger/MariaDB4j/issues/288)'
  * Specifically [here](https://github.com/vorburger/MariaDB4j/issues/288#issuecomment-552106844). 
* '[macOS OpenSSL version issue - Homebrew moved it from v1.0 to v1.1 - initial fix](https://github.com/kelaberetiv/TagUI/issues/635#issuecomment-696948461)'

##### mariaDB4j `"error while loading shared libraries: libncurses.so.5"` on Debian:
```
mysql: /tmp/MariaDB4j/base/bin/mysql:
  error while loading shared libraries: libncurses.so.5: cannot open shared object file: No such file or directory
```
This will only clearly come out when runnig the Initializer Validator in `--verbose` mode.

Try this suggested solution from Stack Overflow: '[error while loading shared libraries: libncurses.so.5:](https://stackoverflow.com/a/17801675/321797)'
