## Domain 'orderfrequencies'

The **orderfrequencies** subfolder contains CSV configuration files that help modify and create order frequencies. It should be possible in most cases to configure them via a single CSV configuration file, however there can be as many CSV files as desired.
This is a possible example of how the configuration subfolder may look like:
```bash
orderfrequencies/
  └── freqs.csv
```
The CSV configuration allows to either modify exisiting order frequencies or to create new order frequencies, here are the possible headers:

| <sub>Uuid</sub> | <sub>Void/Retire</sub> | <sub>Frequency per day</sub>  | <sub>Concept frequency</sub> |
| - | - | - | - |

###### Header `Concept frequency`
A `Concept` of data class 'Frequency' and data type 'N/A' that represents the order frequency. Typically the name of the order frequency will be obtained through that concept. Examples: "bidaily", "daily", "weekly" ... etc.

###### Header `Frequency per day`
A `Double` that represents the actual numerical frequency per day. The consistency would demand to ensure that it matches the representation given in the `Concept frequency` column. Examples: a bidaily frequency should have a frequency per day of 0.5 (or 2 depending on the interpretation of 'bidaily'), a daily frequency should have a frequency per day of 1.0, a weekly frequency should have a frequency per day of 1/7... etc.

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).