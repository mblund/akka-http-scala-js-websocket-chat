package example.akkawschat

import japgolly.scalajs.react.extra.OnUnmount
import japgolly.scalajs.react.{ BackendScope, Callback, ReactComponentB }
import rx.{ Ctx, Var, Rx, Obs }

import scala.scalajs.js
import scala.util.Random

object Components2 {

  import japgolly.scalajs.react.vdom.prefix_<^._

  object Table {
    val random = Random

    case class Props(widht: Int, table: Vector[Var[Boolean]])

    class Backend($: BackendScope[Props, Unit]) {
      //var i = 0
      def start: Callback = Callback {
        js.timers.setInterval(1) {
          0.to(899).foreach { i ⇒
            $.props.map { p ⇒
              val x = p.table(i)
              x() = random.nextBoolean()
            }.runNow()
          }

        }
      }

      def clear: Callback = Callback.info("Unmount")
      def render(props: Props) =
        <.table(^.cellSpacing := "0",
          <.tbody(
            props.table.grouped(props.widht).map(column ⇒
              <.tr(column.zipWithIndex.map(value ⇒ Cell.Component.withKey(value._2)(Cell.Props(value._1)))))))
    }

    val Component = ReactComponentB[Props]("Cell")
      .stateless.renderBackend[Backend]
      .componentDidMount(_.backend.start)
      .componentWillUnmount(_.backend.clear)
      .build

  }

  object Cell {

    case class Props(target: Rx[Boolean])
    type State = Boolean

    class Backend($: BackendScope[Props, State]) extends OnUnmount {
      def render(state: State) =
        <.td(^.className := (if (state) "red" else "green"))
    }

    val Component = ReactComponentB[Props]("Cell")
      .initialState_P(props ⇒ props.target.now)
      .renderBackend[Backend]
      .componentDidMount { scope ⇒

        val obs = scope.props.target.foreach { x ⇒
          scope.modState(_ ⇒ x).runNow()
        }

        scope.backend.onUnmount(Callback())
      }
      .build

  }
}
