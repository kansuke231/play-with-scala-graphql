

object Models {

  case class NameBasics(
                         nconst: String,
                         primaryName: String,
                         birthYear: Option[String],
                         deathYear: Option[String],
                         primaryProfession: List[String], // array entry
                         knownForTitles: List[String] // array entry
                       )


  case class TitleBasics(
                          tconst: String,
                          titleType: String,
                          primaryTitle: String,
                          originalTitle: String,
                          isAdult: String,
                          startYear: String,
                          endYear: Option[String],
                          runtimeMinutes: String,
                          genres: List[String], // array entry
                        )

  case class TitlePrincipals(
                              tconst: String,
                              ordering: Int,
                              nconst: String,
                              category: String
                            )

  case class Person(primaryName: String, primaryProfession: List[String])

  case class MovieInfo(
                        primaryTitle: String,
                        genres: List[String],
                        castsAndCrews: List[Person]
                        )

  case class TitleRating(
                   tconst: String,
                   averageRating: Float,
                   numVotes: Int
                   )

  case class MovieWithRating(
                            primaryTitle: String,
                            genre: String,
                            averageRating: Double,
                            numVotes: Int
                            )

  case class PersonWithGenres(name: String, numberOfMovies: Int, frequencies: List[Frequency], isTypeCasted: Boolean)

  case class Frequency(genre: String, frequency: Int)

  case class SharedMovies(person1: String, person2: String, sharedMovies: List[String])
}



