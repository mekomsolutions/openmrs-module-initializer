## Domain 'paymentmodes'
The **Payment Modes** subfolder contains CSV import files for saving Payment Modes which are the modes of payment tied to a single billable service, that is, the form of payment e.g. Cash, Insurance, PayPal, Mobile Money etc. Below is a possible example of its content:

```bash
paymentmodes/
  ├──paymentModes.csv
  └── ...
```
Here are the possible headers with a sample data set:

| <sub>Uuid</sub>                                 | <sub>name</sub> | <sub>attributes</sub>                        |
|--------------------------------------|-------------|-----------------------------|
| <sub>526bf278-ba81-4436-b867-c2f6641d060a</sub> |   </sub>Paypal</sub>         | <sub>Maximum :: Numeric :: example-regex :: True;Minimum</sub> |

Let's review the headers as below

###### Header `UUID` *(optional)*
This unique identifier represents the different payment modes.

###### Header `Name` *(required)*
This is the descriptive name of the service price item.

###### Header `Attributes` *(optional)*
A semi-colon separated list of attributes, further divided by the `::` delimiter into `name`, `format`, `regex`, and `required` properties (in that order) of a payment mode's attribute type. For example, in the dataset provided:

First attribute type
* `name`: Maximum
* `format`: Numeric
* `regex`: example-regex
* `required`: True

Second attribute type
* name: Minimum

#### Requirements
#### Billing V1
* The [billing module](https://github.com/openmrs/openmrs-module-billing) version 1.1.0 or higher must be installed
* The OpenMRS version must be 2.4 or higher

#### Billing V2
* The [billing module](https://github.com/openmrs/openmrs-module-billing) version 2.0.0 or higher must be installed
* The OpenMRS version must be 2.7.8 or higher

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api-2.4/src/test/resources/testAppDataDir/configuration).
