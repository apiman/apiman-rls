import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import scala.util.Random

abstract class ApimanRlsSimulation extends Simulation {
  
  var rlsHost = "http://bluejay:8080";
  
}

class SmokeTestSimulation extends ApimanRlsSimulation {

  val httpConf = http
    .baseURL(rlsHost)
    .acceptHeader("application/json,application/xml;q=0.9,*/*;q=0.8")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0")

  val scn = scenario("SmokeTestScenario")
    .exec(_.set("LimitId", "SmokeTest-Limit-1"))
    .exec(_.set("MaxLimitValue", 2000))
    .exec(http("Root").get("/"))
    .exec(http("CreateLimit").post("/limits/").header("Content-Type", "application/json").body(io.gatling.core.body.ElFileBody("create-limit.json")).asJSON)
    .repeat(10, "n") {
      exec(http("IncrementLimit").put("/limits/${LimitId}").header("Content-Type", "application/json").body(io.gatling.core.body.ElFileBody("increment-limit.json")).asJSON)
    }

  setUp(
    scn.inject(atOnceUsers(100))
  ).protocols(httpConf)
}


class SingleRateLimitSimulation extends ApimanRlsSimulation {

  val httpConf = http
    .baseURL(rlsHost)
    .acceptHeader("application/json,application/xml;q=0.9,*/*;q=0.8")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0")

  val scn = scenario("SingleRateLimitScenario")
    .exec(_.set("LimitId", "Limit-1"))
    .exec(_.set("MaxLimitValue", 20000000))
    .exec(http("Root").get("/"))
    .exec(http("CreateLimit").post("/limits/").header("Content-Type", "application/json").body(io.gatling.core.body.ElFileBody("create-limit.json")).asJSON)
    .pause(1)
    .repeat(1000, "n") {
      exec(http("IncrementLimit").put("/limits/${LimitId}").header("Content-Type", "application/json").body(io.gatling.core.body.ElFileBody("increment-limit.json")).asJSON)
      .pause(1)
    }

  setUp(
    scn.inject(rampUsers(10000) over (120 seconds))
  ).protocols(httpConf)
}


class SeveralRateLimitsSimulation extends ApimanRlsSimulation {
  
  val limitIdFeeder = Array(
    Map("LimitId" -> "Limit-1"),
    Map("LimitId" -> "Limit-2"),
    Map("LimitId" -> "Limit-3"),
    Map("LimitId" -> "Limit-4"),
    Map("LimitId" -> "Limit-5"),
    Map("LimitId" -> "Limit-6"),
    Map("LimitId" -> "Limit-7"),
    Map("LimitId" -> "Limit-8"),
    Map("LimitId" -> "Limit-9"),
    Map("LimitId" -> "Limit-10") ).random
  
  val httpConf = http
    .baseURL(rlsHost)
    .acceptHeader("application/json,application/xml;q=0.9,*/*;q=0.8")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0")

  val scn = scenario("SeveralRateLimitsScenario")
    .feed(limitIdFeeder)
    .exec(_.set("MaxLimitValue", 2000000))
    .exec(http("Root").get("/"))
    .exec(http("CreateLimit").post("/limits/").header("Content-Type", "application/json").body(io.gatling.core.body.ElFileBody("create-limit.json")).asJSON)
    .pause(1)
    .repeat(1000, "n") {
      exec(http("IncrementLimit").put("/limits/${LimitId}").header("Content-Type", "application/json").body(io.gatling.core.body.ElFileBody("increment-limit.json")).asJSON)
      .pause(1)
    }

  setUp(
    scn.inject(rampUsers(10000) over (120 seconds))
  ).protocols(httpConf)
}


class UniqueRateLimitsSimulation extends ApimanRlsSimulation {
  
  val limitIdFeeder = Iterator.continually(Map("LimitId" -> ("Limit-" + Random.alphanumeric.take(30).mkString)))
  
  val httpConf = http
    .baseURL(rlsHost)
    .acceptHeader("application/json,application/xml;q=0.9,*/*;q=0.8")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0")

  val scn = scenario("UniqueRateLimitsScenario")
    .feed(limitIdFeeder)
    .exec(_.set("MaxLimitValue", 50000))
    .exec(http("Root").get("/"))
    .exec(http("CreateLimit").post("/limits/").header("Content-Type", "application/json").body(io.gatling.core.body.ElFileBody("create-limit.json")).asJSON)
    .pause(1)
    .repeat(1000, "n") {
      exec(http("IncrementLimit").put("/limits/${LimitId}").header("Content-Type", "application/json").body(io.gatling.core.body.ElFileBody("increment-limit.json")).asJSON)
      .pause(1)
    }

  setUp(
    scn.inject(rampUsers(10000) over (120 seconds))
  ).protocols(httpConf)
}


class KnockOverSimulation extends ApimanRlsSimulation {
  
  val limitIdFeeder = Iterator.continually(Map("LimitId" -> ("Limit-" + Random.alphanumeric.take(30).mkString)))
  
  val httpConf = http
    .baseURL(rlsHost)
    .acceptHeader("application/json,application/xml;q=0.9,*/*;q=0.8")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0")

  val scn = scenario("KnockOverScenario")
    .feed(limitIdFeeder)
    .exec(_.set("MaxLimitValue", 500000))
    .exec(http("Root").get("/"))
    .exec(http("CreateLimit").post("/limits/").header("Content-Type", "application/json").body(io.gatling.core.body.ElFileBody("create-limit.json")).asJSON)
    .pause(1)
    .repeat(500, "n") {
      exec(http("IncrementLimit").put("/limits/${LimitId}").header("Content-Type", "application/json").body(io.gatling.core.body.ElFileBody("increment-limit.json")).asJSON)
    }

  setUp(
    scn.inject(rampUsers(10000) over (60 seconds))
  ).protocols(httpConf)
}




class SlowRampSimulation extends ApimanRlsSimulation {
  
  val limitIdFeeder = Array(
    Map("LimitId" -> "Limit-0"),
    Map("LimitId" -> "Limit-1"),
    Map("LimitId" -> "Limit-2"),
    Map("LimitId" -> "Limit-3"),
    Map("LimitId" -> "Limit-4"),
    Map("LimitId" -> "Limit-5"),
    Map("LimitId" -> "Limit-6"),
    Map("LimitId" -> "Limit-7"),
    Map("LimitId" -> "Limit-8"),
    Map("LimitId" -> "Limit-9"),
    Map("LimitId" -> "Limit-10"),
    Map("LimitId" -> "Limit-11"),
    Map("LimitId" -> "Limit-12"),
    Map("LimitId" -> "Limit-13"),
    Map("LimitId" -> "Limit-14"),
    Map("LimitId" -> "Limit-15"),
    Map("LimitId" -> "Limit-16"),
    Map("LimitId" -> "Limit-17"),
    Map("LimitId" -> "Limit-18"),
    Map("LimitId" -> "Limit-19"),
    Map("LimitId" -> "Limit-20"),
    Map("LimitId" -> "Limit-21"),
    Map("LimitId" -> "Limit-22"),
    Map("LimitId" -> "Limit-23"),
    Map("LimitId" -> "Limit-24") ).random
  
  val httpConf = http
    .baseURL(rlsHost)
    .acceptHeader("application/json,application/xml;q=0.9,*/*;q=0.8")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0")

  val scn = scenario("SlowRampScenario")
    .feed(limitIdFeeder)
    .exec(_.set("MaxLimitValue", 50000000))
    .exec(http("Root").get("/"))
    .exec(http("CreateLimit").post("/limits/").header("Content-Type", "application/json").body(io.gatling.core.body.ElFileBody("create-limit.json")).asJSON)
    .pause(1)
    .repeat(3000, "n") {
      exec(http("IncrementLimit").put("/limits/${LimitId}").header("Content-Type", "application/json").body(io.gatling.core.body.ElFileBody("increment-limit.json")).asJSON).pause(200 milliseconds)
    }

  setUp(
    scn.inject(rampUsers(10000) over (20 minutes))
  ).protocols(httpConf)
}

