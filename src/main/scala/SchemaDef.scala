import sangria.schema._

object SchemaDef {

  import Models._
  import sangria.macros.derive._

  val IdentifiableType = InterfaceType(
    "Identifiable",
    "Entity that can be identified",
    fields[Unit, Identifiable](
      Field("id", IntType, resolve = _.value.id)
    )
  )


  implicit val PictureType: ObjectType[Unit, Picture] =
    deriveObjectType[Unit, Picture](
      ObjectTypeDescription("The product picture"),
      DocumentField("url", "Picture CDN URL")
    )

  // From here my stuff.

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

  implicit val MovieBasicsType: ObjectType[Unit, MovieBasics] =
    deriveObjectType[Unit, MovieBasics](
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
    fields[ShopRepository, Unit](


        // For all NameBasics
        Field("nameBasics", ListType(NameBasicsType),
        description = Some("Returns a list of name basics"),
          arguments = Argument("nconst", StringType) :: Nil,
        resolve = c => c.ctx.nameBasics(c.arg[String]("nconst"))
      ),

      // For all NameBasics
      Field("movieBasics", ListType(MovieBasicsType),
        description = Some("Returns a list of movie basics"),
        //arguments = Argument("ids", ListInputType(IntType)) :: Nil,
        resolve = c => c.ctx.movieBasics
      )
    )
  )

  val ShopSchema = Schema(QueryType) //define entry point
}