/*
This is a script used to initialize the database with tables necessary to
download abstracts, parsed sentences, and relations.

It assumes that you do not yet have a relation_extraction database.
If you have one, you need to back up the data on it first (if neccesary)
and remove it before running this script.
*/

CREATE DATABASE relation_extraction;

use relation_extraction;

CREATE TABLE terms (id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                    term VARCHAR(100),
                    search BOOL NOT NULL, # search is to enable/disable search term
                    date_added DATE);

CREATE TABLE abstracts (id INT NOT NULL PRIMARY KEY, # UID of article
                        db VARCHAR(50),              # db source, e.g. pubmed, genome
                        pub_date DATE,               # publication date
                        journal_title VARCHAR(1000),
                        journal_vol VARCHAR(50),
                        journal_issue VARCHAR(50),
                        authors VARCHAR(1000),
                        title VARCHAR(1000),
                        abstract TEXT,
                        retrieve_date DATE);         # time retrieved

CREATE TABLE terms_abstracts (id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                              term_id INT,
                              abstract_id INT,
                              FOREIGN KEY (term_id)
                                REFERENCES terms(id)
                                ON UPDATE CASCADE ON DELETE SET NULL,
                              FOREIGN KEY (abstract_id)
                                REFERENCES abstracts(id)
                                ON UPDATE CASCADE ON DELETE SET NULL);

CREATE TABLE sentences (id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                        abstract_id INT,             # id of the abstract
                        sentence_num INT,            # zero-based index of sentence in abstract
                        sentence VARCHAR(2000),
                        parse_tree TEXT,
                        FOREIGN KEY (abstract_id)
                          REFERENCES abstracts(id)
                          ON UPDATE CASCADE ON DELETE RESTRICT);

CREATE TABLE relations (id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                        abstract_id INT,
                        sentence_id INT,
                        subject VARCHAR(100),
                        predicate VARCHAR(100),
                        object VARCHAR(100),
                        extract_date DATE,
                        FOREIGN KEY (abstract_id)
                          REFERENCES abstracts(id)
                          ON UPDATE CASCADE ON DELETE RESTRICT,
                        FOREIGN KEY (sentence_id)
                          REFERENCES sentences(id)
                          ON UPDATE CASCADE ON DELETE RESTRICT);
