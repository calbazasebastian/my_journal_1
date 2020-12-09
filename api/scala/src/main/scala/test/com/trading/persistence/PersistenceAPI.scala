package test.com.trading.persistence

import test.com.trading.service.domain.Cast

trait PersistenceAPI {
  def saveCast(cast: Cast):String

  def updateCast(cast: Cast): String

  def findCast[A<:Cast](originator: Long, bondId: Int, buySell: String): A

  def findActiveCasts[A<:Cast](originator: Long, targetId: Long): Array[A]
}