## Domain 'queues'
The **queues** subfolder contains CSV import files for saving queues in bulk. This is a possible example of its content:
```bash
queues/
  ├──queues.csv
  └── ...
```
There is currently only one format for the queue CSV line, here are the possible headers with a sample data set:

| <sub>Uuid</sub>                                 | <sub>Void/Retire</sub> | <sub>Name</sub>                        | <sub>Description</sub>   | <sub>Service</sub>            | <sub>Location</sub>           |
|--------------------------------------|-------------|-----------------------------|---------------|--------------------|--------------------|
| <sub>32176576-1652-4835-8736-826eb0237482</sub> |             | <sub>Clinical Consultation Queue</sub> | <sub>Consult Queue</sub> | <sub>Outpatient Service</sub> | <sub>Outpatient Clinic</sub>| |

Headers that start with an underscore such as `_order:1000` are metadata headers. The values in the columns under those headers are never read by the CSV parser.

Let's review some important headers.

###### Header `Name` *(mandatory)*
This is _not_ a localized header.
<br/>The  name is _not_ a secondary identifier to access a queue type. A UUID must be provided for each queue.

###### Header `Description`
A description is optional and will populate the queue description

###### Header `Service`
This is a reference (UUID, same as mapping or name) to an existing concept that defines the service associated with this queue.

###### Header `Location`
This is a reference (UUID or name) to an existing location that defines the location associated with this queue.

#### Requirements
* The [queue module](https://github.com/openmrs/openmrs-module-queue) must be installed
* The OpenMRS version must be 2.3 or higher

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).
