package test.com.trading.service.domain

case class Cast(originatorId: Long, bondId: Int, buySell: String, status: String, price: Int, quantity: Int, targetUserIds: Array[Long])
