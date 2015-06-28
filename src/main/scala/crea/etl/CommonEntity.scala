package crea.etl

import scalikejdbc._

package object CommonEntity {
  val DBConnectionString = "jdbc:mysql://localhost:3306/relation_extraction"

  case class SearchTerm(id: Int, term: String, search: Boolean, dateAdded: String)
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

  case class TermAbstractLink(id: Int, termId: Int, abstractId: Int)
  object TermAbstractLink extends SQLSyntaxSupport[TermAbstractLink] {
    override val tableName = "terms_abstracts"
    def apply(rs: WrappedResultSet) = new TermAbstractLink(rs.get("id"), rs.get("term_id"), rs.get("abstract_id"))
  }

  case class Sentence(id: Int, abstractId: Int, sentenceNum: Int, sentence: String, parseTree: String)
  object Sentence extends SQLSyntaxSupport[Sentence] {
    override val tableName = "sentences"
    def apply(rs: WrappedResultSet) = new Sentence(rs.get("id"), rs.get("abstract_id"), rs.get("sentence_num"), rs.get("sentence"), rs.get("parse_tree"))
  }
}
