package example.akkawschat

import japgolly.scalajs.react.ReactComponentB

/**
 * Created by magnus on 09/06/16.
 */
object Components {

  import japgolly.scalajs.react.vdom.prefix_<^._

  val a = <.ol(
    ^.id := "my-list",
    ^.lang := "en",
    ^.margin := "8px",
    <.li("Item 1"),
    <.li("Item 2"))

  val Hello =
    ReactComponentB[String]("Hello")
      .render_P(name ⇒ <.div("Hello there ", name))
      .build

  // Usage:

}
