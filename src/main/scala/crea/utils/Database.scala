package crea.utils

import scalikejdbc._

import ConfigReader._

object Database {
  def connect() = {
    Class.forName("org.h2.Driver")
    val connectionString = ConfigReader.DBConfig.connectionString
    val user = ConfigReader.DBConfig.user
    val password = ConfigReader.DBConfig.password

    ConnectionPool.singleton(connectionString, user, password)

    AutoSession
  }
}
