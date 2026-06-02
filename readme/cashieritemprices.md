## Domain 'cashieritemprices'
The **Cashier Item Prices** subfolder contains CSV import files for saving Cashier Item Prices. A cashier item price ties a `BillableService` *or* a `StockItem` to a `PaymentMode` and a `BigDecimal` price.

```bash
cashieritemprices/
  ├──cashierItemPrices.csv
  └── ...
```
Below are the possible headers with a sample data set:

| <sub>Uuid</sub>                                 | <sub>Name</sub>                | <sub>Price</sub>  | <sub>Payment Mode</sub>                        | <sub>Stock Item</sub>                            | <sub>Billable Service</sub>            |
|-------------------------------------------------|--------------------------------|-------------------|------------------------------------------------|--------------------------------------------------|----------------------------------------|
| <sub>c1c1c1c1-0000-0000-0000-000000000001</sub> | <sub>ANC Service Price</sub>   | <sub>150.00</sub> | <sub>526bf278-ba81-4436-b867-c2f6641d060a</sub>| <sub></sub>                                      | <sub>a0f7d8a1-4fa2-418c-aa8a-cccccccc0001</sub> |
| <sub>c1c1c1c1-0000-0000-0000-000000000004</sub> | <sub>Paracetamol Item Price</sub>| <sub>20.00</sub> | <sub>526bf278-ba81-4436-b867-c2f6641d060a</sub>| <sub>b2b2b2b2-0000-0000-0000-000000000001</sub>  | <sub></sub>                            |

Let's review the headers as below.

###### Header `Uuid` *(optional)*
The UUID of the cashier item price. If empty a random UUID is generated; if provided, the row updates the existing item price with that UUID (or creates one with that UUID).

###### Header `Void/Retire` *(optional)*
When set to `true`, the rest of the row is ignored and the existing cashier item price (identified by `Uuid`) is retired. See [CSV conventions](csv_conventions.md).

###### Header `Name` *(required)*
The display name of the item price.

###### Header `Price` *(required)*
The price amount, parsed as a `BigDecimal` (e.g. `150.00`).

###### Header `Payment Mode` *(required)*
The UUID of an existing `PaymentMode`.

###### Header `Stock Item` *(required if `Billable Service` is empty)*
The UUID of an existing stockmanagement `StockItem`. The Initializer module does not load stock items; the referenced stock item must already exist in the database (for example, created through the stockmanagement module's UI).

###### Header `Billable Service` *(required if `Stock Item` is empty)*
The UUID of an existing `BillableService`. See [Billable Services](billableservices.md).

**Exactly one** of `Stock Item` or `Billable Service` must be set on each row. Rows that set both, or neither, are rejected.

#### Requirements
* The [billing module](https://github.com/openmrs/openmrs-module-billing) version 2.0.0 or higher must be installed.
* The [stockmanagement module](https://github.com/openmrs/openmrs-module-stockmanagement) must be installed if `Stock Item` references are used.
* The OpenMRS version must be 2.7.8 or higher.

#### Further examples:
Please look at the test configuration folder for sample import files for all domains, see [here](../api-2.7/src/test/resources/testAppDataDir/configuration).