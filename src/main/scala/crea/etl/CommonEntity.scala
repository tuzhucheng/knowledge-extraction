package crea.etl

import scalikejdbc._

package object CommonEntity {
  val DBConnectionString = "jdbc:mysql://localhost:3306/relation_extraction"

  case class SearchTerm(id: Long, term: String, search: Boolean, dateAdded: String)
  object SearchTerm extends SQLSyntaxSupport[SearchTerm] {
    override val tableName = "terms"
    def apply(rs: WrappedResultSet) = new SearchTerm(rs.get("id"), rs.get("term"), rs.get("search"), rs.get("date_added"))
  }

  case class ArticleAbstract(id: Int, db: String, publishDate: String, journalTitle: String, journalVol: String, journalIssue: String, authors: String, title: String, articleAbstract: String, retrieveDate: String)
  object ArticleAbstract extends SQLSyntaxSupport[ArticleAbstract] {
    override val tableName = "abstracts"
    def apply(rs: WrappedResultSet) = new ArticleAbstract(rs.get("id"),
                                                          rs.get("db"),
                                                          rs.get("pub_date"),
                                                          rs.get("journal_title"),
                                                          rs.get("journal_vol"),
                                                          rs.get("journal_issue"),
                                                          rs.get("authors"),
                                                          rs.get("title"),
                                                          rs.get("abstract"),
                                                          rs.get("retrieve_date"))
  }
}
