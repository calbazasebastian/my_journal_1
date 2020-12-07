package test.com.trading.utils

import java.net.URL

import com.cloudant.client.api.model.ChangesResult
import com.cloudant.client.api.{ClientBuilder, Database}
import test.com.trading.config.Configuration

class CouchDbChangesFeed(dbname: String, traderId: Long) {
  var feedData: List[ChangesResult.Row] = List()

  val changesThread = new Thread {
    override def run = {
      // feed type continuous
      val client = ClientBuilder.url(new URL(Configuration.persistenceUrl()))
        .username(Configuration.persistenceUser())
        .password(Configuration.persistencePassword())
        .build()

      val db: Database = client.database(dbname, false)
      val since = db.info.getUpdateSeq

      val changes = db.changes()
        .since(since)
        .filter("casts/trader")
        .parameter("traderId", traderId.toString)
        .includeDocs(true)
        .heartBeat(30000)
        .continuousChanges()

      while (changes.hasNext()) {
        val feed = changes.next();
        feedData = feedData.+:(feed)
        println("testing " + feed.getId)
      }
    }
  }
  changesThread.start

  def feedMessages: List[ChangesResult.Row] = {
    //    todo not thread safe
    val feedDataBack = feedData
    feedData = List()
    feedDataBack
  }

}
