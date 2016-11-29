#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# compile the java program
javac -d $DIR/../src $DIR/../src/Cafe.java

#run the java program
#Use your database name and portss
java -cp $DIR/../src:$CLASSPATH Cafe mydb $PGPORT

