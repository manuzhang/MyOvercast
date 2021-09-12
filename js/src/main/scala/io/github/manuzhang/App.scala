package io.github.manuzhang

import scala.scalajs.js
import slinky.core._
import slinky.core.annotations.react
import slinky.web.html._
import typings.antd.components._
import typings.antd.tableInterfaceMod.{ColumnFilterItem, ColumnType}

import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.Object.entries

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
    val myOvercast = getJsonMap(MyOvercast)
    val items = getJsonMap(myOvercast("episodes").asInstanceOf[js.Object]).map { case (_, obj) =>
      val item = getJsonMap(obj.asInstanceOf[js.Object])
      new TableItem(get[String](item, "podcast"), get[String](item, "podcastUrl"),
        get[String](item, "episode"), get[String](item, "episodeUrl"),
        get[Boolean](item, "starred"), get[String](item, "listenDate"))
    }.toList.sortWith((a, b) => { a.listenDate > b.listenDate })

    div(className := "App")(
      header(className := "App-header")(
        h1(className := "App-title")("My Overcast Listening History")
      ),
      renderStats(get[Int](myOvercast, "playedNum"), get[Int](myOvercast, "inProgressNum"), 
        get[Int](myOvercast, "starredNum")),
      renderTable(items: List[TableItem])
    )
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
    section(
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
    )
  }

  private def getJsonMap(obj: js.Object): Map[String, Any] = {
    entries(obj).map(kv => kv._1 -> kv._2).toMap
  }
  
  private def get[T](map: Map[String, Any], key: String): T = {
    map(key).asInstanceOf[T]
  }
}
