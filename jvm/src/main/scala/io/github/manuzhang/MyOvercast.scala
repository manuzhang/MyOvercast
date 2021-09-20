package io.github.manuzhang

import io.github.manuzhang.MyOvercast.PodcastEpisode

import scala.collection.mutable.ArrayBuffer
import scala.xml.XML
import upickle.default._

case class MyOvercast(playedNum: Int, inProgressNum: Int, starredNum: Int, episodes: List[PodcastEpisode])

object MyOvercast extends App {

  implicit val overcastRw: ReadWriter[MyOvercast] = macroRW[MyOvercast]

  case class PodcastEpisode(podcast: String, podcastUrl: String, episode: String, episodeUrl: String,
    starred: Boolean, listenDate: String)
  implicit val episodeRw: ReadWriter[PodcastEpisode] = macroRW[PodcastEpisode]

  case class TopPodcasts(podcasts: List[(String, Int)])
  implicit val topRw: ReadWriter[TopPodcasts] = macroRW[TopPodcasts]


  val url = "https://overcast.fm"
  val email = System.getenv("EMAIL")
  val password = System.getenv("PASSWORD")

  val r = requests.post(s"$url/login",
    data = Map("email" -> email, "password" -> password), 
    check = false)
  val resp = requests.get(s"$url/account/export_opml/extended",
    headers = Map("Cookie" -> r.cookies.values.head.toString)
  ).text

  val episodes = ArrayBuffer.empty[PodcastEpisode]
  var subscribed = 0
  var playedNum = 0
  var inProgressNum = 0
  var starredNum = 0
  (XML.loadString(resp) \\ "outline").foreach {
    outline =>
      if ((outline \@ "type") == "rss") {
        subscribed += 1
        val podcast = outline \@ "title"
        val podcastUrl = outline \@ "htmlUrl"
        outline.nonEmptyChildren.foreach { node =>
          if (node \@ "type" == "podcast-episode") {
            val played = node \@ "played" == "1"
            if (played) {
              playedNum += 1
            }
            val inProgress = (node \@ "progress").nonEmpty
            if (inProgress) {
              inProgressNum += 1
            }
            val starred = (node \@ "userRecommendedDate").nonEmpty
            if (starred) {
              starredNum += 1
            }
            val episode = node \@ "title"
            val episodeUrl = node \@ "overcastUrl"
            val listenDate = node \@ "userUpdatedDate"
            episodes.append(PodcastEpisode(podcast, podcastUrl, episode, episodeUrl, starred, listenDate))
          }
        }
      }

      val top10  = episodes.groupBy(_.podcast).map { case (podcast, episodes) =>
        podcast -> episodes.size
      }.toList.sortWith { (a, b) => a._2 > b._2 }.take(10)
      val path = os.pwd / "js" / "src" / "main" / "resources"
      os.write.over(path / "myovercast.json",
        write(MyOvercast(playedNum, inProgressNum, starredNum, episodes.toList), indent = 2))
      os.write.over(path / "myovercast-top10.json",
        write(TopPodcasts(top10), indent = 2))
  }
}
