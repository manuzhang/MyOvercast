package io.github.manuzhang

import scala.scalajs.{js, LinkingInfo}
import slinky.web.ReactDOM
import slinky.hot
import org.scalajs.dom

import scala.scalajs.js.annotation.JSImport

@JSImport("./index.css", JSImport.Default)
@js.native
object IndexCSS extends js.Object

object Main {
  val css = IndexCSS

  def main(args: Array[String]): Unit = {
    if (LinkingInfo.developmentMode) {
      hot.initialize()
    }

    ReactDOM.render(App.component(()), dom.document.getElementById("container"))
  }
}
