## Domain 'specialities'

The **specialities** subfolder contains CSV configuration files that help modify and create Bahmni appointments specialities. It should be possible in most cases to configure them via a single CSV configuration file, however there can be as many CSV files as desired.
This is a possible example of how the configuration subfolder may look like:
```bash
specialities/
  └── specialities.csv
```
The CSV configuration allows to either modify exisiting appointments specialities or to create new appointments specialities. Here is a sample CSV:

|<sub>Uuid</sub>| <sub>Void/Retire</sub> | <sub>Name</sub>            | 
|--------------------------------------|-------------|-
|  <sub>aaa1a367-3047-4833-af27-b30e2dac9028</sub> |             | <sub>Radiology</sub> |                           
| <sub>439559c2-a3a4-4a25-b4b2-1a0299e287ee</sub> |             | <sub>Cardiology</sub>   

###### Header `Name` 
The speciality **name** is mandatory and must be provided.

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).