## Domain 'billing'
The **billing** subfolder contains CSV import files for saving Billable Services, Cash Point and Payment Modes in bulk. This is a possible example of its content:
```bash
billing/
  ├──billing.csv
  └── ...
```
There is currently only one format for the billing CSV line, here are the possible headers with a sample data set:

| <sub>Uuid</sub>                                 | <sub>Service Name</sub> | <sub>Short Name</sub>                        | <sub>Concept</sub>   | <sub>Service Type</sub>            | <sub>Service Status</sub>            |
|--------------------------------------|-------------|-----------------------------|---------------|--------------------|--------------------|
| <sub>32176576-1652-4835-8736-826eb0237482</sub> |   </sub>Antenatal Care</sub>         | <sub>ANTC</sub> | <sub>1592AAAAAA AAAAAAAAAAAAAAAA AAAAAAAAAA</sub> | <sub>Antenatal Services</sub> |<sub>Enabled</sub> |

Let's review some important headers.
##### Billable Services
###### Header `UUID` 
This is the UUID of the billable service

###### Header `Service Name` *(required)*
This is the name of the billable service

###### Header `Short Name` *(optional)*
This is the short name of the billable service associated with the billable service

###### Header `Concept` *(optional)*
This is a reference (UUID, same as mapping or name) to an existing concept associated with the service.

###### Header `Service Type` *(optional)*
This is a reference to an existing concept for the type of service that could be assigned to the Billable Service created.

###### Header `Service Status` *(optional)*
This references to the service status, defaulting to Billable Service Status ENABLED if the status is not provided.

##### Cash Points

The cash points are locations where a a bill has been made for a billable service. The Cash Points objects are mapped, and ensures that the necessary relationships and fields are correctly set to the billable services.

###### Header `UUID` *(optional)*
This is the UUID of the billable service

###### Header `Name` *(required)*
This is the name of the cash point such as IPD Cash Point.

###### Header `Description` *(optional)*
The description of the cash point

###### Header `Location` *(optional)*
This is a reference to the location of the cash point e.g ART Clinic

##### Service Prices

These would be payment modes for billable service.

###### Header `UUID` *(optional)*
This unique identifier represents the different payment modes.

###### Header `Name` *(required)*
This is the descriptive name of the service price item.

###### Header `Price` *(optional)*
This is the numerical value resepresenting th cost of the service.

###### Header `Payment Mode` *(optional)*
This is a reference of the accepted mode of payment e.g Cash, Credit, Insurance, Mobile Money

###### Header `Item` *(optional)*
This is a description of the item of service being prices that helps with categorising the service or item associated with the price

###### Header `Billable Service` *(optional)*
This references the billable service associated with the item

#### Requirements
* The [billing module](https://github.com/openmrs/openmrs-module-billing) version 1.0.0 or higher must be installed
* The OpenMRS version must be 2.4 or higher

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).
