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



base="/Users/kansukeikehara/Documents/code/Git/play-with-scala-graphql/"
file="imdb_data/name.basics.modified.tsv"


# Skipping the header row
#tail -n +2  imdb_data/name.basics.tsv | awk  -F $'\t'  '$5="{"$5"}",$6="{"$6"}"' OFS='\t' > imdb_data/name.basics.modified.tsv

# Replace single quotes with double single quotes, so that Postgres won't complain.
#sed -i "s/'/''/g" imdb_data/name.basics.modified.tsv

# COPY the data from tsv file to Postgres
#psql -d imdb -h localhost -p 5432 -c "COPY name_basics FROM '$base$file' DELIMITER E'\t';"