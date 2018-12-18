import Models._
import slick.jdbc.H2Profile.api._
import com.github.tminglei.slickpg._

import scala.language.postfixOps



trait MyPostgresProfile extends ExPostgresProfile
  with PgArraySupport
  {
    override val api = MyAPI
  object MyAPI extends API with ArrayImplicits
    {
    implicit val strListTypeMapper = new SimpleArrayJdbcType[String]("text").to(_.toList)

  }
}

object MyPostgresProfile extends MyPostgresProfile


class ShopRepository(db: Database) {

  import ShopRepository._

//  def allProducts = db.run(Products.result)
//
//  def products(ids: Seq[ProductId]): Future[Seq[Product]] = db.run(Products.filter(_.id inSet ids).result)
//
//  def product(id: ProductId): Future[Option[Product]] = db.run(Products.filter(_.id === id).result.headOption)
//
//  def allCategories = db.run(Categories.result)
//
//  def categories(ids: Seq[CategoryId]): Future[Seq[Category]] = db.run(Categories.filter(_.id inSet ids).result)
//
//  def category(id: CategoryId): Future[Option[Category]] = db.run(Categories.filter(_.id === id).result.headOption)

  def nameBasics(nconst: String) = db.run(NameBasicsTables.filter(_.nconst === nconst).result.headOption)

  def titleBasics(tconst: String) = db.run(MovieBasicsTables.filter(_.tconst === tconst).result.headOption)

//  def movieInfo(primaryTitle: String) = {
//    val a = db.run(MovieBasicsTables.filter(_.primaryTitle === primaryTitle).result)
//    a.
//
//  }

}

object ShopRepository {

  import MyPostgresProfile.api._


  class NameBasicsTable(tag: Tag) extends Table[NameBasics](tag,  "name_basics") {

    def nconst = column[String]("nconst",O.PrimaryKey)
    def primaryName = column[String]("primary_name")
    def birthYear = column[Option[String]]("birth_year")
    def deathYear = column[Option[String]]("death_year")
    def primaryProfession = column[List[String]]("primary_profession")
    def knownForTitles = column[List[String]]("known_for_titles")

    def * = (nconst, primaryName, birthYear, deathYear, primaryProfession, knownForTitles) <> (NameBasics.tupled, NameBasics.unapply)
  }

  val NameBasicsTables = TableQuery[NameBasicsTable]

  class TitleBasicsTable(tag: Tag) extends Table[TitleBasics](tag, "title_basics") {

    def tconst = column[String]("tconst", O.PrimaryKey)
    def titleType = column[String]("title_type")
    def primaryTitle = column[String]("primary_title")
    def originalTitle = column[String]("original_title")
    def isAdult = column[String]("is_adult")
    def startYear = column[String]("start_year")
    def endYear = column[Option[String]]("end_year")
    def runtimeMinutes = column[String]("runtime_minutes")
    def genres = column[List[String]]("genres")

    def * = (tconst, titleType, primaryTitle, originalTitle, isAdult, startYear, endYear, runtimeMinutes, genres) <> ((TitleBasics.apply _).tupled, TitleBasics.unapply)
  }

  val MovieBasicsTables = TableQuery[TitleBasicsTable]



  def createDatabase() = {
    val db = Database.forURL("jdbc:postgresql://localhost:5432/imdb", "username", "password",
      null, "org.postgresql.Driver")

    new ShopRepository(db)
  }


}