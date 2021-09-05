package io.github.manuzhang

import slinky.core._
import slinky.core.annotations.react
import slinky.web.html._

//@JSImport("resources/App.css", JSImport.Default)
//@js.native
//object AppCSS extends js.Object
//
//@JSImport("resources/logo.svg", JSImport.Default)
//@js.native
//object ReactLogo extends js.Object

@react object App {
  type Props = Unit

  // private val css = AppCSS

  val component = FunctionalComponent[Props] { _ =>
    render()
  }

  def render() = {
    div(className := "App")(
      header(className := "App-header")(
        // img(src := ReactLogo.asInstanceOf[String], className := "App-logo", alt := "logo"),
        h1(className := "App-title")("Welcome to React (with Scala.js!)")
      ),
      p(className := "App-intro")(
        "To get started, edit ", code("App.scala"), " and save to reload."
      )
    )
  }
}
