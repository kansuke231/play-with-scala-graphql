

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

  case class Cast(primaryName: String, primaryProfession: List[String])

  case class MovieInfo(
                        primaryTitle: String,
                        genres: List[String],
                        casts: List[Cast]
                        )
}



