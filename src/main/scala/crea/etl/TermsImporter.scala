package crea.etl

import scala.io.Source

import java.text.SimpleDateFormat
import java.util.Calendar

import scalikejdbc._

import CommonEntity._
import crea.utils.Database

object TermsImporter extends App {

  private[this] implicit val logger = org.log4s.getLogger
  val termsFile = "aging_map_terms.txt"

  logger.info("TermsImporter is started.")

  implicit val session = Database.connect()

  val dateFormat = new SimpleDateFormat("yyyy-MM-dd")
  val formattedDate = dateFormat.format(Calendar.getInstance().getTime())

  for (line <- Source.fromFile(termsFile).getLines()) {
    SQL("INSERT INTO terms (term, search, date_added) VALUES ({term}, TRUE, {date_added})")
    .bindByName(
      'term -> line,
      'date_added -> formattedDate
    ).map(rs => SearchTerm(rs)).execute.apply()
  }

}
