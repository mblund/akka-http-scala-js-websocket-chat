package example.akkawschat

import japgolly.scalajs.react.{ Callback, BackendScope, ReactComponentB }

import scala.scalajs.js
import scala.util.Random

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

  case class State(counter: Int, table: Table)

  class Backend($: BackendScope[Table, State]) {
    val random = Random

    var interval: js.UndefOr[js.timers.SetIntervalHandle] =
      js.undefined

    def tick =
      $.modState(s ⇒ State((s.counter + 1) % (s.table.colCount * s.table.rowCount), s.table.update(s.counter)))

    def start = Callback {
      interval = js.timers.setInterval(1)(tick.runNow())
    }

    def clear = Callback {
      interval foreach js.timers.clearInterval
      interval = js.undefined
    }

    def render(state: State) =
      <.table(^.cellSpacing := "0",
        <.tbody(
          state.table.rows.map(column ⇒ <.tr(column.map(value ⇒ <.td(^.className := (if (value) "red" else "green"))())))))
  }

  val TableComponent = ReactComponentB[Table]("Table")
    .initialState_P(p ⇒ State(0, p))
    .renderBackend[Backend]
    .componentDidMount(_.backend.start)
    .componentWillUnmount(_.backend.clear)
    .build

  object Table {
    def apply(rowCount: Int, colCount: Int): Table =
      new Table(rowCount, colCount, data = 0.to(rowCount * colCount - 1).map(_ ⇒ false).toVector)
  }

  class Table(val rowCount: Int, val colCount: Int, data: Vector[Boolean]) {

    def rows = data.grouped(rowCount)

    def update(row: Int, col: Int, value: Boolean): Table =
      new Table(rowCount, colCount, data = data.updated(row * colCount + col, value))

    def update(counter: Int): Table =
      new Table(rowCount, colCount, data = data.updated(counter, !data(counter)))
  }

}
