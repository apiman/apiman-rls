# apiman-rls
A standalone Rate Limiting [Micro-]Service.

## Building
The apiman-rls project is a standard maven project, so to build it from source, 
simply do this:

```
mvn clean package
```

## Running
Once the project is built, there are a number of ways you can run the server.  Perhaps
the easiest way is to simply run the fat jar that is created in the apiman-rls-vertx 
module:

```
cd vertx/target
java -jar apiman-rls-vertx-${version}-fat.jar
```

This will start up the server on port 8080 of localhost.  Point your browser or REST
client at the resulting endpoint:

[http://localhost:8080/](http://localhost:8080/)

You should see a response payload with some basic information, for example:

```json
{
  "name" : "apiman-rls-api",
  "version" : "1.0",
  "description" : "A REST API to the apiman Rate Limiting [Micro-]Service.  This API provides a way for external clients to quickly and accurately query and increment named rate limits."
}
```

## Getting Started
Now that the server is running, you can start making REST calls to the server.  Details
of the REST API can be found here:

[http://docs.ratelimiterapi.apiary.io/](http://docs.ratelimiterapi.apiary.io/)

