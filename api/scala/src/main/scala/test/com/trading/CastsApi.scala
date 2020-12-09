package test.com.trading

import org.http4s.{EntityDecoder, HttpRoutes, Request}
import org.http4s.server.middleware.{CORS, Logger}
import cats.effect._
import io.circe.generic.auto._
import io.circe.syntax.EncoderOps
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.server.blaze._
import org.http4s.implicits._
import test.com.trading.requests.Pricing
import org.http4s.util.CaseInsensitiveString
import test.com.trading.service.CastsService

import scala.concurrent.ExecutionContext.global

object CastsApi extends IOApp {

  implicit val decoder: EntityDecoder[IO, Pricing] = jsonOf[IO, Pricing]

  val castService = new CastsService
  //     todo input validations
  val castRoutes = HttpRoutes.of[IO] {

    case req@POST -> Root / "casts" / IntVar(bondId) / buyOrSellOption =>
      val originator = getOriginator(req)
      val pricing = req.as[Pricing].unsafeRunSync()

      val castId = castService.cast(originator, bondId, buyOrSellOption, pricing)
      Ok(s"${castId}".asJson)

    case req@GET -> Root / "casts" / LongVar(targetId) =>
      val originator = getOriginator(req)
      val casts = castService.active(originator, targetId)
      Ok(casts.asJson);

    case req@DELETE -> Root / "casts" / IntVar(bondId) / buyOrSellOption =>
      val originator = getOriginator(req)
      castService.cancel(originator, bondId, buyOrSellOption)
      Ok(s"Canceled ,$buyOrSellOption $bondId ".asJson)
  }.orNotFound
  // for local testing
  val corsService = CORS(castRoutes)

  def getOriginator(req: Request[IO]): Long = {
    req.headers.get(CaseInsensitiveString("User")).get.value.toLong
  }

  def run(args: List[String]): IO[ExitCode] = {
    BlazeServerBuilder[IO](global)
      .bindHttp(8080,"0.0.0.0")
      .withHttpApp(Logger.httpApp(true, true)(corsService))
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
  }
}