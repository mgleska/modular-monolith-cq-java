# Modular monolith with Command and Query (Java version)

## Key goals and requirements

1. **Architecture: modular monolith (vertical slices).**
2. Modules separated according to functional boundaries (verbs).
3. **Domain-Driven Design** applied at strategic level.
4. **Command and Query** pattern for all actions.
5. **Layers (horizontal slices)** applied at module level.
6. Automatic control of whether cross-module access rules and in module layer-to-layer access rules are followed.
7. Pure API service - JSON input, JSON output.

## Tactical objectives

1. Application built using the **Spring Boot** framework.
2. Using **Jakarta Validation** to process input data received by the API.
3. Using Spring Security. It allows relatively simple use of ready-made solutions for connecting to various identity providers (e.g. OAuth2, SAML).
4. Entity versioning (know also as "optimistic locking") for checking consistency between data presented to user (frontend side) and actual state of entity in database (backend side).
5. Database deadlock avoidance.
6. Zero CRUD, zero PUT/PATCH/DELETE.
7. Using ready-made solutions to create API documentation based on data structures and annotations prepared for the Jakarta Validation.
8. High coverage by tests.

## Architecture

<img src="docs/modular-monolith.svg" alt="modular monolith">

<img src="docs/module.svg" alt="module">

### General principles of Architecture

1. Each module implements set of domain actions.
2. All actions from layer **Action** of any module can be used by other modules or by "external world" (by API/CLI/Message/Event/direct call).
3. Other modules and "external world" can access and manipulate module's data only by calling actions exposed by module.
4. Typical action defines own Data Transfer Objects (DTOs) for input data and different DTOs for output data.
5. Each module holds its own set of data in storage (e.g. database). Only the module has direct access to its data.

### Layer _Access_

In layer **Access** module defines and implements methods which do translation between "external language" (for example: HTTP POST call with JSON structure as input data) and actions defined in layer **Action**.

Typical methods in this layer:
* method for handling the HTTP endpoint call with JSON data,
* method for handling the HTTP endpoint call with HTTP form data,
* method for handling the command line call,
* method for handling messages incoming from queue (RabbitMQ, SQS),
* method for handling messages incoming from pub-sub topic (Kafka),
* method for handling event emitted by framework (Spring).

General rule for layer **Access** is, that component from this layer can **import** only:
* actions and structures defined at layer **Action** of given module,
* supplementary classes and structures defined at layer **Access** of given module,
* classes and objects from common infrastructure, framework and `External Libraries`.

Usually there is no need to define method to perform external call from other module in the application.\
The reason is simple - other module can **import** directly action from layer **Action** of given module and supply input data in form required by the action (as: DTO or value with Java built-in type).

### Layer _Action_

In layer **Action** module implements **domain actions** which are core responsibility of the module.

Each action covers part of business domain and holds (encapsulates) business rules specific for given action.

**An important feature of Architecture is, that it focuses on actions. It is worth to note it.**

Actions which belongs to one business domain (bounded context) are located in the one module.\
Actions in the module may relay strictly on the other actions in this module or on supplementary services from layer **Support** (also from the same module).\
We can say, that actions and services in the module are **tightly coupled**.

Relations between modules are defined by strict rules:
* list of actions exposed by the module
* action's input DTO
* action's output DTO

We can say, that modules are **loosely coupled**.

Actions follow **Command and Query** pattern (also known as Command Query Responsibility Segregation / **CQRS**).\
So any action have to be one of:
* command,
* query.

Action **query** reads data from module's internal state (database) and converts it to output DTO (or aggregate of related DTOs).\
For the **query** action it is forbidden to make changes to the internal state of the module.\
Action's output DTO should contain minimal set of data resulting from business logic.

Main goal of action **command** is to change module's internal state - for example create in database new instance of Product model.\
Usually **command** takes input DTO, validates data from DTO, apply business rules and finally change internal state.\
Some **commands** may produce output DTO - for example selected data from just created model in database.

Input and output DTOs are the only way to supply data into module and read data from the module.\
Someone names these DTOs **read model** (output DTO) and **write model** (input DTO).

**Command and Query** pattern do not force using events or other complicated staff.

The good practice is to implement action as one class, with one public method and private supplementary methods.\
If it is required, action can **import** any supplementary methods and structures defined at layers **Action** and **Support** of given module and layer **Action** of any other module.

An important feature of the **Architecture** is, that **other modules do not have direct access to models** of given module.\
It gives us high level of isolation and prevents data which belongs to given module from direct manipulation by other modules.

General rule for layer **Action** is, that component from this layer can **import**:
* methods and structures defined at layers **Action** and **Support** in given module,
* actions and structures exposed by other modules,
* classes and objects from common infrastructure, framework and `External Libraries`.

### Layer _Support_

Layer **Support** is a place, where module defines and implements classes, methods and structures used to perform common supplementary services required by domain actions from layer **Action**.\
Also in **Support** there is a definition of structures of module's internal state. Or to put it simply - models in the database.

It is worth to note, that `Entity` class name do not match 1-to-1 name of table in database. For example class `Offer` is mapped to table `ofr_offer`. This approach allow to group tables of the module by common prefix (`ofr_` in this example).

General rule for layer **Support** is, that component from this layer can **import** only:
* actions and structures defined at own layer,
* actions and structures defined at layer **Action** of given module,
* actions and structures exposed (defined at layer **Action**) by other modules,
* classes and objects from common infrastructure, framework and `External Libraries`.


## Task (example to demonstrate key features of _Architecture_)

### Domain background

We have a group of individual businesses (stores) that collectively purchase products from suppliers.

Each store independently sets the selling price of the product and decides which products to offer.

The group wants to display current offers in the mobile app – product availability and prices in the store selected by the mobile app user.

Additionally, store employees should be able to hide the offer even if the product is available in the store.

### Task

Create a service (backend) which will:
1. periodically get list of stores, from central API,
2. periodically get dictionary of products, from central API,
3. periodically get list of offers for each store, from ERP system,
4. register mobile users; maintain list of mobile users,
5. supply actual list of offers in store selected by mobile user,
6. register CMS users (employees); maintain list of CMS users,
7. supply list of offers (in CMS mode) with ability to change offer visibility.

### Solution

1. Modular monolith with Command & Query pattern.
2. Modules:
   1. Customer - actions related to mobile user:
      1. login user or register new user - simplified endpoint, just for demo,
      2. command to set preferred store in mobile user profile.
   2. Offer - actions related to offers:
      1. supply list of offers for mobile application; with pagination,
      2. supply details of offer for mobile application,
      3. supply list of offers for CMS; with search and pagination,
      4. command to change offer visibility,
      5. command to download list of offers.
   3. Product - actions related to products:
      1. command to download dictionary of products,
      2. command to download products quantity in each store,
      3. sharing product information for other modules.
   4. Store - actions related to stores:
      1. command to download list of stores,
      2. supply list of stores for mobile application,
      3. supply details of store for mobile application.
   5. User - actions related to CMS user:
      1. login user or register new user - simplified endpoint, just for demo.
3. JWT token for authentication of mobile user.\
In token we store two main parameters of mobile user:
   * user ID
   * ID of selected store

    JWT token allows to verify user identity and get vital user parameters without database call. It is important feature for heavy loaded systems.
4. Spring Security used for authentication of CMS users.\
This approach allow to set sophisticated authenticators (like OAuth, SAML, LDAP, etc.) with minimal effort.
5. MariaDB as database.


## Some implementation details

### Directory structure

At level 1 below `src/` directory, each directory represents a module.\
In this example implementation we have:
```text
src/
   customer/
   offer/
   product/
   shared/
   store/
   user/
```
Special directory `shared/` is not a module.\
It holds common services and structures used by other modules. So this directory is part of `common infrastructure`.


At level 2 below `src/` directory, we have the following directories:
```text
src/
   customer/
      access/
      action/
      model/
      support/
   offer/
      access/
      ...
```
Directory `access/` holds the first layer of the module.\
Directory `action/` holds the second layer.\
Directories `model/` and `support/` holds the third layer.

At level 3 below `src/*/` directory, we have the following directories:
```text
src/
   customer/
      access/
         console/
         controller/
      action/
         command/
         enums/
         query/
      model/
      support/
         {whatever required}/
         ... 
```

### Module boundary checking

With proposed directory structure we can quite easy check if **Architecture** rules are preserved.

For checking we use **Spring Modulith** package - https://docs.spring.io/spring-modulith/reference/index.html

To check rules use: `mvnw test -Dtest=ApplicationStructureTest`

### OpenAPI file generated from DTOs

File `src/main/resources/openapi/openapi.yaml` is generated from input and output DTOs for all endpoints.\
When API is running, Swagger UI is available on URL http://127.0.0.1:8080/swagger-ui/index.html

For this task we use library `springdoc-openapi`.

To generate `openapi.yaml` file use: `mvnw verify -DskipTests`

### Testing

ToDo ...

## Usage:

```shell
docker compose up
```

It will download required images, build custom image for Java and execute script `start-app.sh` containing commands: 
```shell
mvnw clean package -DskipTests

java -jar target/mm-cq-java-2-0.0.1-SNAPSHOT.jar

# preparing demo data
java -jar target/mm-cq-java-2-0.0.1-SNAPSHOT.jar store:import
java -jar target/mm-cq-java-2-0.0.1-SNAPSHOT.jar product:import
java -jar target/mm-cq-java-2-0.0.1-SNAPSHOT.jar product:quantity
java -jar target/mm-cq-java-2-0.0.1-SNAPSHOT.jar offer:import r001
java -jar target/mm-cq-java-2-0.0.1-SNAPSHOT.jar offer:import r002
java -jar target/mm-cq-java-2-0.0.1-SNAPSHOT.jar offer:import r003
```

### Mobile API example

To create mobile user use API call like this:
```shell
curl --location 'http://127.0.0.1:8080/api/customer/login' \
--header 'Content-Type: application/json' \
--data '{
    "customerId": 0
}'
```
Result will look like this:
```json
{
    "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhY2MiLCJ1aWQiOjEyLCJzdGlkIjowLCJleHAiOjE3Nzc2ODE0OTR9.jtFxJUZKE1yTt7XrPzQD6SmL6YNLvhwxnqcamjHxTbk"
}
```
This JWT access token contains generated user ID (probably 1) and is valid for 8 hours.\
It is regular JWT token, signed, not encrypted. Its content can be viewed on https://jwt.io \
Now user should select a preferred store. So we have to list available stores:
```shell
curl --location 'http://127.0.0.1:8080/api/store/list' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhY2MiLCJ1aWQiOjEsInN0aWQiOjAsImV4cCI6MTc4MzM4NjkxNH0.rRmdxVU_Nk35EAYOiafpMa3N7otaHm5yhxUFdiK5ysc' \
--header 'Content-Type: application/json'
```
Let say, that on the list is store with ID=2.\
We can select this store:
```shell
curl --location 'http://127.0.0.1:8080/api/customer/change-store' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhY2MiLCJ1aWQiOjEyLCJzdGlkIjowLCJleHAiOjE3Nzc2ODE0OTR9.jtFxJUZKE1yTt7XrPzQD6SmL6YNLvhwxnqcamjHxTbk' \
--data '{
    "storeId": 2
}'
```
Result is:
```json
{
    "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhY2MiLCJ1aWQiOjEyLCJzdGlkIjoyLCJleHAiOjE3Nzc2ODY1ODJ9.spdbTmIvaJgYbBcC8H6-BuFNr2DuMveH1Q8UT7bF1xM"
}
```
It is our final access token, for our user and store ID=2.\
Now we can see list of offers in store with ID=2:
```shell
curl --location 'http://127.0.0.1:8080/api/offer/list' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhY2MiLCJ1aWQiOjEsInN0aWQiOjIsImV4cCI6MTc4MzM4NzA4MX0.bc0Uw2p02J2roBva64agoHTp_ZF-bLdygAT1_nAEDxM' \
--header 'Content-Type: application/json'
```
Result should look like this:
```json
{
    "items": [
        {
            "id": 10,
            "productEan": "ean-1",
            "productName": "Red square",
            "price": 93,
            "lowestPrice": 102
        },
        {
            "id": 11,
            "productEan": "ean-2",
            "productName": "Blue square imported",
            "price": 206,
            "lowestPrice": 227
        },
        {
            "id": 12,
            "productEan": "ean-3",
            "productName": "Green square",
            "price": 276,
            "lowestPrice": 304
        },
        {
            "id": 13,
            "productEan": "ean-4",
            "productName": "Red triangle",
            "price": 386,
            "lowestPrice": 425
        },
        {
            "id": 14,
            "productEan": "ean-5",
            "productName": "Blue triangle",
            "price": 529,
            "lowestPrice": null
        }
    ],
    "page": 1,
    "perPage": 5
}
```

### CMS API example

To create CMS user use API call like this:
```shell
curl --location 'http://127.0.0.1:8080/api/admin/user/login' \
--header 'Content-Type: application/json' \
--data-raw '{
    "email": "user@my.company.com"
}'
```
Result will look like this:
```json
{
    "token": "1-3/5bmuAvnZOAxFN5+L/Dw4wY8X8IrJF8O3115FEzTp0="
}
```
It is custom Spring Security token. With long TTL.\
Using this token for authentication, we can list offers in selected store (e.g. with ID=3):
```shell
curl --location 'http://127.0.0.1:8080/api/admin/offer/list?storeId=3&perPage=4' \
--header 'Authorization: Bearer 1-3/5bmuAvnZOAxFN5+L/Dw4wY8X8IrJF8O3115FEzTp0=' \
--header 'Content-Type: application/json'
```
Result should look like this:
```json
{
   "items": [
      {
         "id": 24,
         "storeName": "Czerwonak",
         "visible": true,
         "productEan": "ean-8",
         "productName": "Blue circle imported",
         "price": 859
      },
      {
         "id": 18,
         "storeName": "Czerwonak",
         "visible": true,
         "productEan": "ean-2",
         "productName": "Blue square imported",
         "price": 182
      },
      {
         "id": 21,
         "storeName": "Czerwonak",
         "visible": true,
         "productEan": "ean-5",
         "productName": "Blue triangle",
         "price": 478
      },
      {
         "id": 25,
         "storeName": "Czerwonak",
         "visible": true,
         "productEan": "ean-9",
         "productName": "Green circle imported",
         "price": 825
      }
   ],
   "page": 1,
   "perPage": 4
}
```
Now we can check details of offer with ID=20:
```shell
curl --location 'http://127.0.0.1:8080/api/admin/offer/20' \
--header 'Authorization: Bearer 1-3/5bmuAvnZOAxFN5+L/Dw4wY8X8IrJF8O3115FEzTp0=' \
--header 'Content-Type: application/json'
```
Result:
```json
{
    "id": 20,
    "version": 0,
    "visible": true,
    "productEan": "ean-4",
    "productName": "Red triangle",
    "price": 416,
    "lowestPrice": 458,
    "imageUrl": "red-triangle.png",
    "quantity": 13000
}
```
Important information is, that model in database has `version=0` and `visible=true`.\
Now we can hide the offer on list in mobile application:
```shell
curl --location 'http://127.0.0.1:8080/api/admin/offer/change-visibility' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer 1-3/5bmuAvnZOAxFN5+L/Dw4wY8X8IrJF8O3115FEzTp0=' \
--data '{
    "id": 20,
    "version": 0,
    "visible": false
}'
```
Checking again details of offer with ID=20 we get:
```json
{
    "id": 20,
    "version": 1,
    "visible": false,
    "productEan": "ean-4",
    "productName": "Red triangle",
    "price": 416,
    "lowestPrice": 458,
    "imageUrl": "red-triangle.png",
    "quantity": 13000
}
```
Now we have `visible=false` and `version` value increased to `1`.


## "Zero CRUD, zero PUT/PATCH/DELETE" - why?

At first reading of this sentence you may think "This guy is crazy! He rejects the foundations of REST API."

Here is the explanation of this architectural decision.

I saw a lot of projects, where domain model is mapped 1-to-1 to database table end exposed directly to external world (mostly frontend SPA).

With such design, frontend SPA can directly manipulate model stored in database - by changing whole entity (PUT action) or only part of the entity (PATCH action).\
Orders coming from frontend SPA contain commands PUT/PATCH, that the backend and database should execute without hesitation.\
Source of the orders is a user, which makes decision based on what he/she see on the screen.

This approach is valid for application designed for direct database manipulation (like phpMyAdmin) or simple blogpost.

Main drawback of this approach is, that the user makes a decision based on data which **was** in database at moment when data was sent to presentation layer. Ignoring the fact, that the same entity/model at the moment the user makes a decision **may already be different**.

Some projects simply ignore this inconsistency between user view and real application state.\
Other project tries to solve this problem - with more or less successful results.

Suppose we build application for transportation of domestic shipments.\
Users see on the screen shipment in state "NEW". Then user goes to grab a coffee.\
Whe he comes back, he decides to send shipment for acceptance by carrier. So he chooses on the screen new shipment state "SENT" and press "Submit" button. Frontend sends PATCH request with "{state = SENT}".\
But it was urgent shipment. In the meantime, another user sent the shipment for acceptance and the carrier responded with confirmation for execution. And current shipment state is "CONFIRMED".

Should we accept PATCH request from coffee lover and change shipment state to "SENT"?\
Technically - yes, it is fundamental rule of REST/PATCH.\
From business point of view - definitely not.

We can easily avoid such dilemmas if we abandon PUT/PATCH/DELETE and focus on CQRS approach with **actions**.

Let's go back to domestic shipments example.\
The user comes back with a coffee and issue a command "I would like to send this shipment for acceptance". By POST request.\
Backend checks command pre-condition and see, that shipment was already modified during user's "coffee time" - the version of the shipment in the database differs from the version of the shipment displayed on the user's screen.\
So backend may answer with a user-friendly message that user's command cannot be done.

This approach allows to encapsulate domain logic related to the command "I would like to send this shipment for acceptance" in Java class designed strictly for this one command.
