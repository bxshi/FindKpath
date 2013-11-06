#Find K Path

Project for discriminative meta-path research.

## Run

**Before building, please make sure you have already got `maven` on your machine.**

1. Build project

        mvn clean compile assembly:single

The result `FindKpath-1.0-SNAPSHOT-jar-with-dependencies.jar` will be at `target/`.

2. The `Neo4j` database should be put under the same directory

        /
        /FindKpath-1.0-SNAPSHOT-jar-with-dependencies.jar
        /db

**The `Neo4j` database file must be named `db`, otherwise it will create a new database.**

3. Run `.jar` file with the following command

        java -jar FindKpath-1.0-SNAPSHOT-jar-with-dependencies.jar

## Test

TODO: The Unit tests in this project is executed by Intellij IDE, will provide a way to execute them without IDE in the future.

