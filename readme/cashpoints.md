## Domain 'cashpoints'
**Cash Points** subfolder contains CSV import files for saving Cash Points that are locations where a bill has been made over a billable service in a facility e.g. ART Clinic, OPD among others. This is a possible example of its content:

```bash
cashpoints/
  ├──cashPoints.csv
  └── ...
```
Below are the possible headers with a sample data set:

| <sub>Uuid</sub>                                 | <sub>name</sub> | <sub>description</sub>                        | <sub>location</sub>   |
|--------------------------------------|-------------|-----------------------------|---------------|
| <sub>54065383-b4d4-42d2-af4d-d250a1fd2590</sub> |   </sub>OPD Cash Point</sub>         | <sub>Opd cash point for billing</sub> | <sub>ART Clinic</sub> |

Let's review the headers as below

###### Header `UUID` *(optional)*
This is the UUID of the billable service

###### Header `Name` *(required)*
This is the name of the cash point such as OPD Cash Point.

###### Header `Description` *(optional)*
The description of the cash point

###### Header `Location` *(optional)*
This is a reference to the location of the cash point e.g ART Clinic

#### Requirements
* The [billing module](https://github.com/openmrs/openmrs-module-billing) version 1.1.0 or higher must be installed
* The OpenMRS version must be 2.4 or higher

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api-2.4/src/test/resources/testAppDataDir/configuration).