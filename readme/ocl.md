## About
The JSON file inside .zip file loads all the concepts exported from OCL.

The OCL package is a convenient way to load all your concepts into your EMR's dictionary. Advantages: 
* Offline: Helpful for sites without an active internet connection at the time of set-up
* Support More Sites: Having your collection(s) without having to create the concepts manually.
* Multiple Collections: There is no limit to the number of collections you can import using the /ocl config file (Just be mindful of potential clashes if the same concepts exist in multiple collections - e.g. a situation where a Question concept comes from 2 collections, but each have different Answer options) 

## Requirements
* You will need to install [the Open Concept Lab Module](https://addons.openmrs.org/show/org.openmrs.module.open-concept-lab) (aka the OCL Module or Subscription Module). 

## Domain 'ocl'
The **ocl** subfolder contains JSON file of OCL dictionary packages as .zip files to be imported. This is a possible example of its content:
```bash
ocl/
  └── test.zip
```

## Steps: How to find the .zip file in Open Concept Lab (OCL)
1. Go to the Open Concept Lab's online UI at https://app.openconceptlab.org/ (note: If your collection is private, you will need to sign-in to OCL before proceeding).
2. Go to your collection.
3. Click the "Versions" tab. 
4. Scroll through the list to find the specific Version you want. Then click the download icon (:arrow_down:), and select the _Export_ option. 
<img width="500" alt="image" src="https://user-images.githubusercontent.com/67400059/160782124-f9c6ea69-59fa-45ab-b7b8-e4ed80ed8829.png">

5. Now upload your .zip file to your /ocl config file (and remove any old versions no longer required) - that's it!


### Further examples:
* See how the /ocl file is used in the OpenMRS 3.x RefApp distro: https://github.com/openmrs/openmrs-distro-referenceapplication/tree/main/distro/configuration/ocl
* Please look at the test configuration folder for sample import files for all domains, see [here](../api/src/test/resources/testAppDataDir/configuration).

## Video Tutorial: 📽️

[![How to use the /ocl config file for uploading concepts](https://img.youtube.com/vi/AYXjCg0y51U/0.jpg)](https://youtu.be/AYXjCg0y51U)

