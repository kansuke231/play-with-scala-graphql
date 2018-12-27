## Preparation
Before running the application, some imdb data have to be inserted into PostgreSQL tables.
`database_insert_postgres.sh` file contains useful commands for manipulating, formatting and inserting data.


## Tech stack
 For this task, I have selected Scala for the programming language, Scala-Akka for web server, Sangria for GraphQL library and PostgreSQL for storing IMDb data sets.
 
## Running the app
Simply run the following command 
```sh
$ sbt run
```
 and access to `http://localhost:8080` for the interactive GraphQL console.
 
 On the GraphQL console, if you try the following query:
```graphql
query {
    titleBasics(primaryTitle: "Matrix"){
        tconst
        originalTitle
        genres
    }
}
```
you should see the result as below:
```
{
  "data": {
    "titleBasics": {
      "tconst": "tt0106062",
      "originalTitle": "Matrix",
      "genres": [
        "Action",
        "Drama",
        "Fantasy"
      ]
    }
  }
}
```
