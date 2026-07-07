#!/bin/bash

set -ex

cd "$(dirname "$0")"

./docker/wait-for "$DB_HOST":3306 -t 120

if [ "$1" == "dev" ]
then
    ./mvnw clean package -DskipTests
fi

java -jar target/mm-cq-java-2-0.0.1-SNAPSHOT.jar &
sleep 5

# preparing demo data
java -jar target/mm-cq-java-2-0.0.1-SNAPSHOT.jar store:import
java -jar target/mm-cq-java-2-0.0.1-SNAPSHOT.jar product:import
java -jar target/mm-cq-java-2-0.0.1-SNAPSHOT.jar product:quantity
java -jar target/mm-cq-java-2-0.0.1-SNAPSHOT.jar offer:import r001
java -jar target/mm-cq-java-2-0.0.1-SNAPSHOT.jar offer:import r002
java -jar target/mm-cq-java-2-0.0.1-SNAPSHOT.jar offer:import r003
