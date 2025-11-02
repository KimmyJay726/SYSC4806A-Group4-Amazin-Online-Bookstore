# SYSC4806A-Group4-Amazin-Online-Bookstore 📚

The goal of this project is to develop an **online bookstore application** with **user authentication**, **product catalog**, and **order processing** capabilities.

---

## Project Status: Current Sprint (Sprint #2)

Kanban board status as of: **November 1, 2025**
| Kanban Column | Key Deliverables/Issues |
| :--- | :--- |
| **Done** | - Create main and login HTML pages (**#2** and **#1**)<br>- Create User Controller and Book Java/Controller classes (**#11** and **#12**)<br>- Reformat Project Structure (**#22**)<br>- Create Azure web app & Pass Workflow Check (**#16** and **#18**) |
| **In Progress** | - Create updated UML class diagram (**#15**)<br>- Create updated README and database schema (**#14**)<br>- Create editBook HTML page (**#7**) |
| **To Do** | - Create addBook HTML page (**#6**) |
| **In Review** | - Create browse HTML page (**#3**)|

---

## Next Sprint Plan (Sprint #3)

Next Sprint Scheduled for: **November 8, 2025**
| Assignee | New Issues |
| :--- | :--- |
| **Jake** | Create recommendations HTML page #5, Update README and database schema (Sprint #2) #36 |
| **Fiona** | Create Shopping Cart Nav button to top Nav Bar #34, Create Buttons to add book to Shopping Cart #35 |
| **Andrew** | Create editBook HTML page #7, Create updated UML class diagram#15, Create Tests for Endpoint Testing #37 |
| **Jacob** | Create purchase HTML page #4, Determine and implement a way to initialize an Owner User #33, Create Hook to redirect user who is currently not logged in to the Login/Register Page when navigating #32 |
| **Safi** | Add Logging (Project Topic) #21, Create a demo.md file #38 |

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

### Use the following to create an admin account:

curl -X POST http://localhost:8080/client/register -H "Content-Type: application/json" -d '{"username": "admin", "password": "admin", "isOwner": true}' Then login with username: admin, password: admin
