## Domain 'appointmentservicedefinitions'

The **appointmentservicedefinitions** subfolder contains CSV configuration files that help modify and create Bahmni appointment service definitions.
This is a possible example of how the configuration subfolder may look like:
```bash
appointmentservicedefinitions/
  └── servicedefinitions.csv
```
The CSV configuration allows to either modify exisiting appointment service definitions or to create new ones. Here is a sample CSV:

| <sub>Uuid</sub> | <sub>Void/Retire</sub> | <sub>Name</sub> | <sub>Description</sub> | <sub>Duration</sub> | <sub>Start time</sub> | <sub>End time</sub> | <sub>Max load</sub> | <sub>Speciality</sub> | <sub>Location</sub> | <sub>Label colour</sub> |
| ------------ | ------------ | ------------ | ------------ | ------------ | ------------ | ------------ | ------------ | ------------ | ------------ | ------------ |
| <sub>fc46dedf-5e96-44d4-bd99-bec1d80d15d5</sub> | | <sub>Radiology</sub> | <sub>Service offered by the diagnostic imaging department. Both in-patients and out-patients are serviced</sub> | | <sub>9:00</sub> | <sub>17:00</sub> | | <sub>X-Ray</sub> | <sub>OPD1</sub> | <sub>#8FBC8F</sub> | 

###### Header `Name` *(mandatory)*

###### Header `Description` *(optional)*

###### Header `Duration` *(optional)*
Duration of service in *minutes*, eg. `30`for 30 min.

###### Header `Start time` and `End time` *(optional)*
The working hours for a service or availability of a service in the following format: `HH:mm`.

###### Header `Max Load` *(optional)*
This is the indicative maximum number of individual appointments that can be booked for a service between the start and end times.

###### Header `Speciality` *(optional)*
A service can be associated with a broader speciality. For example, 'X-Ray' as a service under 'Radiology'. Both specialities names and specialities UUIDs can be used to provide a reference to a speciality.

###### Header `Location` *(optional)*
A default location where a service is expected to be offered to the patients. 

###### Header `Label colour` *(optional)*
The services can be assigned colours. On Bahmni calendar view all the appointments booked for this service will be displayed in the specified colour.

## Domain 'appointmentservicetypes'

The **appointmentservicetypes** subfolder contains CSV configuration files that help modify and create Bahmni appointment service types. This is a possible example of how the configuration subfolder may look like:
```bash
appointmentservicetypes/
  └── servicetypes.csv
```
The CSV configuration allows to either modify exisiting appointment service types or to create new ones. Here is a sample CSV:

| <sub>Uuid</sub> | <sub>Void/Retire</sub> | <sub>Name</sub> | <sub>Service definition</sub> | <sub>Duration</sub>|
| ------------ | ------------ | ------------ | ------------ | ------------|
| <sub>ffd7e0f4-33f1-4802-b87a-8d610ba1132d</sub> | | <sub>Short follow-up</sub> | <sub>Radiology</sub> | <sub>20</sub> |

**NOTE:** Service types are somewhat misnamed, they rather represent a **duration override** to a service definition.

###### Header `Name` *(mandatory)*

###### Header `Service definition` *(mandatory)*
A service for which this is a specific _type_. Both service definition names and service definition UUIDs can be used to set a reference to the service definition.

###### Header `Duration` *(optional)*
Duration of service in *minutes*, eg. `20`for 20 min.


#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).