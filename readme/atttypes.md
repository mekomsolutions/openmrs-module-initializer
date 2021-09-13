## Domain 'attributetypes'
Firstly let us be reminded that *person* attribute types are handled separately, see [here](atttypes.md).

Secondly, program attribute types are supported for OpenMRS core versions of `2.2.*` and above or OpenMRS core `2.1.*` with any version of the bahmni-core module loaded. 

The **attributetypes** subfolder contains CSV import files for saving attribute types in bulk. This is a possible example of its content:
```bash
attributetypes/
  ├── attributetypes.csv
  └── ...
```
See below a sample CSV file that processes a couple of attributes types:

| <sub>Uuid                                </sub> | <sub>Void/Retire</sub> | <sub>Entity name</sub> | <sub>Name                        </sub> | <sub>Description                          </sub> | <sub>Min occurs</sub> | <sub>Max occurs</sub> | <sub>Datatype classname                                  </sub> | <sub>Datatype config</sub> | <sub>Preferred handler classname</sub> | <sub>Handler config</sub> | 
|--------------------------------------|-------------|-------------|------------------------------|---------------------------------------|------------|------------|------------------------------------------------------|-----------------|-----------------------------|----------------| 
| <sub>0bb29984-3193-11e7-93ae-92367f002671</sub> | <sub>           </sub> | <sub>Location   </sub> | <sub>Location Height             </sub> | <sub>Location Height's description        </sub> | <sub>1         </sub> | <sub>1         </sub> | <sub>org.openmrs.customdatatype.datatype.FloatDatatype   </sub> | <sub>               </sub> | <sub>                           </sub> | <sub>              </sub> | <sub>
| <sub>0bc29982-3193-11e3-93ae-92367f222671</sub> | <sub>           </sub> | <sub>Visit      </sub> | <sub>Visit Color                 </sub> | <sub>Visit Color's description            </sub> | <sub>1         </sub> | <sub>1         </sub> | <sub>org.openmrs.customdatatype.datatype.FreeTextDatatype</sub> | <sub>               </sub> | <sub>                           </sub> | <sub>              </sub> | <sub>
| <sub>9eca4f4e-707f-4bb8-8289-2f9b6e93803c</sub> | <sub>           </sub> | <sub>Location   </sub> | <sub>Location ISO Code           </sub> | <sub>Location ISO Code's description      </sub> | <sub>1         </sub> | <sub>10        </sub> | <sub>org.openmrs.customdatatype.datatype.FreeTextDatatype</sub> | <sub>               </sub> | <sub>                           </sub> | <sub>              </sub> | <sub>
| <sub>                                    </sub> | <sub>           </sub> | <sub>Provider   </sub> | <sub>Provider Speciality         </sub> | <sub>Clinical speciality for this provider</sub> | <sub>0         </sub> | <sub>7         </sub> | <sub>org.openmrs.customdatatype.datatype.FreeTextDatatype</sub> | <sub>               </sub> | <sub>                           </sub> | <sub>              </sub> | <sub>
| <sub>                                    </sub> | <sub>TRUE       </sub> | <sub>Provider   </sub> | <sub>Provider Rating             </sub> | <sub>                                     </sub> | <sub>          </sub> | <sub>          </sub> | <sub>                                                    </sub> | <sub>               </sub> | <sub>                           </sub> | <sub>              </sub> | <sub>
| <sub>7d002484-0fcd-4759-a67a-04dbf8fdaab1</sub> | <sub>           </sub> | <sub>Concept    </sub> | <sub>Concept Location            </sub> | <sub>                                     </sub> | <sub>1         </sub> | <sub>1         </sub> | <sub>org.openmrs.customdatatype.datatype.LocationDatatype</sub> | <sub>               </sub> | <sub>                           </sub> | <sub>              </sub> | <sub>
| <sub>3884c889-35f5-47b4-a6b7-5b1165cee218</sub> | <sub>           </sub> | <sub>Program    </sub> | <sub>Program Assessment          </sub> | <sub>Program Assessment's description     </sub> | <sub>          </sub> | <sub>          </sub> | <sub>org.openmrs.customdatatype.datatype.FreeTextDatatype</sub> | <sub>               </sub> | <sub>                           </sub> | <sub>              </sub> | <sub>
| <sub>b1d98f27-c058-46f2-9c12-87dd7c92f7e3</sub> | <sub>           </sub> | <sub>Program    </sub> | <sub>Program Efficiency Indicator</sub> | <sub>Metric of the program efficiency     </sub> | <sub>0         </sub> | <sub>1         </sub> | <sub>org.openmrs.customdatatype.datatype.FloatDatatype   </sub> | <sub>               </sub> | <sub>                           </sub> | <sub>              </sub> | <sub>
| <sub>                                    </sub> | <sub>TRUE       </sub> | <sub>Concept    </sub> | <sub>Concept Family              </sub> | <sub>                                     </sub> | <sub>          </sub> | <sub>          </sub> | <sub>                                                    </sub> |               </sub> | <sub>                           </sub> | <sub>              </sub> |

Let's review some important headers.

###### Header `Entity name` *(mandatory)*
This points to the attribute type entity, the possible value are: `Concept`, `Location`, `Program`, `Provider`, `Visit`.

###### Header `Name` *(mandatory)*
This is _not_ a localized header.

###### Header `Datatype classname` *(mandatory)*
This points to the Java type of the attribute that is defined by the attribute type. [Here](https://github.com/openmrs/openmrs-core/tree/2.1.1/api/src/main/java/org/openmrs/customdatatype/datatype) is the list of possible values.

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).