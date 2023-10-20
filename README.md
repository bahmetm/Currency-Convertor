# Currency Exchange Project

This project implements a REST API for managing currencies and exchange rates, allowing users to view and edit lists of currencies, perform currency conversion, and access exchange rate information. The project is developed in Java and uses PostgreSQL as the database.

## Table of Contents
- [Requirements](#requirements)
- [Motivation](#motivation)
- [Database Structure](#database-structure)
- [REST API Endpoints](#rest-api-endpoints)
  - [Currencies](#currencies)
  - [Exchange Rates](#exchange-rates)
  - [Currency Exchange](#currency-exchange)
- [Error Handling](#error-handling)

---

## Requirements

Before getting started with this project, it's important to be familiar with the following technologies and concepts:

- Java, including collections and object-oriented programming (OOP) principles.
- Maven for project management.
- Backend development with Java servlets.
- HTTP, including GET and POST requests, and response codes.
- REST API design and JSON.
- Databases, specifically PostgreSQL and JDBC.
- Deployment on cloud hosting, Linux command line, and Tomcat.

No external frameworks are used in this project.

## Motivation

The primary motivations behind this project include:

- Learning and implementing the Model-View-Controller (MVC) design pattern.
- Designing a RESTful API with correct resource naming and HTTP response codes.
- Designing project
- Gaining experience in SQL, including basic syntax, table creation and joins.
- Exploring database usage, specifically PostgreSQL.
- Create a functional currency exchange application that accurately handles financial calculations and utilizes appropriate data structures.
  
## Database Structure

This project uses a PostgreSQL database with two tables: `currency` and `exchange_rate`. The structure of these tables is as follows:

### Table: currency

| Column     | Type    | Description                     |
|------------|---------|---------------------------------|
| id         | int     | Currency ID (auto-increment)    |
| code       | Varchar | Currency code                   |
| fullname   | Varchar | Full currency name              |
| sign       | Varchar | Currency symbol                 |

Example record for the Australian dollar (AUD):

| id | code | fullname           | sign |
|----|------|--------------------|------|
| 1  | AUD  | Australian dollar  | A$   |

Currency codes are based on the [IBAN currency codes](https://www.iban.com/currency-codes).

Indexes:

- Primary key on the `id` field.
- Unique index on the `code` field for currency code uniqueness and faster currency lookup.

### Table: exchange_rate

| Column             | Type      | Description                                 |
|--------------------|-----------|---------------------------------------------|
| id                 | int       | Exchange rate ID (auto-increment)           |
| base_currency_id   | int       | ID of the base currency (foreign key)       |
| target_currency_id | int       | ID of the target currency (foreign key)     |
| rate               | Decimal(6)| Exchange rate from base to target currency  |

Example record for the exchange rate from USD to EUR:

| ID | base_currency_id | target_currency_id | rate  |
|----|------------------|--------------------|-------|
| 1  | 0                | 1                  | 0.99  |

Indexes:

- Primary key on the `id` field.
- Unique index on the pair of fields `base_currency_id` and `target_currency_id` for currency pair uniqueness and faster rate retrieval.

---

## REST API Endpoints

The REST API provides CRUD operations for both currencies and exchange rates. Below are the available endpoints:

### Currencies

- **GET /currencies**: Retrieve a list of all currencies.

  Example Response:
  ```json
  [
      {
          "id": 0,
          "name": "United States dollar",
          "code": "USD",
          "sign": "$"
      },   
      {
          "id": 1,
          "name": "Euro",
          "code": "EUR",
          "sign": "€"
      }
  ]
  ```

- **GET /currency/{code}**: Retrieve information about a specific currency by its code.

  Example Response:
  ```json
  {
      "id": 1,
      "name": "Euro",
      "code": "EUR",
      "sign": "€"
  }
  ```

- **POST /currencies**: Add a new currency to the database.

  Example Request Body:
  ```json
  {
      "name": "British Pound",
      "code": "GBP",
      "sign": "£"
  }
  ```

  Example Response:
  ```json
  {
      "id": 2,
      "name": "British Pound",
      "code": "GBP",
      "sign": "£"
  }
  ```

### Exchange Rates

- **GET /exchangeRates**: Retrieve a list of all exchange rates.

  Example Response:
  ```json
  [
      {
          "id": 1,
          "baseCurrency": {
              "id": 0,
              "name": "United States dollar",
              "code": "USD",
              "sign": "$"
          },
          "targetCurrency": {
              "id": 1,
              "name": "Euro",
              "code": "EUR",
              "sign": "€"
          },
          "rate": 0.99
      }
  ]
  ```

- **GET /exchangeRate/{base}/{target}**: Retrieve the exchange rate for a specific currency pair.

  Example Response:
  ```json
  {
      "id": 1,
      "baseCurrency": {
          "id": 0,
          "name": "United States dollar",
          "code": "USD",
          "sign": "$"
      },
      "targetCurrency": {
          "id": 1,
          "name": "Euro",
          "code": "EUR",
          "sign": "€"
      },
      "rate": 0.99
  }
  ```

- **POST /exchangeRates**: Add a new exchange rate to the database.

  Example Request Body:
  ```json
  {
      "baseCurrencyCode": "USD",
      "targetCurrencyCode": "GBP",
      "rate": 0.75
  }
  ```

  Example Response:
  ```json
  {
      "id": 2,
      "baseCurrency": {
          "id": 0,
          "name": "United States dollar",
          "code": "USD",
          "sign": "$"
      },
      "targetCurrency": {
          "id": 2,
          "name": "British Pound",
          "code": "GBP",
          "sign": "£"
      },
      "rate": 0.75
  }
  ```

- **PATCH /exchangeRate/{base}/{target}**: Update an existing exchange rate in the database.

  Example Request Body:
  ```json
  {
      "rate": 0.80
  }
  ```

  Example Response:
  ```json
  {
      "id": 1,
      "baseCurrency": {
          "id": 0,
          "name": "United States dollar",
          "code": "USD",
          "sign": "$"
      },


      "targetCurrency": {
          "id": 1,
          "name": "Euro",
          "code": "EUR",
          "sign": "€"
      },
      "rate": 0.80
  }
  ```

### Currency Exchange

- **GET /exchange?from={baseCurrencyCode}&to={targetCurrencyCode}&amount={amount}**: Perform currency exchange calculation.

  Example Request:
  ```
  GET /exchange?from=USD&to=EUR&amount=10
  ```

  Example Response:
  ```json
  {
      "baseCurrency": {
          "id": 0,
          "name": "United States dollar",
          "code": "USD",
          "sign": "$"
      },
      "targetCurrency": {
          "id": 1,
          "name": "Euro",
          "code": "EUR",
          "sign": "€"
      },
      "rate": 0.99,
      "amount": 10.00,
      "convertedAmount": 9.90
  }
  ```

---

## Error Handling

In case of errors, the API responds with a JSON message that indicates the nature of the error. The exact message depends on the specific error encountered.

Example Error Response:
```json
{
    "message": "Currency not found"
}
```

Please note that this README provides an overview of the project and its features. For detailed implementation and code examples, refer to the project source code.
