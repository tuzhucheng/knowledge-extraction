package crea.utils

import com.typesafe.config.ConfigFactory

object ConfigReader {
  private val config =  ConfigFactory.load()

  private lazy val root = config.getConfig("relation-extraction")

  object DBConfig {
    private val dbConfig = root.getConfig("mysql")
    lazy val connectionString = dbConfig.getString("connectionString")
    lazy val user = dbConfig.getString("user")
    lazy val password = dbConfig.getString("password")
  }
}
