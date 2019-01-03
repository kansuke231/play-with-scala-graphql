import sangria.schema._


object SchemaDef {

  import Models._
  import sangria.macros.derive._


  implicit val NameBasicsType: ObjectType[Unit, NameBasics] =
    deriveObjectType[Unit, NameBasics](
      ObjectTypeDescription("Information for NameBasics"),
      DocumentField("nconst", "alphanumeric unique identifier of the name/person"),
      DocumentField("primaryName", "name by which the person is most often credited"),
      DocumentField("birthYear", " in YYYY format"),
      DocumentField("deathYear", "in YYYY format if applicable, else '\\N'"),
      DocumentField("primaryProfession", "(array of strings)– the top-3 professions of the person"),
      DocumentField("knownForTitles", "(array of tconsts) – titles the person is known for")
    )

  implicit val TitleBasicsType: ObjectType[Unit, TitleBasics] =
    deriveObjectType[Unit, TitleBasics](
      ObjectTypeDescription("Information for MovieBasics"),
      DocumentField("tconst", ""),
      DocumentField("titleType", ""),
      DocumentField("primaryTitle", ""),
      DocumentField("originalTitle", ""),
      DocumentField("isAdult", ""),
      DocumentField("startYear", ""),
      DocumentField("endYear", ""),
      DocumentField("runtimeMinutes", ""),
      DocumentField("genres", "")
    )

  implicit val PersonType: ObjectType[Unit, Person] =
    deriveObjectType[Unit, Person](
      ObjectTypeDescription("Cast for a specific movie."),
      DocumentField("primaryName", ""),
      DocumentField("primaryProfession", ""),
    )

  implicit val MovieInfoType: ObjectType[Unit, MovieInfo] =
    deriveObjectType[Unit, MovieInfo](
      ObjectTypeDescription("Information for MovieInfo"),
      DocumentField("primaryTitle", ""),
      DocumentField("genres", ""),
    )

  implicit val MovieWithRatingType: ObjectType[Unit, MovieWithRating] =
    deriveObjectType[Unit, MovieWithRating](
      ObjectTypeDescription("Top rated movies of a specific genre."),
      DocumentField("primaryTitle", ""),
      DocumentField("genre", ""),
      DocumentField("averageRating", ""),
      DocumentField("numVotes", "")
    )

  implicit val PersonWithGenresType: ObjectType[Unit, PersonWithGenres] =
    deriveObjectType[Unit, PersonWithGenres](
      ObjectTypeDescription(""),
      DocumentField("name", ""),
      DocumentField("numberOfMovies", ""),
      DocumentField("frequencies", ""),
      DocumentField("isTypeCasted", "")
    )

  implicit val FrequencyType: ObjectType[Unit, Frequency] =
    deriveObjectType[Unit, Frequency](
      ObjectTypeDescription(""),
      DocumentField("genre", ""),
      DocumentField("frequency", ""),
    )

  implicit val SharedMoviesType: ObjectType[Unit, SharedMovies] =
    deriveObjectType[Unit, SharedMovies](
      ObjectTypeDescription(""),
      DocumentField("person1", ""),
      DocumentField("person2", ""),
      DocumentField("sharedMovies", "")
    )



  val QueryType = ObjectType(
    "Query",
    fields[MyContext, Unit](

        Field("nameBasics", OptionType(NameBasicsType),
          description = Some("Returns information of the persion for a specific nconst"),
          arguments = Argument("nconst", StringType) :: Nil,
        resolve = c => c.ctx.nameBasics(c.arg[String]("nconst"))
      ),

      Field("titleBasics", OptionType(TitleBasicsType),
        description = Some("Returns information of the specified movie"),
        arguments = Argument("primaryTitle", StringType) :: Nil,
        resolve = c => c.ctx.titleBasics(c.arg[String]("primaryTitle"))
      ),

      Field("movieWithCastsAndCrews", OptionType(MovieInfoType),
        description = Some("Returns information of the specified movie"),
        arguments = Argument("title", StringType) :: Nil,
        resolve = c => c.ctx.movieWithCasts(c.arg[String]("title"))
      ),

      Field("topRatedMovies", ListType(MovieWithRatingType),
        description = Some("Returns top rated movies of a specific genre"),
        arguments = Argument("genre", StringType) :: Argument("number", IntType) :: Nil,
        resolve = c => c.ctx.topRatedMovies(c.arg[String]("genre"), c.arg[Int]("number"))
      ),

      Field("personWithGenres", PersonWithGenresType,
        description = Some(""),
        arguments = Argument("name", StringType) :: Nil,
        resolve = c => c.ctx.isTypeCasted(c.arg[String]("name"))
      ),

      Field("sharedMovies", SharedMoviesType,
        description = Some(""),
        arguments = Argument("person1", StringType) :: Argument("person2", StringType) :: Nil,
        resolve = c => c.ctx.sharedMovies(c.arg[String]("person1"), c.arg[String]("person2"))
      )
    )
  )

  val schema = Schema(QueryType) //define entry point


}