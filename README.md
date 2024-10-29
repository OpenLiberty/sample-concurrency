# Sample Concurrency
Open Liberty sample application demonstrating Jakarta Concurrency 

**Note: The sample is currently using a Beta build of Open Liberty.**

## Environment Setup
To run this sample, first [download](https://github.com/OpenLiberty/sample-concurrency/archive/refs/heads/main.zip) or clone this repo - to clone:
```shell
git clone git@github.com:OpenLiberty/sample-concurrency.git
```

Requires Java 21 for virtual threads. IBM Semuru Runtimes Java 21 can be obtained [here](https://developer.ibm.com/languages/java/semeru-runtimes/downloads/)


### Set Up MongoDB
You will need a MongoDB instance to use this sample. If you have Docker installed, you can use the following command to start a mongo container:
```shell
docker run -d -p 27017:27017 --name liberty_mongo mongo:7.0.7
```

## Running the Sample
From inside the `sample-concurrency` directory, build and start the application in Open Liberty with the following command: 

```shell
./mvnw liberty:dev
```

Once the server has started, the application is available at http://localhost:9080

### Try it out
The sample application has three buttons, one for each of the endpoints in the application. When you select the `Schedule` button, a scheduled async method will be started, incrementing a counter every three seconds. The second button, `ContextualFlow`, starts a reactive stream, which inserts a document in a mongo database. The result of the insert is emitted by a publisher in the MongoDb driver. A contexutalized `Flow.Subscriber` is used to receive the result, and if successful, lookup and return a string from `java:comp/env` context. The third button, `VirtualThreads`, runs 100,000 virtual threads, each of which sleeps for 1 second before returning.

### How it works
This application provides a few REST endpoints to demonstrate some of the new features of Jakarta Concurrency 3.1. The endpoints are available in [ConcurrencyService.java](src/main/java/io/openliberty/sample/application/ConcurrencyService.java).

#### Schedule
The first endpoint `/schedule` demonstrates the `@Schedule` annotation on an asynchronous method. The asynchronous method is in the [ConcurrencyBean](src/main/java/io/openliberty/sample/application/ConcurrencyBean.java) CDI Bean.

```java
@Asynchronous(runAt = { @Schedule(cron = "*/3 * * * * *")}) // Every 3 Seconds
void counter() {
    count++;
```
After the method is called, it will run asynchronously on a schedule set by the provided cron string. This results in the count increasing every 3 seconds.

#### Contextual Flow
The second endpoint `/contextualFlow` demonstrates using `ContextService` to contextualize a `Flow.Subscriber`. In [ConcurrencyService](src/main/java/io/openliberty/sample/application/ConcurrencyService.java#L70) the endpoint uses the MongoDB Reactive Streams driver to insert a document in a MongoDB database. The result of the insert is provided via a `Publisher`, which publishes the result asynchronously, with no mechanism to configure an executor. `ContextService.contextualSubscriber` is used to contextualize the `Flow.Subscriber` which subscribes to the `Publisher`.

```java
public void contextualFlow() {
    mongo.insertOne(Document.parse("{\"test\" : \"data\"}"))
         .subscribe(FlowAdapters.toSubscriber(contextService.contextualSubscriber(subscriber)));
}
```

With this, context is available in the [MongoSubscriber](src/main/java/io/openliberty/sample/application/reactivestreams/MongoSubscriber.java#L42), demonstrated by a JNDI lookup in `java:comp/env`. The provided message is set in the [web.xml](src/main/webapp/WEB-INF/web.xml#L14).

```java
public void onNext(InsertOneResult item) {
    ...
    String replySuccess = (String) new InitialContext().lookup("java:comp/env/replySuccess");
```

#### Virtual Threads
The final endpoint `/virtualThreads` demonstrates a `ManagedExecutor` configured to use Virtual Threads. This is done in [ConcurrencyService](src/main/java/io/openliberty/sample/application/ConcurrencyService.java#L37), using `@ManagedExecutorDefinition` to define a `ManagedExecutorService` which uses virtual threads by specifying `virtual = true`.

```java
@ManagedExecutorDefinition(name = "java:module/concurrent/virtual-executor",
                           qualifiers = WithVirtualThreads.class,
                           virtual = true)
...
public class ConcurrencyService {
```

Since [WithVirtualThreads.class](src/main/java/io/openliberty/sample/application/cdi/WithVirtualThreads.java) is specified as a qualifier, the `ManagedExecutorService` with virtual threads can be injected by specifying `@WithVirtualThreads`.
```java
    @Inject
    @WithVirtualThreads
    ManagedExecutorService virtualManagedExecutor;
```
This `ManagedExecutor` is used to create 100,000 threads, which is trivial with Virtual Threads, but likely to run out of memory with Platform Threads.

```java
for (int i = 1; i < 100_000; i++) {
    futures.add(virtualManagedExecutor.submit(() -> {
...
            Thread.sleep(Duration.ofSeconds(1));   
```

## Stop MongoDB
When you are done trying out the sample application, you can stop the MongoDB container with:
```shell
docker stop liberty_mongo
```

## Where to go next

Check out the Jakarta Concurrency Specification on GitHub: https://github.com/jakartaee/concurrency.

If you want to learn more about Open Liberty, check out the guides: https://openliberty.io/guides/  

You can make suggestions or report bugs by opening an issue, or star this repository to show you're interested.