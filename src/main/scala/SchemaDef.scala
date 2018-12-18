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
      )
    )
  )

  val schema = Schema(QueryType) //define entry point


}