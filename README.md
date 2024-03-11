# sample-concurrency
Open Liberty sample application demonstrating Jakarta Concurrency 

Requires Java 21 for virtual threads
```shell
mvn liberty:dev
```

## MongoDB image

```shell
docker run -d -p 27017:27017 mongo:5.0.25
```