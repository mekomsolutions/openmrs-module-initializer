## Domain 'metadatasharing'
The **metadatasharing** subfolder contains all the Metadata Sharing (MDS) packages as .zip files to be imported. This is a possible example of its content:
```bash
metadatasharing/
  ├── PatientIdentifierType.zip
  ├── PersonAttributeType.zip
  └── ...
```
There can be as many MDS packages as desired. Providing multiples .zip files allows to split the metadata to be imported by areas, categories or any other segmentation that the implementors deem relevant.
<br/>They will all be imported following the 'prefer theirs' rule, meaning that the the metadata shipped with the packages is considered being the master metadata. Existing objects will be overwritten, missing objects will be created... etc.
<br/>MDS packages are a convenient way to bring in metadata, especially while other methods have not yet been implemented. However when otherwise possible, other ways should be preferred.

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).