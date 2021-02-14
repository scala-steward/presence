package ahlers.presence.web.client.resume

import cats.syntax.option._
import ahlers.presence.web.client.UiState
import ahlers.presence.web.client.UiState.{ FocusedResumePage, UnfocusedResumePage }
import com.raquo.domtypes.generic.Modifier
import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.ReactiveSvgElement
import org.scalajs.dom
import org.scalajs.dom.svg.G

/**
 * @since January 31, 2021
 * @author <a href="mailto:michael@ahlers.consulting">Michael Ahlers</a>
 */
object ExperienceNodeView {

  def render(
    nodeState: ExperienceNodeState,
    $focusedNodeState: Signal[Option[ExperienceNodeState]]
  ): ReactiveSvgElement[G] = {
    import svg._

    val $isRevealed: Signal[Boolean] =
      EventStream
        .fromValue(true, emitOnce = true)
        .delay(10 * nodeState.index.toInt)
        .toSignal(false)

    val onClickEnterFocus: Modifier[ReactiveSvgElement[G]] =
      onClick
        .stopPropagation
        .mapToValue(nodeState.id
          .map(FocusedResumePage(_))
          .getOrElse(UnfocusedResumePage)) --> (UiState.router.pushState(_))

    /** We could be more clever, but this is easy to understand. */
    val $classNames: Signal[Map[String, Boolean]] =
      $isRevealed
        .combineWith($focusedNodeState)
        .map { case (isRevealed, focusedNodeState) =>
          val isFocused = focusedNodeState.contains(nodeState)
          val isNotFocused = !focusedNodeState.forall(_ == nodeState)

          Map(
            "revealed" -> isRevealed,
            "focused" -> isFocused,
            "not-focused" -> isNotFocused)
        }

    g(
      className := Seq("experience-node-view", nodeState.kind),
      className <-- $classNames,
      style := "--delay: %dms".format(nodeState.index.toInt * 10),
      nodeState.logo match {
        case None =>
          circle(
            cx := nodeState.cx.toString,
            cy := nodeState.cy.toString,
            r := nodeState.radius.toString,
            fill := "#292929"
          )
        case Some(logo: String) =>
          image(
            xlinkHref := logo,
            x := nodeState.x.toString,
            y := nodeState.y.toString,
            width := nodeState.width.toString,
            height := nodeState.height.toString)
      },
      onClickEnterFocus
    )
  }

}
