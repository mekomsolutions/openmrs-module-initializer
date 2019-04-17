# Programs, Workflows and States
## Domain 'programs'
The **programs** subfolder contains CSV import files for saving programs in bulk. This is a possible example of its content:
```bash
programs/
  ├── opd_programs.csv
  └── ...
```
There is currently only one format for the program CSV line, here is an example:

| <sub>Uuid</sub> | <sub>Void/Retire</sub> | <sub>Program concept</sub> | <sub>Outcomes concept</sub> | <sub>_order:1000</sub> |
| - | - | - | - | - |
| <sub>eae98b4c-e195-403b-b34a-82d94103b2c0</sub> | | <sub>TB Program</sub> | <sub>TB Program Outcomes</sub> | |


<br/>Let's review the headers.

###### Header `Program concept`
This is a reference (UUID, same as mapping or name) to the underlying concept that defines the program. The program name and description will be inferred from this concept and cannot be provided directly.

###### Header `Outcomes concept`
This is a reference (UUID, same as mapping or name) to the concept that defines the outcomes of the program, typically a set.

## Domain 'programworkflows'
The **programworkflows** subfolder contains CSV import files for saving program workflows in bulk while linking them to their programs. This is a possible example of its content:
```bash
programworkflows/
  ├── opd_program_workflows.csv
  └── ...
```
There is currently only one format for the program CSV line, here is an example:

| <sub>Uuid</sub> | <sub>Void/Retire</sub> | <sub>Program</sub> | <sub>Workflow concept</sub> | <sub>_order:1000</sub> |
| - | - | - | - | - |
| <sub>2b98bc76-245c-11e1-9cf0-00248140a5eb</sub> | | <sub>TB Program</sub> | <sub>TB Treatment Status (workflow)</sub> | |


<br/>Let's review the headers.

###### Header `Program`
This is a reference to the program that the workflow should be added to. It can be either the program name, the program UUID or a reference to the program's underlying defining concept (concept name, concept UUID or 'same as' concept mapping).

A workflow can only added to one program, attempting to add to another program an existing workflow that is already added to a program will not produce any changes to the workflow (and an error will be logged.)

###### Header `Workflow concept`
This is a reference (UUID, same as mapping or name) to the underlying concept that defines the program workflow. The program workflow name and description will be inferred from this concept and cannot be provided directly.

## Domain 'programworkflowstates'
The **programworkflowstates** subfolder contains CSV import files for saving program workflow states in bulk while linking them to their workflows. This is a possible example of its content:
```bash
programworkflows/
  ├── opd_program_workflow_states.csv
  └── ...
```
There is currently only one format for the program CSV line, here is an example:

| <sub>Uuid</sub> | <sub>Void/Retire</sub> | <sub>Workflow</sub> | <sub>State concept</sub>  | <sub>Initial</sub>  | <sub>Terminal</sub> | <sub>_order:1000</sub> |
| - | - | - | - | - | - | - |
| <sub>cfa241f4-2700-102b-80cb-0017a47871b2</sub> | | <sub>TB Treatment Status (workflow)</sub> | <sub>Active treatment (initial)</sub> | <sub>true</sub> | | |
| <sub>88b717c0-f580-497a-8d2b-026b60dd6bfd</sub> | | <sub>TB Treatment Status (workflow)</sub> | <sub>Transferred out</sub> | | <sub>true</sub> | |


<br/>Let's review the headers.

###### Header `Workflow`
This is a reference to the workflow that the state should be added to. It can be either the workflow UUID or a reference to the workflow's underlying defining concept (concept name, concept UUID or 'same as' concept mapping).

A state can only added to one workflow, attempting to add to another workflow an existing state that is already added to a workflow will not produce any changes to the state (and an error will be logged.)

###### Header `State concept`
This is a reference (UUID, same as mapping or name) to the underlying concept that defines the program workflow state. The program workflow state name and description will be inferred from this concept and cannot be provided directly.

###### Header `Initial`
Set this to a true value to indicate that the state is an _initial_ state of the workflow.
Leave this blank or set this to a false value to indicate that the state is not an _initial_ state of the workflow.

###### Header `Terminal`
Set this to a true value to indicate that the state is an _terminal_ state of the workflow.
Leave this blank or set this to a false value to indicate that the state is not an _terminal_ state of the workflow.