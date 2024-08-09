## Domain 'billableservices'
The **Billable Services** subfolder contains CSV import files for saving Billable Services which are services offered at a facility that can be billed to client. Below is a possible example of its content:

```bash
billableservice/
  ├──services.csv
  └── ...
```
Below are the possible headers with a sample data set:

| <sub>Uuid</sub>                                 | <sub>Service Name</sub> | <sub>Short Name</sub>                        | <sub>Concept</sub>   | <sub>Service Type</sub>            | <sub>Service Status</sub>            |
|--------------------------------------|-------------|-----------------------------|---------------|--------------------|--------------------|
| <sub>32176576-1652-4835-8736-826eb0237482</sub> |   </sub>Antenatal Care</sub>         | <sub>ANTC</sub> | <sub>1592AAAAAA AAAAAAAAAAAAAAAA AAAAAAAAAA</sub> | <sub>Antenatal Services</sub> |<sub>Enabled</sub> |

Let's review the headers as below

 ###### Header `UUID` *(required)*
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

#### Requirements
* The [billing module](https://github.com/openmrs/openmrs-module-billing) version 1.1.0 or higher must be installed
* The OpenMRS version must be 2.4 or higher

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api-2.4/src/test/resources/testAppDataDir/configuration).
