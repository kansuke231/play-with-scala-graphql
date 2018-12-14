import Models._
import slick.jdbc.H2Profile.api._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

class ShopRepository(db: Database) {

  import ShopRepository._

  def allProducts = db.run(Products.result)

  def products(ids: Seq[ProductId]): Future[Seq[Product]] = db.run(Products.filter(_.id inSet ids).result)

  def product(id: ProductId): Future[Option[Product]] = db.run(Products.filter(_.id === id).result.headOption)

  def allCategories = db.run(Categories.result)

  def categories(ids: Seq[CategoryId]): Future[Seq[Category]] = db.run(Categories.filter(_.id inSet ids).result)

  def category(id: CategoryId): Future[Option[Category]] = db.run(Categories.filter(_.id === id).result.headOption)

  def nameBasics = db.run(NameBasicsTables.result)

}

object ShopRepository {

  class ProductTable(tag: Tag) extends Table[Product](tag, "PRODUCTS") {
    def id = column[ProductId]("PRODUCT_ID", O.PrimaryKey)

    def name = column[String]("NAME")

    def description = column[String]("DESCRIPTION")

    def price = column[BigDecimal]("PRICE")

    def * = (id, name, description, price) <> ((Product.apply _).tupled, Product.unapply)
  }

  val Products = TableQuery[ProductTable]

  class CategoryTable(tag: Tag) extends Table[Category](tag, "CATEGORY") {
    def id = column[CategoryId]("CATEGORY_ID", O.PrimaryKey)

    def name = column[String]("NAME")

    def * = (id, name) <> ((Category.apply _).tupled, Category.unapply)
  }

  val Categories = TableQuery[CategoryTable]


  // New stuff
  class NameBasicsTable(tag: Tag) extends Table[NameBasics](tag, "NAMEBASICS") {

    def nconst = column[String]("NCONST", O.PrimaryKey)
    def primaryName = column[String]("PRIMARY_NAME")
    def birthYear = column[String]("BIRTH_YEAR")
    def deathYear = column[String]("DEATH_YEAR")
    def primaryProfession = column[String]("PRIMARY_PROFESSION")
    def knownForTitles = column[String]("KNOWN_FOR_TITLES")

    def * = (nconst, primaryName, birthYear, deathYear, primaryProfession, knownForTitles) <> ((NameBasics.apply _).tupled, NameBasics.unapply)
  }

  val NameBasicsTables = TableQuery[NameBasicsTable]


  /**
    * JOIN TABLE
    */
  class TaxonomyTable(tag: Tag) extends Table[Taxonomy](tag, "PRODUCT_CATEGORY") {
    def productId = column[ProductId]("PRODUCT_ID")

    def categoryId = column[CategoryId]("CATEGORY_ID")

    //relations
    def product = foreignKey("PRODUCT_FK", productId, Products)(_.id)

    def category = foreignKey("CATEGORY_FK", categoryId, Categories)(_.id)

    def idx = index("UNIQUE_IDX", (productId, categoryId), unique = true)

    def * = (productId, categoryId) <> ((Taxonomy.apply _).tupled, Taxonomy.unapply)
  }

  val Taxonometry = TableQuery[TaxonomyTable]

  val databaseSetup = DBIO.seq(
    (Products.schema ++ Categories.schema ++ Taxonometry.schema ++ NameBasicsTables.schema ).create,

    Products ++= Seq(
      Product(1, "Cheescake", "Tasty", BigDecimal(12.34)),
      Product(2, "Health Potion", "+50 HP", BigDecimal(98.89)),
      Product(3, "Pineapple", "The biggest one", BigDecimal(0.99)),
      Product(4, "Bull's egg", "The left one", BigDecimal(100.99)),
      Product(5, "Water", "Bottled", BigDecimal(0.25)),
      Product(6, "Candle", "", BigDecimal(13.99))
    ),
    Categories ++= Seq(
      Category(1, "Food"),
      Category(2, "Magic ingredients"),
      Category(3, "Home interior")
    ),

    Taxonometry ++= Seq(
      Taxonomy(1, 1),
      Taxonomy(2, 2),
      Taxonomy(3, 1),
      Taxonomy(4, 1),
      Taxonomy(4, 2),
      Taxonomy(5, 1),
      Taxonomy(5, 2),
      Taxonomy(6, 3),
      Taxonomy(6, 2)
    ),

      // New stuff here.
      NameBasicsTables ++= Seq(
      NameBasics("nm0000001", "Fred Astaire",	"1899", "1987", "soundtrack,actor,miscellaneous", "tt0050419,tt0072308,tt0043044,tt0053137"),
      NameBasics("nm0000002", "Lauren Bacall",	"1924", "2014", "actress,soundtrack", "tt0038355,tt0117057,tt0037382,tt0071877")

    )
  )

  def createDatabase() = {
    val db = Database.forConfig("h2mem")

    Await.result(db.run(databaseSetup), 10 seconds)

    new ShopRepository(db)
  }

}