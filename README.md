# SYSC4806A-Group4-Amazin-Online-Bookstore 📚

The goal of this project is to develop an **online bookstore application** with **user authentication**, **product catalog**, and **order processing** capabilities.

---

## Project Status: Current Sprint (Sprint #6)

Kanban board status as of: **December 1, 2025**

|Kanban Column	| Key Deliverables/Issues                                                                                                                                                                                                                                                                                                                                                                                                       |
|---|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Done** | - Created and maintained mock MVC tests (#56)<br>- Fixed a bug where action buttons would disappear on search (#73)<br>- Regenerated UML (#78)<br>- Enhanced purchase page (#63)<br>- Added credit card validation and simulated credit cards (#44)<br>- Removed stock decrement on add to cart (#74)<br>- Added route back to homepage from Login page (#51)<br>- Added favicon (#47)<br>- Fixed purchase logic to decrement inventory (#75)<br>- Added tests for recommendations (#66)<br>- Improved and optimized searching (#43)<br>- Improved user interface for admin by adding a floating add book button (#45)<br>- Removed page reloading on button press (#86)<br>
|
| **In Progress** | None                                                                                                                                                                                                                                                                                                                                                                                                                          |
| **To Do** | None                                                                                                                                                                                                                                                                                                                                                                                                                          |
| **In Review** | None                                                                                                                                                                                                                                                                                                                                                                                                                          |

---

## Next Sprint Plan

N/A

---

## Database Schema

This schema is derived directly from the UML diagram.

### Table: `Users` (Derived from Client Class)

| Field Name | Data Type | Constraints / Description                               |
| :--- |:----------|:--------------------------------------------------------|
| **id** | `Long`    | **Primary Key**. Unique identifier for the user/client. |
| **username** | `String`  | Unique display name.                                    |
| **password** | `String`  | Hashed password for authentication.                     |
| **isOwner** | `Boolean` | Flag indicating if the user is an administrator/owner.  |
| **shoppingCartIds** | `List`    | Lists the IDs of books in the user's shopping cart.     |
| **purchasedBookIds** | `List`    | Lists the IDs of books purchased by the user.           |

### Table: `Books` (Derived from Book Class)

| Field Name                       | Data Type | Constraints / Description                        |
|:---------------------------------|:----------|:-------------------------------------------------|
| **id**                           | `Long`    | **Primary Key**. Unique identifier for the book. |
| **bookTitle**                    | `String`  | Title of the book.                               |
| **bookISBN**                     | `String`  | Unique identifier for the book (**ISBN**).       |
| **bookPitcure**                  | `String`  | Source of the image of the book cover.           |
| **bookDescription**              | `String`  | Detailed summary or description of the book.     |
| **bookAuthor**                   | `String`  | Author's name.                                   |
| **bookPublisher**                | `String`  | Publisher's name.                                |
| **bookPrice**                    | `Double`  | Book price.                                      |
| **numBooksAvailableForPurchase** | `Integer` | Number of the book in stock.                     |

### Relationship Table: `Cart` (Handles the `shoppingCart` relationship)

| Field Name | Data Type | Constraints / Description |
| :--- | :--- | :--- |
| **user\_id** | `Long` | **Foreign Key** to `Users.id`. |
| **book\_id** | `Long` | **Foreign Key** to `Books.id`. |
| **quantity** | `Integer` | Number of units of this book in the user's cart. |

### Relationship Table: `Purchases` (Handles the `purchasedBooks` relationship)

| Field Name | Data Type | Constraints / Description |
| :--- | :--- | :--- |
| **purchase\_id** | `Long` | **Primary Key**. Unique ID for this purchase record. |
| **user\_id** | `Long` | **Foreign Key** to `Users.id`. The customer who made the purchase. |
| **book\_id** | `Long` | **Foreign Key** to `Books.id`. The specific book purchased. |

---

## Notes

### Link to deployed site (on Azure):

- https://amazin-online-bookstore-group4.azurewebsites.net/

### Link to the logging (on Azure):

- https://amazingbookstore.grafana.net/

### Link to the Loki server:
- http://loki:3100

### Link to Grafana:
- http://localhost:3000

### Use the following to create an admin account:

- curl -X POST http://localhost:8080/client/register -H "Content-Type: application/json" -d '{"username": "admin", "password": "admin", "isOwner": true}' 
- Then login with username: admin, password: admin
