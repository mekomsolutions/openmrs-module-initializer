## Domain 'locations'
The **locations** subfolder contains CSV import files for saving locations in bulk. This is a possible example of its content:
```bash
locations/
  ├──locations.csv
  └── ...
```
There is currently only one format for the location CSV line, here are the possible headers with a sample data set:

| <sub>Uuid                                </sub> | <sub>Void/Retire</sub> | <sub>Name                    </sub> | <sub>Description</sub> | <sub>Parent                  </sub> | <sub>Tags                             </sub> | <sub>Attribute\|9eca4f4e-707f-4bb8-8289-2f9b6e93803c</sub> | <sub>Attribute\|Last Audit Date</sub> | <sub>Address 1      </sub> | <sub>Address 2</sub> | <sub>Address 3</sub> | <sub>Address 4</sub> | <sub>Address 5</sub> | <sub>Address 6</sub> | <sub>City/Village</sub> | <sub>County/District</sub> | <sub>State/Province</sub> | <sub>Postal Code</sub> | <sub>Country </sub> |
|--------------------------------------|-------------|--------------------------|-------------|--------------------------|-----------------------------------|------------------------------------------------|---------------------------|-----------------|-----------|-----------|-----------|-----------|-----------|--------------|-----------------|----------------|-------------|----------|
| <sub>a03e395c-b881-49b7-b6fc-983f6bddc7fc</sub> | <sub>           </sub> | <sub>The Lake Clinic-Cambodia</sub> | <sub>           </sub> | <sub>                        </sub> | <sub>Login Location; Facility Location</sub> | <sub>HQ Facility                                   </sub> | <sub>2017-05-15               </sub> | <sub>Paradise Street</sub> | <sub>         </sub> | <sub>         </sub> | <sub>         </sub> | <sub>         </sub> | <sub>         </sub> | <sub>            </sub> | <sub>Siem Reap      </sub> | <sub>Siem Reap     </sub> | <sub>           </sub> | <sub>Cambodia</sub> |
| <sub>                                    </sub> | <sub>           </sub> | <sub>OPD Room                </sub> | <sub>           </sub> | <sub>The Lake Clinic-Cambodia</sub> | <sub>Consultation Location            </sub> | <sub>                                              </sub> | <sub>                         </sub> | <sub>               </sub> | <sub>         </sub> | <sub>         </sub> | <sub>         </sub> | <sub>         </sub> | <sub>         </sub> | <sub>            </sub> | <sub>               </sub> | <sub>              </sub> | <sub>           </sub> | <sub>        </sub> |

<br/>Let's review some important headers.

###### Header `Name` *(mandatory)*
This is _not_ a localized header.

###### Header `Parent`
This is a pointer to the parent location. Both location UUIDs and names are supported to refer to another location.

###### Header `Tags`
The list of location tags. Location tag _names_ should be provided. Those names can point to either existing location tags, or if no existing tag is found a new tag with the specified name will be created.

###### Attribute headers
Locations support *attributes*. The values for those attributes can be set under ad-hoc headers starting with the special prefix `Attribute|`. The value indicated on a CSV line will be resolved to its final value based on the type of the attribute. Let us look at an example:

| Attribute\|Last Audit Date |
|---------------------------|
| 2017-05-15                |

This attribute points to an attribute type identified by "`Last Audit Date`". The attribute type identifier (a name here) suggests that it might be an attribute of custom datatype `Date`. This means that its value, represented by the string `2017-05-15`, will eventually be resolved as the Java date `Mon May 15 00:00:00 2017` set as an attribute of the location described by the CSV line.

The RHS of the attribute header, the part that refers to the attribute type, can either point to an attribute type name or to an attribute type UUID.

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).