package crea.etl

import scalikejdbc._
import scalaj.http.Http

import scala.collection.mutable.ListBuffer
import scala.xml.Node

import java.util.Calendar
import java.text.SimpleDateFormat

import CommonEntity._

object PubmedToDBDriver {

  private[this] implicit val logger = org.log4s.getLogger
  Class.forName("org.h2.Driver")
  ConnectionPool.singleton(DBConnectionString, "creauser", "creauser")
  implicit val session = AutoSession

  val today = Calendar.getInstance().getTime()
  val dateFormat = new SimpleDateFormat("yyyy-MM-dd")
  val retrieveDateStr = dateFormat.format(today)

  val BaseNCBIEUtilsSearchURL = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi"
  val BaseNCBIEUtilsFetchURL = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi"

  val PUBMED = "pubmed"

  def storeArticleRecord(searchTermId: Int, articleRecord:Node) = {
    val medlineCitation = articleRecord \ "MedlineCitation"
    val pmid = (medlineCitation \ "PMID").text.toInt

    val article = medlineCitation \ "Article"

    // Fetching journal information
    val journalTitle = (article \ "Journal" \ "Title").text
    val journalVolume = (article \ "Journal" \ "JournalIssue" \ "Volume").text
    val journalIssue = (article \ "Journal" \ "JournalIssue" \ "Issue").text

    // Fetching article information
    val articleTitle = (article \ "ArticleTitle").text
    val abstractText = (article \ "Abstract" \ "AbstractText").text
    val dateCreated = (medlineCitation \ "DateCreated")
    val dateCreatedFormatted = (dateCreated \ "Year").text + "-" + (dateCreated \ "Month").text + "-" + (dateCreated \ "Day").text

    // Fetching author information. Each author is stored as "lastname, firstname".
    // Multiple authors are joined together by semicolon.
    val authorsList = (article \ "AuthorList" \ "Author").toList
    val authorsBuffer = ListBuffer.empty[String]
    for (author <- authorsList) {
      val lastName = (author \ "LastName").text
      val foreName = (author \ "ForeName").text
      // val initials = (author \ "Initials").text
      authorsBuffer += lastName + ", " + foreName
    }
    val authorsString = authorsBuffer.toList.mkString(";")
    println(Array(pmid, dateCreatedFormatted, journalTitle, journalVolume, journalIssue, articleTitle, authorsString, retrieveDateStr).deep.mkString(","))

    // Store article in abstracts table
    SQL("INSERT IGNORE INTO abstracts (id, db, pub_date, journal_title, journal_vol, journal_issue, authors, title, abstract, retrieve_date) VALUES ({id}, {db}, {pub_date}, {journal_title}, {journal_vol}, {journal_issue}, {authors}, {title}, {abstract}, {retrieve_date})")
    .bindByName(
      'id -> pmid,
      'db -> PUBMED,
      'pub_date -> dateCreatedFormatted,
      'journal_title -> journalTitle,
      'journal_vol -> journalVolume,
      'journal_issue -> journalIssue,
      'authors -> authorsString,
      'title -> articleTitle,
      'abstract -> abstractText,
      'retrieve_date -> retrieveDateStr
    ).map(rs => ArticleAbstract(rs)).execute.apply()

    // Store term used to find article in terms_abstracts table
    SQL("INSERT IGNORE INTO terms_abstracts (term_id, abstract_id) VALUES ({term_id}, {abstract_id})")
    .bindByName(
      'term_id -> searchTermId,
      'abstract_id -> pmid
    ).map(rs => TermAbstractLink(rs)).execute.apply()
  }

  def main(args: Array[String]) {
    logger.info("PubmedToDBDriver is started.")

    val searchTerms: List[SearchTerm] = sql"SELECT * from terms".map(rs => SearchTerm(rs)).list.apply()

    for (searchTerm <- searchTerms) {
      if (searchTerm.search) {
        val term = searchTerm.term
        val searchTermId = searchTerm.id

        // Get article ids for each search term and put them up on the history server
        val searchRes = Http(BaseNCBIEUtilsSearchURL).param("db", "pubmed")
                                                     .param("term", term)
                                                     .param("datetype", "pdat")
                                                     .param("mindate", "2014/01/01")
                                                     .param("maxdate", "2014/01/31")
                                                     .param("usehistory", "y")
                                                     .asString

        val searchResBody = scala.xml.XML.loadString(searchRes.body)
        val articleCount = (searchResBody \ "Count").text.toInt
        val webEnv = (searchResBody \ "WebEnv").text
        val queryKey = (searchResBody \ "QueryKey").text

        Thread sleep 1000

        // Get all article records in batches of 500
        var (retStart, retMax) = (0, 500)
        while (retStart < articleCount) {
          val articleRecordRes = Http(BaseNCBIEUtilsFetchURL).param("db", "pubmed")
                                                             .param("WebEnv", webEnv)
                                                             .param("query_key", queryKey)
                                                             .param("retmode", "xml")
                                                             .param("retstart", retStart.toString)
                                                             .param("retmax", retMax.toString)
                                                             .asString

          val articleRecordResBody = scala.xml.XML.loadString(articleRecordRes.body)
          val articleRecords = (articleRecordResBody \ "PubmedArticle").toList
          for (articleRecord <- articleRecords) {
              storeArticleRecord(searchTermId, articleRecord)
          }
          retStart += retMax
          Thread sleep 1000
        }
      }
    }
  }

}
