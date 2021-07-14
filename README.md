# Book MicroService Sample
## Technology used
- Springboot
- Restful
- Spring data
- JPA Search Criteria  
- Kafka
- swagger api 3.0
- Code deploy to Azure

## requirement
- Java 1.8
- Maven installed
- Kafka up and run
- lombok plugin is required

## start a Kafka
- download kafka and the following script
```aidl
./bin/zookeeper-server-start.sh config/zookeeper.properties
./bin/kafka-server-start.sh config/server.properties
```
## enable Kafka consume
```
bin/kafka-console-consumer.sh --topic bookms-topic --from-beginning --bootstrap-server localhost:9092
```

## Run springboot
```aidl
mvn springboot:run
http://localhost/bookms/swagger-ui/index.html?configUrl=/bookms/v3/api-docs/swagger-config
```

## Consumer output example
```aidl
2021-07-13T15:22:02.083 Book Created Book(id=1, title=:Java, authors=[David], isbn=1234567890123, publicationDate=2021-03-03)
2021-07-13T15:23:00.206 Book Searched for Book(id=1, title=:Java, authors=[David], isbn=1234567890123, publicationDate=2021-03-03)
2021-07-13T15:37:28.214 Book Modify from Book{id=1, title=':Java', authors=[David], isbn='1234567890123', publicationDate=2021-03-03} to Book{id=1, title='C#', authors=[David, John], isbn='1234567890123', publicationDate=2021-03-03}
2021-07-13T15:38:33.429 Book Delete for Book{id=1, title='C#', authors=[David, John], isbn='1234567890123', publicationDate=2021-03-03}
```

## deployment __
```
mvn clean package azure-webapp:deploy
```

## Azure URL
```aidl
http://bookmsdavid.azurewebsites.net/bookms/swagger-ui/index.html?configUrl=/bookms/v3/api-docs/swagger-config#/
```

## Possible Operation and response
### Create Book
- if create successfully
```
response code: 200
{
  "id": 1,
  "title": "string",
  "authors": [
    "string"
  ],
  "publicationDate": "14/07/2021",
  "ISBN": "1234567890123"
}
```
- an event service will sent to message broker
- if does not provide ISBN
```
response code: 400
{
  "message": "Validation Error",
  "details": [
    "Field isbn-must not be blank"
  ]
}
```
- if ISBN validation invalid
```
response code: 400
{
  "message": "Validation Error",
  "details": [
    "Field isbn-ISBN must be 13 digital"
  ]
}
```
### find Book by ID
- if no book found
```
response code: 200
{
  "message": "Detail",
  "details": [
    "Book not found by id=2"
  ]
}
```
- if book found
```
response code: 200
{
  "id": 1,
  "title": "string",
  "authors": [
    "string"
  ],
  "publicationDate": "14/07/2021",
  "ISBN": "1234567890123"
}
```
- an event service will sent to message broker
### modify book
- if book not found same as GET by ID
- if book found and isbn has issue same as book creation
- if book found and input without any problem
```
response code: 200
{
  "id": 1,
  "title": "Java",
  "authors": [
    "David"
  ],
  "publicationDate": "14/07/2021",
  "ISBN": "1234567890124"
}
```
- an event service will sent to message broker
### delete book
- if book not found, same result as book not found above
- otherwise success
```
response code: 200
{
  "id": 1,
  "title": "Java",
  "authors": [
    "David"
  ],
  "publicationDate": "14/07/2021",
  "ISBN": "1234567890124"
}
```
- an event service will sent to message broker
### Find all Book
- show empty [] is not book found
### Find by title, Author, ISBN
- support case insensitive
- if book found. book will be display
- if book found. event message will send to broker
- if book not found. an error resposne will show.