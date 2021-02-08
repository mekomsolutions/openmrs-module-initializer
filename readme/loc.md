## Domain 'locations'
The **locations** subfolder contains CSV import files for saving locations in bulk. This is a possible example of its content:
```bash
locations/
  ├──locations.csv
  └── ...
```
There is currently only one format for the location CSV line, here are the possible headers with a sample data set:

| <sub>Uuid</sub>                                 | <sub>Void/Retire</sub> | <sub>Name</sub>                     | <sub>Description</sub> | <sub>Parent</sub>                   | <sub>Tag\|Login Location</sub> | <sub>Tag\|Facility Location</sub> | <sub>Attribute\|9eca4f4e-707f-4bb8-8289-2f9b6e93803c</sub> | <sub>Attribute\|Last Audit Date</sub> | <sub>Address 1</sub>       | <sub>Address 2</sub> | <sub>Address 3</sub> | <sub>Address 4</sub> | <sub>Address 5</sub> | <sub>Address 6</sub> | <sub>City/Village</sub> | <sub>County/District</sub> | <sub>State/Province</sub> | <sub>Postal Code</sub> | <sub>Country</sub>  | <sub>Tags</sub>                              |
|--------------------------------------|-------------|--------------------------|-------------|--------------------------|---------------------|------------------------|-------------------------------------------------|----------------------------|-----------------|-----------|-----------|-----------|-----------|-----------|--------------|-----------------|----------------|-------------|----------|-----------------------------------|
| <sub>a03e395c-b881-49b7-b6fc-983f6bddc7fc</sub> |             | <sub>The Lake Clinic-Cambodia</sub> |             |                          | <sub>TRUE</sub>                | <sub>TRUE</sub>                   | <sub>HQ Facility</sub>                                     | <sub>2017-05-15</sub>                 | <sub>Paradise Street</sub> |           |           |           |           |           |              | <sub>Siem Reap</sub>       | <sub>Siem Reap</sub>      |             | <sub>Cambodia</sub> | <sub>Login Location; Facility Location</sub> |
|                                      |             | <sub>OPD Room</sub>                 |             | <sub>The Lake Clinic-Cambodia</sub> |                     | <sub>TRUE</sub>                   |                                                 |                            |                 |           |           |           |           |           |              |                 |                |             |          | <sub>Consultation Location</sub>             |

<br/>Let's review some important headers.

#### Header `Name` *(mandatory)*
This is _not_ a localized header.

#### Header `Parent`
This is a pointer to the parent location. Both location UUIDs and names are supported to refer 
to another location.

#### Tag headers
There are two ways to assign *location tags* to locations. Choose one or the other.
Note that tag assignments are processed in the following way:

1. All tags are cleared from the location.
2. The location is assigned tags from the `Tags` header. 
3. Tags are assigned from the `Tag|` headers which are `true`.
  Setting the header to false has no further effect.

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

| <sub>Attribute\|Last Audit Date</sub> |
|----------------------------|
| <sub>2017-05-15</sub>                 |

This attribute points to an attribute type identified by "`Last Audit Date`". The attribute type 
identifier (a name here) suggests that it might be an attribute of custom datatype `Date`. This 
means that its value, represented by the string `2017-05-15`, will eventually be resolved as the 
Java date `Mon May 15 00:00:00 2017` set as an attribute of the location described by the CSV line.

The RHS of the attribute header, the part that refers to the attribute type, can either point to 
an attribute type name or to an attribute type UUID.

## Further examples
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).