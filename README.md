# User-CRUD-SpringBoot
Users CRUD with testing support for controller layer.

## Code Checkout
You can checkout the code by simply runing the command below:

```bash
git clone https://github.com/tahir-mehmood/User-CRUD-SpringBoot.git
```

## Compiling the code
As this is a Spring boot application using the maven build system, you can easily build the project by running below command:

```bash
mvn clean install
```

This will download all the required dependencies, compile the code, run some tests and build the jar file.
Next step is to run the jar file which should run a tomcat server on port 8080 and expose http endpoints. 

## Run the application
To run this spring boot application you need to run below command:

```bash
mvn spring-boot:run
```
This will run the embedded tomcat server on port 8080. You can test the services using Postman or curl once the server is up and running.

Following endpoints will be available:



## API Reference

#### Get all Users

```http
  GET /users/
```

```
request
curl --location --request GET 'http://localhost:8080/user'


response
When No User found : Empty response body with response code '204 no content'

JSON Array if users are found: HTTP STATUS 200
[
    {
        "id": 1,
        "firstName": "Tahir",
        "lastName": "Mehmood",
        "dateOfBirth": "01-07-1990",
        "email": "atif.nbsit@gmail.com"
    }
]


```
 
#### Get Single User

```http
  GET /users/${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `string` | **Required**. Id of user to fetch |

This API gets a single user with given Id

```
Response When No User exists with given ID:
Status code '404 Not Found'
{
    "statusCode": 404,
    "timestamp": "2022-06-28T14:25:47.925+00:00",
    "message": "Could not find a user with ID: 2",
    "description": "uri=/user/2"
}

JSON Object if users are found: HTTP STATUS 200
{
    "id": 1,
    "firstName": "Tahir",
    "lastName": "Mehmood",
    "dateOfBirth": "01-07-1990",
    "email": "atif.nbsit@gmail.com"
}


```


#### Create A New User
```http
  POST /users
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `firstName`      | `string` | **Required**. First Name of user |
| `lastName`      | `string` | **Required**. Last Name of user|
| `dateOfBirth`      | `string` | **Required**. Date Of Birth of user. Must be a past date  |
| `email`      | `string` | **Required**. Email Address  of user. Must be a valid email|

This will create a user and return its URL to access in location header.

```http
POST /Users
request
curl --location --request POST 'http://localhost:8080/user' \
--header 'Content-Type: application/json' \
--data-raw '{
    "firstName":"Tahir",
    "lastName": "Mehmood",
    "dateOfBirth":"01-07-1990",
    "email":"atif.nbsit@gmail.com"
}'

Response
Status Code 400 Bad Request : If another user with this email already exists
{
    "email": "Non-Unique-Email: Another user with same email already exists. "
}

Status code 201 Created : When a valid user

```



#### Modify/Edit A User
```http
  PUT /users/{id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `string` | **Required**. Id of user to be edited. This will be part of the URL. Rest of fields will be in the request body as a json object |
| `firstName`      | `string` | **Required**. First Name of user |
| `lastName`      | `string` | **Required**. Last Name of user|
| `dateOfBirth`      | `string` | **Required**. Date Of Birth of user. Must be a past date  |
| `email`      | `string` | **Required**. Email Address  of user. Must be a valid email|

This will update the user record if it exists. If record doesn't exist it will return appropriate error response.

```
Request
curl --location --request POST 'http://localhost:8080/user/1' \
--header 'Content-Type: application/json' \
--data-raw '{
    "firstName":"Tahir",
    "lastName": "Mehmood",
    "dateOfBirth":"01-07-1986",
    "email":"tahir.nbsit@gmail.com"
}'

Response Status code 200 : If valid user was passed.
{
    "id": 1,
    "firstName": "Tahir",
    "lastName": "Mehmood",
    "dateOfBirth": "01-07-1986",
    "email": "tahir.nbsit@gmail.com"
}
```

#### Delete A User

```http
  DELETE /users/${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `string` | **Required**. Id of user to be deleted |

User with the given Id will be deleted if exists. Otherwise proper error response will be returned.

```
Request
curl --location --request DELETE 'http://localhost:8080/user/1'

Response Status code 204 No Content : A Valid user will be deleted
```

 ## 🔗 Links
[![portfolio](https://img.shields.io/badge/my_portfolio-000?style=for-the-badge&logo=ko-fi&logoColor=white)](https://github.com/tahir-mehmood)
[![linkedin](https://img.shields.io/badge/linkedin-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/tahirmehmood04/)






