## Domain 'metadatasets'
The **metadatasets** subfolder contains CSV import files for saving metadata terms mappings in bulk. This is a possible example of its content:
```bash
metadatasets/
  ├──metadatasets.csv
  └── ...
```
There is currently only one format for the CSV line, here are the possible headers with a sample data set:

| <sub>Uuid</sub> |<sub>Void/Retire</sub> | <sub>Name</sub> | <sub>Description</sub> | <sub>_order:1000</sub> |
| - | - | - | - | - |
| <sub>f0ebcb99-7618-41b7-b0bf-8ff93de67b9e</sub> | | <sub>Extra Identifiers Set</sub> | <sub>Set of extra patient identifiers</sub> | |

Headers that start with an underscore such as `_order:1000` are metadata headers. The values in the columns under those headers are never read by the CSV parser.

Metadata sets expose the usual OpenMRS metadata headers:

###### Header `Name`
Metadata sets names are *optional* and are not used as unique identifiers for metadata sets.

###### Header `Description`

## Domain 'metadatasetmembers'
The **metadatasetmembers** subfolder contains CSV import files for saving metadata terms mappings in bulk. This is a possible example of its content:
```bash
metadatasetmembers/
  metadatasetmembers.csv
  └── ...
```
There is currently only one format for the CSV line, here are the possible headers with a sample data set:

| <sub>Uuid                                </sub> | <sub>Void/Retire</sub> | <sub>Name          </sub> | <sub>Description           </sub> | <sub>Metadata set uuid                   </sub> | <sub>Metadata class                   </sub> | <sub>Metadata uuid                       </sub> | <sub>Sort weight</sub> | <sub>_order:1000 |
|--------------------------------------|-------------|----------------|------------------------|--------------------------------------|-----------------------------------|--------------------------------------|-------------|-------------|
| <sub>ee00777d-0cbe-41b7-4c67-8ff93de67b9e</sub> | <sub>           </sub> | <sub>Legacy Id     </sub> | <sub>                      </sub> | <sub>f0ebcb99-7618-41b7-b0bf-8ff93de67b9e</sub> | <sub>org.openmrs.PatientIdentifierType</sub> | <sub>n0ebcb90-m618-n1b1-b0bf-kff93de97b9j</sub> | <sub>           </sub> | <sub>            |
| <sub>f0ebcb99-272d-41b7-4c67-078de9342492</sub> | <sub>TRUE       </sub> | <sub>Old OpenMRS Id</sub> | <sub>                      </sub> | <sub>f0ebcb99-7618-41b7-b0bf-8ff93de67b9e</sub> | <sub>org.openmrs.PatientIdentifierType</sub> | <sub>8f6ed8bb-0cbe-4c67-bc45-c5c0320e1324</sub> | <sub>           </sub> | <sub>            |
| <sub>dbfd899d-e9e1-4059-8992-73737c924f88</sub> | <sub>           </sub> | <sub>Outpatient Id </sub> | <sub>IdentifierType for OPD</sub> | <sub>f0ebcb99-7618-41b7-b0bf-8ff93de67b9e</sub> | <sub>org.openmrs.PatientIdentifierType</sub> | <sub>7b0f5697-27e3-40c4-8bae-f4049abfb4ed</sub> | <sub>34</sub>          |             |

Each line describes how a metata set member belongs to a metata *set*. Let's review the key headers:

###### Header `Name`
Metadata set members names are *optional* and are not used as unique identifiers.

###### Header `Description`

###### Header `Metadata set uuid` *(mandatory)*
The UUID of the set to which the member belongs.

###### Header `Metadata class` *(mandatory)*
The Java class of the metadata entity that the member represents.

###### Header `Metadata uuid` *(mandatory)*
The UUID of the metadata entity that the member represents.

###### Header `Sort weight`
See [here](https://github.com/openmrs/openmrs-module-metadatamapping/blob/dcf20262bced23af2a6556696079f5df637fe642/api/src/main/java/org/openmrs/module/metadatamapping/MetadataSetMember.java#L102).


## Domain 'metadatatermmappings'
The **metadatatermmappings** subfolder contains CSV import files for saving metadata terms mappings in bulk. This is a possible example of its content:
```bash
metadatatermmappings/
  ├──metadataterms.csv
  └── ...
```
There is currently only one format for the CSV line, here are the possible headers with a sample data set:

| <sub>Uuid</sub> |<sub>Void/Retire</sub> | <sub>Mapping source</sub> | <sub>Mapping code</sub>  | <sub>Metadata class name</sub>  | <sub>Metadata uuid</sub> | <sub>_order:1000</sub> |
| - | - | - | - | - | - | - |
| <sub>21e24b36-f9e3-4b0e-986d-9899665597f7</sub> | | <sub>org.openmrs.module.emrapi</sub> | <sub>emr.primaryIdentifierType</sub> | <sub>org.openmrs.PatientIdentifierType</sub>  | <sub>264c9e75-77da-486a-8361-31558e051930</sub>  | |

Headers that start with an underscore such as `_order:1000` are metadata headers. The values in the columns under those headers are never read by the CSV parser.

Let's review some important headers.

###### Header `Mapping source` *(mandatory)*
A logical grouping of metadata terms mappings.

###### Header `Mapping code` *(mandatory)*
The code part of the metadata term mapping. That is a unique code, in the scope of the source, that points to and represents the metadata. Together the mapping source and the mapping code form the metadata term mapping that identify univocally a piece of metadata.

###### Header `Metadata class name` *(mandatory)*
The complete Java class name of the metadata object, eg. `org.openmrs.PatientIdentifierType`, `org.openmrs.Location`, ... etc.

###### Header `Metadata uuid` *(mandatory)*
The UUID of the metadata object represented by the metadata term mapping.

---

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).