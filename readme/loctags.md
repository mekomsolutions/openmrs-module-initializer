## Domain 'locationtags'
The **locationtags** subfolder contains CSV import files for saving location tags in bulk.

This is useful if you want to manage location tags explicitly. This compliments the
`Tag|TagName` headers of the location loader. However, location tags can also be created
dynamically by the location loader, using its `Tags` header. If using that header,
you do not need to use this location tag loader. Please see the
locations loader [docs](./loc.md) for details.

This is an example of the content of the `locationtags` subfolder:
```bash
locationtags/
  ├──locationtags.csv
  └── ...
```
There is currently only one format for the location tag CSV line,
here are the possible headers with a sample data set:

| <sub>Uuid                                </sub> | <sub>Void/Retire</sub> | <sub>Name       </sub> | <sub>Description</sub> |
|--------------------------------------|-------------|--------------------------|-------------|
| <sub>b03e395c-b881-49b7-b6fc-983f6bddc7fc</sub> | <sub>           </sub> | <sub>Yoga Location</sub> | <sub>           </sub> |
| <sub>                                    </sub> | <sub>           </sub> | <sub>Nice Sofa    </sub> | <sub>           </sub> |

<br/>

###### Header `Name` *(mandatory)*
This is _not_ a localized header.

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see
[here](../api/src/test/resources/testAppDataDir/configuration).