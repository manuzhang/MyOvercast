package io.github.manuzhang

import scala.scalajs.js
import slinky.core._
import slinky.core.annotations.react
import slinky.core.facade.Hooks
import slinky.web.html._
import typings.antd.components._
import typings.antd.libTableInterfaceMod.ColumnType
import typings.react.mod.CSSProperties

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.Object.entries
import scala.util.{Failure, Success, Try}

@JSImport("../../classes/myovercast.json", JSImport.Default)
@js.native
object MyOvercast extends js.Object

@JSImport("antd/dist/antd.css", JSImport.Default)
@js.native
object CSS extends js.Any

@react object App {
  type Props = Unit

  private val css = CSS

  val component = FunctionalComponent[Props] { _ =>
    val (days, setDays) = Hooks.useState(7)
    val (episodes, setEpisodes) = Hooks.useState[List[TableItem]](Nil)
    val (items, setItems) = Hooks.useState[List[TableItem]](Nil)
    val (overcast, setOvercast) = Hooks.useState[Map[String, Any]](Map.empty[String, Any])
 
    Hooks.useEffect(() => {
      val myOvercast = getJsonMap(MyOvercast)
      setOvercast(myOvercast)
      val eps = getJsonMap(myOvercast("episodes").asInstanceOf[js.Object]).map { case (_, obj) =>
        val item = getJsonMap(obj.asInstanceOf[js.Object])
        new TableItem(get[String](item, "podcast", ""), get[String](item, "podcastUrl", ""),
          get[String](item, "episode", ""), get[String](item, "episodeUrl", ""),
          get[Boolean](item, "starred", false), get[String](item, "listenDate", ""))
      }.toList
        .sortWith((a, b) => { a.listenDate > b.listenDate })
      setEpisodes(eps)
      setItems(getLastItems(eps, days))
    }, Seq())

    div(className := "App")(
      Layout(
        Header(
          h1(className := "App-title")("My Overcast Listening History")
        ).style(CSSProperties().setBackgroundColor("white")),
        Content(
          renderStats(get[Int](overcast, "playedNum", 0),
            get[Int](overcast, "inProgressNum", 0),
            get[Int](overcast, "starredNum", 0)),
          Input.addonBefore("Last")
            .value(days)
            .addonAfter("Days")
            .onChange(e => {
              Try {
                e.target_ChangeEvent.value.toInt
              } match {
                case Success(d) =>
                  setDays(d)
                  setItems(getLastItems(episodes, d))
                case Failure(e) => //
              }
            }),
          renderTable(items)
        ),
        Footer()
      )
    )
  }

  private def getLastItems(episodes: List[TableItem], days: Int): List[TableItem] = {
    episodes.takeWhile { item =>
      val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
      val listenTime = ZonedDateTime.from(formatter.parse(item.listenDate))
      val from = ZonedDateTime.now(listenTime.getZone).minusDays(days)
      listenTime.isAfter(from)
    } 
  }

  class TableItem(val podcast: String, val podcastUrl: String,
    val episode: String, val episodeUrl: String, val starred: Boolean,
    val listenDate: String) extends js.Object

  def renderStats(playedNum: Int, inProgressNum: Int, starredNum: Int) = {
    section(
      Row(
        Col.span(6).offset(1)(Statistic.title("Played").value(playedNum)),
        Col.span(6).offset(1)(Statistic.title("InProgress").value(inProgressNum)),
        Col.span(6).offset(1)(Statistic.title("Starred").value(starredNum))
      )
    )
  }

  def renderTable(items: List[TableItem]) = {
    Table[TableItem]
      .bordered(true)
      .dataSourceVarargs(items: _*)
      .columnsVarargs(
        ColumnType[TableItem]
          .setTitle("Podcast")
          .setRender((_, tableItem, _) => a(href := tableItem.podcastUrl)(tableItem.podcast)),
        ColumnType[TableItem]
          .setTitle("Episode")
          .setRender((_, tableItem, _) => a(href := tableItem.episodeUrl)(tableItem.episode)),
        ColumnType[TableItem]
          .setTitle("Starred")
          .setRender((_, tableItem, _) => Rate.count(1).value(if (tableItem.starred) 1 else 0).build)
      )
  }

  private def getJsonMap(obj: js.Object): Map[String, Any] = {
    entries(obj).map(kv => kv._1 -> kv._2).toMap
  }

  private def get[T](map: Map[String, Any], key: String, default: T): T = {
    get(map, key).getOrElse(default)
  }

  private def get[T](map: Map[String, Any], key: String): Option[T] = {
    map.get(key).map(_.asInstanceOf[T])
  }
}
