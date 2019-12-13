## Domain 'appointmentsservicedefinitions'

The **appointmentsservicedefinitions** subfolder contains CSV configuration files that help modify and create Bahmni appointments service definitions. It should be possible in most cases to configure them via a single CSV configuration file, however there can be as many CSV files as desired.
This is a possible example of how the configuration subfolder may look like:
```bash
appointmentsservicedefinitions/
  └── appointmentsservicedefinitions.csv
```
The CSV configuration allows to either modify exisiting appointments service definitions or to create new ones. Here is a sample CSV:

|uuid   | Void/Retire  | Name  | Description  | Duration  | Start Time  | End Time  | Max Load  | Speciality  | Location  | Label Colour  |
| ------------ | ------------ | ------------ | ------------ | ------------ | ------------ | ------------ | ------------ | ------------ | ------------ | ------------ |
| fc46dedf-5e96-44d4-bd99-bec1d80d15d5  |   |  Radiology | Service offered by the diagnostic imaging department. Both in-patients and out-patients are serviced |   | 9:00  | 17:00  |   | X-Ray  |  OPD1  | #8FBC8F  | 

###### Header `Name` and `Description`
The appointment service **name** is mandatory and must be provided. The description is optional, however it is best practise to provide a rich and meaningful description.

###### Header `Duration` 
Duration of service: Consultation time required to offer the service to a patient.

###### Header `Start Time` and `End Time` 
The working hours for a service or availability of a service

For example, if the diabetes clinic operates from 9.00 AM to 11.00 AM, then start time would be 9.00 AM and end time would be 11.00 AM

###### Header `Max Load` 
This is the indicative maximum no. of appointments that can be booked for a service, for a given availability (start and end time mentioned above)

For example, the clinic can take a maximum of 10 patients between 9.00 AM - 11.00 AM

###### Header `Speciality` 
A service will be associated with a broader speciality.

For example, X-Ray as a service under Radiology.

###### Header `Location` 
A default location where a service is expected to be offered to the patients. 

For example, OPD1 

All locations marked as "appointment locations" in openMRS will be available in the dropdown to choose from.

###### Header `Label Colour` 
The services can be assigned colours. On the calendar view, all the appointments booked for this service, will be displayed in this chosen colour.

This is available to users only when calendar view is turned on. 

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).