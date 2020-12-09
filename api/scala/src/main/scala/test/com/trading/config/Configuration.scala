package test.com.trading.config

object Configuration {

  def persistenceUser(): String = {
    getValue("db_user","DB_USER", "admin")
  }

  def persistenceUrl(): String = {
   getValue("db_url","DB_URL", "http://localhost:5984")
  }

  def persistencePassword(): String = {
    getValue("db_password","DB_PASSWORD", "123456")
  }

  private def getValue(prop: String, env: String, defaulVal: String): String = {
    val value = System.getenv(env)
    if (value == null) {
      return System.getProperty(prop, defaulVal)
    }
     value

  }
}
