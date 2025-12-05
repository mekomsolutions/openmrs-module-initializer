## Domain 'flags'
The **Flags** subfolder contains CSV import files for saving Patient Flags which are used to mark patients with specific conditions or criteria. Below is a possible example of its content:

```bash
flags/
  ├──flags.csv
  └── ...
```
Here are the possible headers with a sample data set:

| <sub>Uuid</sub>                                 | <sub>name</sub> | <sub>criteria</sub> | <sub>evaluator</sub> | <sub>message</sub> | <sub>priority</sub> | <sub>enabled</sub> | <sub>tags</sub> | <sub>description</sub> |
|--------------------------------------|-------------|-----------------------------|----------------|----------------|----------------|----------------|----------------|----------------|
| <sub>526bf278-ba81-4436-b867-c2f6641d060a</sub> |   <sub>HIV Positive</sub>         | <sub>SELECT patient_id FROM patient WHERE ...</sub> | <sub>org.openmrs.module.patientflags.evaluator.SqlFlagEvaluator</sub> | <sub>patientflags.message.hivPositive</sub> | <sub>High Priority</sub> | <sub>true</sub> | <sub>HIV;Clinical</sub> | <sub>Flag for HIV positive patients</sub> |

Let's review the headers as below

###### Header `UUID` *(optional)*
This unique identifier represents the different flags.

###### Header `Name` *(required)*
This is the descriptive name of the flag.

###### Header `Criteria` *(required)*
This is the criteria expression used to determine whether a flag should be triggered for a patient. The format depends on the evaluator type being used.

###### Header `Evaluator` *(required)*
This is the fully qualified class name of the FlagEvaluator implementation to use when evaluating the flag. Examples:
- `org.openmrs.module.patientflags.evaluator.SqlFlagEvaluator` - for SQL-based flags
- `org.openmrs.module.patientflags.evaluator.GroovyFlagEvaluator` - for Groovy script-based flags
- `org.openmrs.module.drools.patientflags.DroolsFlagEvaluator` - for Drools session-based flags

###### Header `Message` *(required)*
This is the message key (from message properties) or the actual message text to display when the flag is triggered.

###### Header `Priority` *(optional)*
This references a Priority entity by UUID or name. The priority determines the visual styling and importance level of the flag.

###### Header `Enabled` *(optional)*
Whether the flag is enabled (true/false). Defaults to `true` if not specified.

###### Header `Tags` *(optional)*
A semi-colon separated list of Tag identifiers (UUID or name) associated with the flag. Tags are used to determine where flags are displayed in the UI.

###### Header `Description` *(optional)*
A description of the flag.

#### Requirements
* The [patientflags module](https://github.com/openmrs/openmrs-module-patientflags) version 3.0 or higher must be installed
* The OpenMRS version must be 2.2 or higher

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api-2.4/src/test/resources/testAppDataDir/configuration).

