import Models._
import slick.jdbc.H2Profile.api._
import com.github.tminglei.slickpg._

import scala.concurrent.Future
import scala.language.postfixOps
import scala.concurrent._
import ExecutionContext.Implicits.global



trait MyPostgresProfile extends ExPostgresProfile with PgArraySupport
  {
    override val api = MyAPI
  object MyAPI extends API with ArrayImplicits
    {
    implicit val strListTypeMapper = new SimpleArrayJdbcType[String]("text").to(_.toList)

  }
}

object MyPostgresProfile extends MyPostgresProfile


class MyContext(db: Database) {

  import MyContext._
  import MyPostgresProfile.api._

  def nameBasics(nconst: String): Future[Option[NameBasics]] = db.run(NameBasicsTables.filter(_.nconst === nconst).result.headOption)

  def titleBasics(primaryTitle: String): Future[Option[TitleBasics]] = {

    db.run(MovieBasicsTables.filter(_.primaryTitle === primaryTitle).result.headOption)
  }

  def movieWithCasts(title: String): Future[MovieInfo] = {

    val query =
      for {
        ((_, movie), name) <- TitlePrincipalsTables join  MovieBasicsTables on (_.tconst === _.tconst) join NameBasicsTables on (_._1.nconst === _.nconst)
        if movie.primaryTitle === title || movie.originalTitle === title
      } yield (movie.primaryTitle, movie.genres, name.primaryName, name.primaryProfession)

    db.run(query.result).map(
      table => {
        val castAndCrews = table.map(
          row => Person(row._3, row._4)
        )
        MovieInfo(table(0)._1, table(0)._2, castAndCrews.toList)
      }
    )

  }

  def topRatedMovies(genre: String, number: Int): Future[Seq[MovieWithRating]] = {

    val query =
      for {
        (movie, rating) <- MovieBasicsTables join  TitleRatingsTables on (_.tconst === _.tconst)
        if genre.bind === movie.genres.any
      } yield (movie.primaryTitle, rating.averageRating, rating.numVotes)


    //db.run(query.sortBy(columns => (columns._3.desc, columns._2.desc)).take(50).result).map(
    db.run(query.sortBy(_._3.desc).take(number).sortBy(_._2.desc).result).map(
      table => {
        table.map(
          row => MovieWithRating(row._1, genre, row._2, row._3)
        )
      }
    )

  }


  def isTypeCasted(name: String) = {

    val query =
      for {
        ((person, titlePrincipal), movie) <- NameBasicsTables join  TitlePrincipalsTables on ((n, tp) => n.nconst === tp.nconst) join MovieBasicsTables on (_._2.tconst === _.tconst)
        if name.bind === person.primaryName
      } yield (movie.genres)


    db.run(query.result).map(
      table => {

        val frequencies = table.flatten.groupBy(s => s).
          map{ s => Frequency(s._1, s._2.length)}.
          toList.
          sortWith(_.frequency > _.frequency)

        PersonWithGenres(
          name,
          table.length,
          frequencies,
          frequencies(0).frequency >= table.length / 2

        )
      }

    )

  }

}

object MyContext {

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


  class TitleRatingsTable(tag: Tag) extends Table[TitleRating](tag, "title_ratings") {

    def tconst = column[String]("tconst", O.PrimaryKey)
    def averageRating = column[Float]("average_rating")
    def numVotes = column[Int]("num_votes")


    def * = (tconst, averageRating, numVotes) <> ((TitleRating.apply _).tupled, TitleRating.unapply)
  }

  val TitleRatingsTables = TableQuery[TitleRatingsTable]


  class TitlePrincipalsTable(tag: Tag) extends Table[TitlePrincipals](tag, "title_principals") {

    def tconst = column[String]("tconst")
    def ordering = column[Int]("ordering")
    def nconst = column[String]("nconst")
    def category = column[String]("category")


    def * = (tconst, ordering, nconst, category) <> ((TitlePrincipals.apply _).tupled, TitlePrincipals.unapply)
  }

  val TitlePrincipalsTables = TableQuery[TitlePrincipalsTable]



  def createDatabase() = {
    val db = Database.forURL("jdbc:postgresql://localhost:5432/imdb", "username", "password",
      null, "org.postgresql.Driver")

    new MyContext(db)
  }


}