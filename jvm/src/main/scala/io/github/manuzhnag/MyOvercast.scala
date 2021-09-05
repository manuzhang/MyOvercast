package io.github.manuzhnag

import java.time.format.DateTimeFormatter
import java.time.{ZonedDateTime, ZoneId}
import scala.xml.XML

object MyOvercast extends App {

  val url = "https://overcast.fm"
  val email = System.getenv("EMAIL")
  val password = System.getenv("PASSWORD")

  val r = requests.post(s"$url/login",
    data = Map("email" -> email, "password" -> password), 
    check = false)
  val resp = requests.get(s"$url/account/export_opml/extended",
    headers = Map("Cookie" -> r.cookies.values.head.toString)
  ).text
  parse(resp, 7)

  def parse(opml: String, days: Int): Unit = {
    (XML.loadString(opml) \\ "outline").foreach {
      outline =>
        if ((outline \@ "type") == "rss") {
          val podcast = outline \@ "text"
          outline.nonEmptyChildren.foreach { node =>
            if (node \@ "played" == "1") {
              val title = node \@ "title"
              val url = node \@ "overcastUrl"
              val source = node \@ "enclosureUrl"
              val listenStr = node \@ "userUpdatedDate"
              val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
              val listenTime = ZonedDateTime.from(pattern.parse(listenStr))
              val (start, end) = getStartEndTime(days)
              if (listenTime.isAfter(start) && listenTime.isBefore(end)) {
                println(s"$podcast,$title,$url")
              }
            }
          }
        }
    }
  }
  
  def getStartEndTime(days: Int): (ZonedDateTime, ZonedDateTime) = {
    val now = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"))
    
    def startOfDay(dt: ZonedDateTime): ZonedDateTime = {
      dt.withHour(0).withMinute(0).withSecond(0)
    }

    (startOfDay(now.minusDays(days)), startOfDay(now))
  }
}
