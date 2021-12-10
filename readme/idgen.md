## Domain 'idgen'

The **idgen** subfolder contains CSV configuration files that help modify and create identifier sources. 
It should be possible in most cases to configure them via a single CSV configuration file, however there can be as many CSV files as desired.
This is a possible example of how the configuration subfolder may look like:
```bash
idgen/
  └── idgen.csv
```
The CSV configuration allows to either modify existing identifier sources or to create new identifier sources.
The following headers are supported:

### All sources

###### Header `Uuid`
This is the UUID of the source to create or modify

###### Header `Name` (required)
This is the name of the source

###### Header `Description`
This is the description of the source

###### Header `Void/Retire`
If the value is truthy this will retire the given source

###### Header `Identifier type` (required)
This is the reference to the underlying identifier type, both an identifier type name or UUID can be provided.

### Specific Types of Sources

One can choose to organize their sources in CSV files however they like.  They can combine multiple types of sources
in a single file that contains a superset of all necessary headers, or they can create multiple files that only
contain the headers relevant for the sources in those files.

For a given row in the CSV file, the type of source that will be instantiated is based on the columns that are populated
for that row.  The below sections describe the 3 types of sources that can be created.  If a non-empty value is 
supplied for a given column that is specific to one of these sources, then this is what determines that this type of 
source is what is created.  All sources have at least one required column to ensure successful identification.

#### Sequential Identifier Generators

###### Header `Base character set` (required)
The range of characters used in a given identifier

###### Header `First identifier base` (required)
The first base identifier in the character set (the first identifier that should be generated, less prefix and suffix)

###### Header `Prefix` (optional)
A prefix that should be present on all generated identifiers

###### Header `Suffix` (optional)
A suffix that should be present on all generated identifiers

###### Header `Min length` (optional)
The minimum length of an identifier (an identifier will be padded with leading zeros as necessary)

###### Header `Max length` (optional)
The maximum length that this generator is allowed to produce

#### Remote Identifier Sources

###### Header `Url` (required)
The url to the remote source

###### Header `User` (required)
The username to authenticate with the remote source

###### Header `Password` (required)
The password to authenticate with the remote source

#### Identifier Pools

###### Header `Pool identifier source` (required)
The UUID of the identifier source that should be used as the source of the pool

###### Header `Pool refill batch size` (optional)
The number of identifiers to refill from the linked source in a given batch

###### Header `Pool minimum size` (optional)
The minimum size of the pool that should be maintained (used by the scheduled refill task)

###### Header `Pool refill with task` (optional)
Set to a truthy value to indicate that the pool should be refilled on a schedule with the configured batch size when minimum size is reached

###### Header `Pool sequential allocation` (optional)
Set to false to indicate that the pool should issue identifiers randomly rather than in the order in which they are added from the source

### Special Headers

###### Header `_version` (optional)
Not currently used, but available as a means to provide future backwards compatibility

###### Header `_order` (optional)
If multiple csv files are present in a domain, if an order is specified, the lower order will be processed first

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration/idgen).