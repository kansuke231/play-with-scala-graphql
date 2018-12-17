#!/bin/bash

# You may need to do "chmod +x database_insert_postgres.sh"


#CREATE TABLE name_basics(
#    nconst VARCHAR PRIMARY KEY,
#    primary_name VARCHAR,
#    birth_year VARCHAR,
#    death_year VARCHAR,
#    primary_profession VARCHAR[],
#    known_for_titles VARCHAR[]
#);



base=""
file="imdb_data/name.basics.100.tsv"

{
# skip the header
read
while IFS=$'\t' read -r -a line
do
# echo "${line[4]}"
 nconst=${line[0]}

 if [[ ${line[1]} == *"'"* ]]
    then
    single_single_quotes="'"
    double_single_quotes="''"
    primary_name=${line[1]/$single_single_quotes/$double_single_quotes}
    else
    primary_name=${line[1]}
    fi

 birth_year=${line[2]}
 death_year=${line[3]}
 primary_profession=${line[4]}
 known_for_titles=${line[5]}

 psql -d imdb -h localhost -p 5432 -c "
    INSERT INTO name_basics (nconst, primary_name, birth_year, death_year, primary_profession, known_for_titles)
    VALUES('${nconst}','$primary_name','$birth_year','$death_year','{$primary_profession}','{$known_for_titles}');
 "
 > /dev/null # Not printing the output ()



done
} < $base$file