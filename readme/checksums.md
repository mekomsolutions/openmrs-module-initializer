## Checksum Files

### Mechanism
When a configuration file is loaded by Initializer the file's checksum is written somewhere.
<br/>When the same configuration file is attempted to be loaded again, the file's checksum is compared to the saved checksum and if they do not differ, the file is skipped.

This is a simple mechanism to minimise the configuration files loading process so that it does only what is necessary to be done.

### Where are the checksum files?
A **configuration_checksums** folder is created at the same level as the **configuration** folder:
<pre>
.
├── modules/
├── openmrs.war
├── openmrs-runtime.properties
├── ...
├── <b>configuration/</b>
└── <b>configuration_checksums/</b>
</pre>
The **configuration** folder structure is reproduced within **configuration_checksums**:
```bash
configuration_checksums/
  ├── addresshierarchy/
  ├── appointmentsspecialities/
  ├── appointmentsservicesdefinitions/
  ├── ...
```
The checksum files for each domain are grouped in each domain subfolder of **configuration_checksums**. Each configuration file has a corresponding checksum file, eg:
<pre>
├── configuration
│   └── concepts
│       └── <b>diagnoses.csv</b>
└── configuration_checksums
    └── concepts
        └── <b>diagnoses.checksum</b>
</pre>
If a configuration is nested however, the checksum files aren't, they are named in a way that ressemble the path of their corresponding configuration file. Eg.:
```bash
├── configuration
│   └── concepts
│       ├── diagnoses
│       │   ├── ncd_diagnoses.csv
│       │   └── oncology_diagnoses.csv
│       ├── drugs
│       │   └── conceptdrugs.csv
│       └── program_concepts.csv
└── configuration_checksums
    └── concepts
        ├── diagnoses_ncd_diagnoses.checksum
        ├── diagnoses_oncology_diagnoses.checksum
        ├── drugs_conceptdrugs.checksum
        └── program_concepts.checksum
```
### Deleting a checksum file
That is basically the way to say to Initializer that the corresponding configuration file should be reloaded the next time the domain will be processed.
