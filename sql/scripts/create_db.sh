#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
psql -h 127.0.0.1 mydb < /extra/jhsie007/CafePOS/sql/src/create_tables.sql
psql -h 127.0.0.1 mydb < $DIR/../src/create_indexes.sql
psql -h 127.0.0.1 mydb < /extra/jhsie007/CafePOS/sql/src/load_data.sql
