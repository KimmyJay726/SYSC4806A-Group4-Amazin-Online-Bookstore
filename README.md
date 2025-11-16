# SYSC4806A-Group4-Amazin-Online-Bookstore 📚

The goal of this project is to develop an **online bookstore application** with **user authentication**, **product catalog**, and **order processing** capabilities.

---

## Project Status: Current Sprint (Sprint #4)

Kanban board status as of: **November 15, 2025**

|Kanban Column	|Key Deliverables/Issues|
|---|-|
| **Done** | - Create recommendations HTML page and add recommendations feature (**#5**)<br>- Add logging to java classes (**#59**)<br>- Add Shopping Cart Nav button to top Nav Bar (**#34**)<br>- Add buttons to add/remove book to/from Shopping Cart (**#35**)<br>- Add tests for testing endpoints (**#61**)<br>- Add purchasing logic (**#62**)<br>- Add Logging (Project Topic) (**#21**)<br> - Add Logging documentation (**#67**) |
| **In Progress** | - Setup Loki Grafana with Microsoft Azure (**#57**)<br>- Create video explaining Loki Grafana |
| **To Do** | - Add MockMVC tests (**#56**) |
| **In Review** | - Add logging errors (**#68**) |

---

## Next Sprint Plan (Sprint #5)

Next Sprint Scheduled for: **November 22, 2025**

| Assignee | New Issues |
|----------|---|
| **Jake** | Fix recommendations feature so that newly purchased books are not recommended #65, Add tests for recommendations feature #66 |
| **Fiona** | Add button to route back to homepage from the login page #51 |
| **Andrew** | Create MockMVC tests #56 |
| **Jacob** | Add verification to purchasing feature #44 Enhance purchasing page #63 |
| **Safi** | Add fuzzy matching #43 |

---

## Database Schema

This schema is derived directly from the UML diagram.

### Table: `Users` (Derived from Client Class)

| Field Name | Data Type | Constraints / Description |
| :--- | :--- | :--- |
| **id** | `Long` | **Primary Key**. Unique identifier for the user/client. |
| **username** | `String` | Unique display name. |
| **password** | `String` | Hashed password for authentication. |
| **isOwner** | `Boolean` | Flag indicating if the user is an administrator/owner. |

### Table: `Books` (Derived from Book Class)

| Field Name | Data Type | Constraints / Description |
| :--- | :--- | :--- |
| **id** | `Long` | **Primary Key**. Unique identifier for the book. |
| **bookISBN** | `String` | Unique identifier for the book (**ISBN**). |
| **bookAuthor** | `String` | Author's name. |
| **bookPublisher** | `String` | Publisher's name. |
| **bookDescription** | `Text` | Detailed summary or description of the book. |
| **bookPicture** | `String` | URL or path to the external image file. |

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

### Link to the Loki server:
- http://loki:3100

### Link to Grafana:
- http://localhost:3000

### Use the following to create an admin account:

- curl -X POST http://localhost:8080/client/register -H "Content-Type: application/json" -d '{"username": "admin", "password": "admin", "isOwner": true}' 
- Then login with username: admin, password: admin
