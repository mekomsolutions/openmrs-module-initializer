## Domain 'appointmentsservicesdefinitions'

The **appointmentsservicesdefinitions** subfolder contains CSV configuration files that help modify and create Bahmni appointments services definitions. It should be possible in most cases to configure them via a single CSV configuration file, however there can be as many CSV files as desired.
This is a possible example of how the configuration subfolder may look like:
```bash
appointmentsservicesdefinitions/
  └── appointmentsservicesdefinitions.csv
```
The CSV configuration allows to either modify exisiting appointments service definitions or to create new ones. Here is a sample CSV:

| <sub>uuid</sub> | <sub>Void/Retire</sub> | <sub>Name</sub> | <sub>Description</sub> | <sub>Duration</sub> | <sub>Start Time</sub> | <sub>End Time</sub> | <sub>Max Load</sub> | <sub>Speciality</sub> | <sub>Location</sub> | <sub>Label Colour</sub> |
| ------------ | ------------ | ------------ | ------------ | ------------ | ------------ | ------------ | ------------ | ------------ | ------------ | ------------ |
| <sub>fc46dedf-5e96-44d4-bd99-bec1d80d15d5</sub> | | <sub>Radiology</sub> | <sub>Service offered by the diagnostic imaging department. Both in-patients and out-patients are serviced</sub> | | <sub>9:00</sub> | <sub>17:00</sub> | | <sub>X-Ray</sub> | <sub>OPD1</sub> | <sub>#8FBC8F</sub> | 

###### Header `Name` *(mandatory)*

###### Header `Description` *(optional)*

###### Header `Duration` *(optional)*
Duration of service in *minutes*, eg. `30`for 30 min.

###### Header `Start Time` and `End Time` *(optional)*
The working hours for a service or availability of a service in the following format: `HH:mm`.

###### Header `Max Load` *(optional)*
This is the indicative maximum number of individual appointments that can be booked for a service between the start and end times.

###### Header `Speciality` *(optional)*
A service will be associated with a broader speciality.

For example, 'X-Ray' as a service under 'Radiology'.

###### Header `Location` *(optional)*
A default location where a service is expected to be offered to the patients. 

###### Header `Label Colour` *(optional)*
The services can be assigned colours. On Bahmni calendar view all the appointments booked for this service will be displayed in the specified colour.

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).