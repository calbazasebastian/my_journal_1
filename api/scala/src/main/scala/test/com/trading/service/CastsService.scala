package test.com.trading.service

import test.com.trading.persistence.PersistenceAPI
import test.com.trading.persistence.impl.couchdb.CouchDbClient
import test.com.trading.requests.Pricing
import test.com.trading.service.domain.Cast

class CastsService {

  val persistenceAPi: PersistenceAPI = new CouchDbClient

  def cast(originator: Long, bondId: Int, buySell: String, pricing: Pricing): String = {
    val cast = persistenceAPi.findCast[Cast](originator, bondId, buySell)
    if (cast == null) {
      persistenceAPi.saveCast(Cast(originator, bondId, buySell, "active", pricing.price, pricing.quantity, pricing.targetUserIds))
    } else {
      persistenceAPi.updateCast(Cast(originator, bondId, buySell, "active", pricing.price, pricing.quantity, pricing.targetUserIds))
    }

  }

  def cancel(originator: Long, bondId: Int, buySell: String): String = {
    var cast = persistenceAPi.findCast[Cast](originator, bondId, buySell)
    cast = cast.copy(status = "inactive")
    persistenceAPi.updateCast(cast)
  }

  def active(originator: Long, targetUserId: Long): Array[Cast] = {
    persistenceAPi.findActiveCasts(originator, targetUserId)
  }
}
