package test.com.trading.persistence.impl.couchdb.domain

import test.com.trading.service.domain.Cast

class CastEntity(val _id: String, val _rev: String,val cast: Cast)
  extends Cast( cast.originatorId, cast.bondId, cast.buySell, cast.status, cast.price, cast.quantity, cast.targetUserIds) {
}