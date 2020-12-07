package test.com.trading.casts


import cats.effect.{IO, _}
import fs2.Stream
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.Header
import org.http4s.circe._
import org.http4s.client.blaze._
import org.http4s.client.dsl.io._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import test.com.trading.CastsApi
import test.com.trading.requests.Pricing
import test.com.trading.service.domain.Cast
import test.com.trading.utils.CouchDbChangesFeed

import scala.concurrent.ExecutionContext.global


class CastsHttpApiSpec extends AnyFlatSpec with should.Matchers {

  //  extract to a test base class
  val url = uri"http://localhost:8080/casts/"

  if (!System.getProperty("isInt", "false").equals("true")) {
    //  start the http server
    val thread = new Thread {
      override def run = {

        CastsApi.main(Array("from_test"))
      }
    }
    thread.start
    Thread.sleep(500)
  }


  // Needed by `BlazeServerBuilder`
  implicit val cs: ContextShift[IO] = IO.contextShift(global)
  implicit val timer: Timer[IO] = IO.timer(global)


  def pricing(bondId: Int, buyOrSell: String, pricing: Pricing, originator: Long): Stream[IO, String] = {
    val req = POST(pricing.asJson, url / bondId.toString / buyOrSell, Header("User", originator.toString))
    BlazeClientBuilder[IO](global).stream.flatMap { httpClient =>
      Stream.eval(httpClient.expect(req)(jsonOf[IO, String]))
    }
  }

  def cancelPricing(bondId: Int, buyOrSell: String, originator: Long): Stream[IO, String] = {
    val req = DELETE(url / bondId.toString / buyOrSell, Header("User", originator.toString))
    BlazeClientBuilder[IO](global).stream.flatMap { httpClient =>
      Stream.eval(httpClient.expect(req)(jsonOf[IO, String]))
    }
  }


  def getActiveCasts(targetUserId: Long, originator: Long): Stream[IO, Array[Cast]] = {
    val req = GET(url / targetUserId.toString, Header("User", originator.toString))
    BlazeClientBuilder[IO](global).stream.flatMap { httpClient =>
      Stream.eval(httpClient.expect(req)(jsonOf[IO, Array[Cast]]))
    }
  }

  // todo better tests
  "pricing" should "watever" in {
    val t1 = 111L
    val t2 = 222L
    val t3 = 333L
    val originator = System.currentTimeMillis()

    val feedClientT1 = new CouchDbChangesFeed("casts", t1)
    val feedClientT2 = new CouchDbChangesFeed("casts", t2)
    val feedClientT3 = new CouchDbChangesFeed("casts", t3)

    Thread.sleep(200)

    pricing(1, "buy", Pricing(1, 1, Array(t1, t2)), originator).compile.last.unsafeRunSync()

    var casts = getActiveCasts(t1, originator).compile.last.unsafeRunSync().get
    casts.size should equal(1)
    casts(0).originatorId should equal(originator)
    casts(0).targetUserIds should equal(Array(t1, t2))
    //    wait a bit
    Thread.sleep(100)
    feedClientT1.feedMessages.size should equal(1)
    feedClientT2.feedMessages.size should equal(1)
    //    should be 0
    feedClientT3.feedMessages.size should equal(0)

    val castId2 = pricing(1, "sell", Pricing(1, 1, Array(t1, t2)), originator).compile.last.unsafeRunSync()
    val castId3 = pricing(1, "sell", Pricing(1, 2, Array(t1, t2)), originator).compile.last.unsafeRunSync()

    casts = getActiveCasts(t1, originator).compile.last.unsafeRunSync().get
    casts.size should equal(2)
    castId2 should equal(castId3)

    Thread.sleep(100)
    feedClientT1.feedMessages.size should equal(2)
    feedClientT2.feedMessages.size should equal(2)
    feedClientT3.feedMessages.size should equal(0)

    cancelPricing(1, "sell", originator).compile.last.unsafeRunSync()
    casts = getActiveCasts(t1, originator).compile.last.unsafeRunSync().get
    casts.size should equal(1)


  }

}
