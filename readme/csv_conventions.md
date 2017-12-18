## Conventions for CSV files
There are a number of conventions that apply to CSV files across all domains, typically the meaning of certain columns (or headers). Headers are in general optional, it is up to the implementor to provide or not the fields that are required to create or edit the OpenMRS objects. Mandatory headers will be specifically indicated.
<br/>Each CSV line is meant to provide enough data to create, edit or void/retire an OpenMRS object of the domain.

###### Header `Uuid` (mandatory)
* If the value under this header is missing the OpenMRS object will be created with a new random UUID.
<br/>_However if the domain supports secondary identifiers, those will be used to fetch the object if the UUID is missing._
* If the value under this header is provided the initializer will attempt to retrieve the OpenMRS object that may already exist with this UUID.
  * If the OpenMRS object already exists it will be modified and saved again according to the CSV line.
  * If the OpenMRS object doesn't exist yet it will be created with the UUID specified on the CSV line.

###### Header `Void/Retire`
Set this to **true** to indicate that the OpenMRS object with the provided UUID should be voided or retired.
<br/>When `Void/Retire` is set to true, the parsing of the remaining of the CSV line is interrupted since the only objective is to retire the concept. And to this end, only the UUID and the retire flag are needed.

###### CSV metadata headers
Special headers are used to provide metadata information about the CSV file itself.
<br/>All metadata headers start with an underscore, eg. `_version:1`, `_order:1000`, ... etc.

###### Header `_version:*`
Versions are primarily introduced to allow for evolutions in the implementation of CSV parsers. Those evolutions may require to modify the CSV headers which will likely lead to backward compatibility issues. Using versions works around this by ensuring that specific parsers are used based on the version specified via the CSV file header.
<br/>When no version header is provided the initializer will fallback on a default one or may complain in its log messages that no version was specified.

###### Header `_order:*`
This metadata header specifies the order of loading of the CSV file _within the domain_. In many cases the creation of OpenMRS objects relies on the existence of other OpenMRS objects that are referred to. This use case is covered by the order header that allows to control the order of loading of files in a given domain.
<br/>For example `_order:1000` indicates that all CSV files with an order smaller than 1,000 will be processed _before_ this file within the domain.
<br/> If the order metadata cannot be parsed or is missing, then the file will be processed _after_ all the ordered CSV files of the domain. However if several CSV files have no order defined, then the loading order between them is undefined.