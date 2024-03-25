# Sample Concurrency
Open Liberty sample application demonstrating Jakarta Concurrency 

## Environment Setup
To run this sample, first [download](https://github.com/OpenLiberty/sample-concurrency/archive/refs/heads/main.zip) or clone this repo - to clone:
```shell
git clone git@github.com:OpenLiberty/sample-concurrency.git
```

Requires Java 21 for virtual threads. IBM Semuru Runtimes Java 21 can be obtained [here](https://developer.ibm.com/languages/java/semeru-runtimes/downloads/)


### Set Up MongoDB
You will need a MongoDB instance to use this sample. If you have Docker installed, you can use the following command to start a mongo container:
```shell
docker run -d -p 27017:27017 mongo:5.0.25
```

## Running the Sample
From inside the `sample-concurrency` directory, build and start the application in Open Liberty with the following command: 

**Note: The sample is currently using a nightly build of Open Liberty. Nightly builds have not gone through the full release process and may not be as stable as Beta or Release builds**

```shell
./mvnw liberty:dev
```

Once the server has started, the application is available at http://localhost:9080