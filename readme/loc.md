## Domain 'locations'
The **locations** subfolder contains CSV import files for saving locations in bulk. This is a possible example of its content:
```bash
locations/
  ├──locations.csv
  └── ...
```
There is currently only one format for the location CSV line, here are the possible headers with a sample data set:

| Uuid                                 | Void/Retire | Name                     | Description | Parent                   | Tag\|Login Location | Tag\|Facility Location | Attribute\|9eca4f4e-707f-4bb8-8289-2f9b6e93803c | Attribute\|Last Audit Date | Address 1       | Address 2 | Address 3 | Address 4 | Address 5 | Address 6 | City/Village | County/District | State/Province | Postal Code | Country  | Tags                              |
|--------------------------------------|-------------|--------------------------|-------------|--------------------------|---------------------|------------------------|-------------------------------------------------|----------------------------|-----------------|-----------|-----------|-----------|-----------|-----------|--------------|-----------------|----------------|-------------|----------|-----------------------------------|
| a03e395c-b881-49b7-b6fc-983f6bddc7fc |             | The Lake Clinic-Cambodia |             |                          | TRUE                | TRUE                   | HQ Facility                                     | 2017-05-15                 | Paradise Street |           |           |           |           |           |              | Siem Reap       | Siem Reap      |             | Cambodia | Login Location; Facility Location |
|                                      |             | OPD Room                 |             | The Lake Clinic-Cambodia |                     | TRUE                   |                                                 |                            |                 |           |           |           |           |           |              |                 |                |             |          | Consultation Location             |

<br/>Let's review some important headers.

#### Header `Name` *(mandatory)*
This is _not_ a localized header.

#### Header `Parent`
This is a pointer to the parent location. Both location UUIDs and names are supported to refer 
to another location.

#### Tag headers
There are two ways to assign *location tags* to locations.

###### `Tag|` headers
Tags can be assigned in true/false columns under headers starting with `Tag|`
followed by the location tag _name_, e.g. `Tag|Login Location`.
Tags assigned in this way will _not_ be created automatically. They can be
created using the [locationtags](./loctags.md) domain.

###### Header `Tags`
A comma-delimited list of location tags specified by _name_. These can be the names 
of existing location tags, or new tags to be created ad-hoc.

#### Attribute headers
Locations support *attributes*. The values for those attributes can be set under ad-hoc headers 
starting with the special prefix `Attribute|`. The value indicated on a CSV line will be resolved 
to its final value based on the type of the attribute. Let us look at an example:

| Attribute\|Last Audit Date |
|----------------------------|
| 2017-05-15                 |

This attribute points to an attribute type identified by "`Last Audit Date`". The attribute type 
identifier (a name here) suggests that it might be an attribute of custom datatype `Date`. This 
means that its value, represented by the string `2017-05-15`, will eventually be resolved as the 
Java date `Mon May 15 00:00:00 2017` set as an attribute of the location described by the CSV line.

The RHS of the attribute header, the part that refers to the attribute type, can either point to 
an attribute type name or to an attribute type UUID.

## Further examples
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).