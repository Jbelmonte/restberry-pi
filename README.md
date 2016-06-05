# Technical Test

## Build

Application uses Maven as build system, so in order to generate the executable JAR file you only need to execute:

```bash
$ mvn package
```

The executable file can be found at `[...]/target/test-web-application.jar`.

### Disclaimer

Built JAR requires its Maven dependencies to appear in classpath.

In order to ease the run command, please update the `pom.xml` file by setting the absolute path to the Maven repository in setting `classpathPrefix` inside `maven-jar-plugin` configuration block.

Example:

```xml
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-jar-plugin</artifactId>
	<configuration>
		<archive>
			<manifest>
				<addClasspath>true</addClasspath>
				<classpathPrefix>/Users/juan/.m2/repository/</classpathPrefix>
				<classpathLayoutType>repository</classpathLayoutType>
				<mainClass>org.company.techtest.Main</mainClass>
			</manifest>
		</archive>
	</configuration>
</plugin>

```


## Run

You can run the server with the following command:

```bash
$ java -jar test-web-application.jar
```

Server will start listening on port `8080`. Example [http://localhost:8080]().

## Endpoints documentation

Description of available endpoints, both REST API and web site, can be found in [src/docs](src/docs) folder, via:

* `api.raml`: RAML specification.
* `api.html`: [HTML export](https://github.com/raml2html/raml2html) of the RAML specification.

### Available users

Initially, there's only one user in database:

* ID: `1`
* Username: `user1`
* Password: `user1`
* Roles: `ADMIN`

Use this user's credentials to create new users using the REST API with any (or all) of the following roles:

* `PAGE_1`
* `PAGE_2`
* `PAGE_3`


## Tests

The repo does not contain unit test files. But you can test all endpoints with the given Postman collection.

[![Run in Postman](https://run.pstmn.io/button.svg)](https://app.getpostman.com/run-collection/c77245b667a597e27107)


