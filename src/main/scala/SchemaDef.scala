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

  implicit val ProductType: ObjectType[Unit, Product] =
    deriveObjectType[Unit, Product](
      Interfaces(IdentifiableType),
      IncludeMethods("picture") //by defaul macro cosinders fields only
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

  /**
    * Category
    */

  implicit val CategoryType: ObjectType[Unit, Category] =
    deriveObjectType[Unit, Category](
      Interfaces(IdentifiableType),
      ObjectTypeDescription("The category of products")
    )

  val QueryType = ObjectType(
    "Query",
    fields[ShopRepository, Unit](
      Field("allProducts", ListType(ProductType),
        description = Some("Returns a list of all available products."),
        resolve = _.ctx.allProducts
      ),
      Field("product", OptionType(ProductType),
        description = Some("Returns a product with specific `id`."),
        arguments = Argument("id", IntType) :: Nil,
        resolve = c => c.ctx.product(c.arg[ProductId]("id"))),


        // For all NameBasics
        Field("nameBasics", ListType(NameBasicsType),
        description = Some("Returns a list of name basics"),
        //arguments = Argument("ids", ListInputType(IntType)) :: Nil,
        resolve = c => c.ctx.nameBasics
      )
    )
  )

  val ShopSchema = Schema(QueryType) //define entry point
}