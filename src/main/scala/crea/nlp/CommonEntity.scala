package crea.nlp

import scalikejdbc._

package object CommonEntity {
  val DBConnectionString = "jdbc:mysql://localhost:3306/relation_extraction"

  case class SearchTerm(id: Long, term: String, search: Boolean, dateAdded: String)
  object SearchTerm extends SQLSyntaxSupport[SearchTerm] {
    override val tableName = "terms"
    def apply(rs: WrappedResultSet) = new SearchTerm(rs.get("id"), rs.get("term"), rs.get("search"), rs.get("date_added"))
  }

  case class ArticleAbstract(id: Long, pubmedJournalId: Long, articleDoi: String, title: String, articleAbstract: String, dateAdded: String)
  object ArticleAbstract extends SQLSyntaxSupport[ArticleAbstract] {
    override val tableName = "abstracts"
    def apply(rs: WrappedResultSet) = new ArticleAbstract(rs.get("id"), rs.get("pubmed_journal_id"), rs.get("article_doi"), rs.get("title"), rs.get("abstract"), rs.get("date_added"))
  }
}
