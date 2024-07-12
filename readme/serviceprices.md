## Domain 'serviceprices'
The **Service Prices** subfolder contains CSV import files for saving Service Prices which are the prices tied to a single billable service, that is, amount payable for a service including the form of payment e.g. Cash, Insurance, PayPal, Mobile Money etc. Below is a possible example of its content:

```bash
serviceprices/
  ├──ServicePrices.csv
  └── ...
```
There is currently only one format for the Service Prices CSV line, here are the possible headers with a sample data set:

| <sub>Uuid</sub>                                 | <sub>name</sub> | <sub>price</sub>                        | <sub>paymentMode</sub>   | <sub>item</sub>            | <sub>billableService</sub>            |
|--------------------------------------|-------------|-----------------------------|---------------|--------------------|--------------------|
| <sub>526bf278-ba81-4436-b867-c2f6641d060a</sub> |   </sub>Antenatal Cash Item</sub>         | <sub>10000</sub> | <sub>Cash</sub> | <sub> </sub> |<sub>Antenatal Care</sub> |

Let's review the headers as below

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
* The [billing module](https://github.com/openmrs/openmrs-module-billing) version 1.1.0 or higher must be installed
* The OpenMRS version must be 2.4 or higher

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api-2.4/src/test/resources/testAppDataDir/configuration).