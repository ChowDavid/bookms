# Book MicroService Sample
## Technology used
- Springboot
- Restful
- Spring data
- Kafka
- swagger api 3.0

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

## error message if ISBN is not 13 dig
```aidl
response 400
{
  "message": "Validation Error",
  "details": [
    "Field isbn-ISBN must be 13 digital"
  ]
}
```
## book not found error
response code 400
```aidl
{
  "message": "Exception",
  "details": [
    "Book not found by id=100"
  ]
}
```

## deployment 
```
mvn clean package azure-webapp:deploy
```

## Azure URL
```aidl
http://bookmsdavid.azurewebsites.net/bookms/swagger-ui/index.html?configUrl=/bookms/v3/api-docs/swagger-config#/
```