## Domain 'locations'
The **locations** subfolder contains CSV import files for saving locations in bulk. This is a possible example of its content:
```bash
locations/
  ├──locations.csv
  └── ...
```
There is currently only one format for the location CSV line, here are the possible headers with a sample data set:

| <sub>Uuid</sub> | <sub>Void/Retire</sub> | <sub>Name</sub> | <sub>Description</sub> | <sub>Parent</sub> | <sub>Tags</sub> | <sub>Address1</sub> | <sub>Address2</sub> | <sub>Address3</sub> | <sub>Address4</sub> | <sub>Address5</sub> | <sub>Address6</sub> | <sub>City/Village</sub> | <sub>County/District</sub> | <sub>State/Province</sub> | <sub>Postal Code</sub> | <sub>Country</sub> | <sub>_order:1000</sub> |
| - | - | - | - | - | - | - | - | - | - | - | - | - | - | - | - | - | - |
||| <sub>The Lake Clinic-Cambodia</sub> ||| <sub>Login Location; Facility Location</sub> | <sub>Paradise Street</sub> |||||| <sub>Siem Reap</sub> | <sub>Siem Reap</sub> | <sub>Siem Reap</sub> || <sub>Cambodia</sub> ||
||| <sub>OPD Room</sub> || <sub>The Lake Clinic-Cambodia</sub> | <sub>Consultation Location</sub> |||||||||||||

Headers that start with an underscore such as `_order:1000` are metadata headers. The values in the columns under those headers are never read by the CSV parser.
<br/>Let's review some important headers.

###### Header `Name`
This is _not_ a localized header.

###### Header `Parent`
This is a pointer to the parent location. Both location UUIDs and names are supported to refer to another location.

###### Header `Tags`
The list of location tags. Location tag _names_ should be provided. Those names can point to either existing location tags, or if no existing tag is found a new tag with the specified name will be created.

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).