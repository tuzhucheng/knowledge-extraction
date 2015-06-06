package crea.nlp

import scala.io.Source

import java.text.SimpleDateFormat
import java.util.Calendar

import scalikejdbc._

object TermsImporter extends App {

  private[this] implicit val logger = org.log4s.getLogger
  val DBConnectionString = "jdbc:mysql://localhost:3306/relation_extraction"
  val termsFile = "aging_map_terms.txt"

  logger.info("TermsImporter is started.")
  Class.forName("org.h2.Driver")

  ConnectionPool.singleton(DBConnectionString, "creauser", "creauser")

  implicit val session = AutoSession

  val dateFormat = new SimpleDateFormat("yyyy-MM-dd")
  val formattedDate = dateFormat.format(Calendar.getInstance().getTime())

  case class SearchTerm(id: Long, term: String, search: Boolean, dateAdded: String)
  object SearchTerm extends SQLSyntaxSupport[SearchTerm] {
    override val tableName = "terms"
    def apply(rs: WrappedResultSet) = new SearchTerm(rs.get("id"), rs.get("term"), rs.get("search"), rs.get("date_added"))
  }

  for (line <- Source.fromFile(termsFile).getLines()) {
    SQL("INSERT INTO terms (term, search, date_added) VALUES ({term}, TRUE, {date_added})")
    .bindByName(
      'term -> line,
      'date_added -> formattedDate
    ).map(rs => SearchTerm(rs)).execute.apply()
  }

}
