package crea.nlp

import scalikejdbc._
import scalaj.http.Http

import CommonEntity._

object PubmedToDBDriver {

  private[this] implicit val logger = org.log4s.getLogger
  Class.forName("org.h2.Driver")
  ConnectionPool.singleton(DBConnectionString, "creauser", "creauser")
  implicit val session = AutoSession

  def main(args: Array[String]) {
    logger.info("PubmedToDBDriver is started.")

    val terms: List[SearchTerm] = sql"SELECT * from terms".map(rs => SearchTerm(rs)).list.apply()

    val BaseNCBIEUtilsSearchURL = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi"
    val BaseNCBIEUtilsFetchURL = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi"

    val searchRes = Http(BaseNCBIEUtilsSearchURL).param("db", "pubmed").param("term", "stem").asString
    val searchResBody = scala.xml.XML.loadString(searchRes.body)
    val articleIds = (searchResBody \ "IdList" \ "Id").map(_.text)

    for (articleId <- articleIds) {
      val fetchRes = Http(BaseNCBIEUtilsFetchURL).param("db", "pubmed").param("id", articleId).param("retmode", "xml").param("rettype", "abstract").asString
      val fetchResBody = scala.xml.XML.loadString(fetchRes.body)

      val doiList = for {
        item <- (fetchResBody \ "PubmedArticle" \ "PubmedData" \ "ArticleIdList" \ "ArticleId" )
        if (item \ "@IdType").text == "doi"
      } yield item.text

      val doi = if (doiList.length >= 1) doiList(0) else "N/A"

      val journalId = (fetchResBody \ "PubmedArticle" \ "MedlineCitation" \ "MedlineJournalInfo" \ "NlmUniqueID" ).text
      val articleTitle = (fetchResBody \ "PubmedArticle" \ "MedlineCitation" \ "Article" \ "ArticleTitle" ).text
      val abstractText = (fetchResBody \ "PubmedArticle" \ "MedlineCitation" \ "Article" \ "Abstract" \ "AbstractText" ).text
      val dateCreated = (fetchResBody \ "PubmedArticle" \ "MedlineCitation" \ "DateCreated")
      val dateAdded = (dateCreated \ "Year").text + "-" + (dateCreated \ "Month").text + "-" + (dateCreated \ "Day").text

      SQL("INSERT INTO abstracts (pubmed_journal_id, article_doi, title, abstract, date_added) VALUES ({pubmed_journal_id}, {article_doi}, {title}, {abstract}, {date_added})")
      .bindByName(
        'pubmed_journal_id -> journalId,
        'article_doi -> doi,
        'title -> articleTitle,
        'abstract -> abstractText,
        'date_added -> dateAdded
      ).map(rs => ArticleAbstract(rs)).execute.apply()
    }
  }

}
