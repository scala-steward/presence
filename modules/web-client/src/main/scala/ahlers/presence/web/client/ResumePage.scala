package ahlers.presence.web.client

import ahlers.presence.web.client.resume._
import cats.instances.option._
import cats.syntax.apply._
import cats.syntax.option._
import com.raquo.airstream.core.Observer
import com.raquo.airstream.signal.Var
import com.raquo.laminar.api.L._
import com.raquo.laminar.nodes.{ ReactiveElement, ReactiveSvgElement }
import d3v4._
import d3v4.d3.ZoomBehavior
import d3v4.d3force._
import d3v4.d3zoom.{ Transform, ZoomEvent }
import org.scalajs.dom
import org.scalajs.dom.ext.KeyCode

import scala.scalajs.js
import scala.scalajs.js.JSConverters._

/**
 * @since December 22, 2020
 * @author <a href="mailto:michael@ahlers.consulting">Michael Ahlers</a>
 */
object ResumePage {

  val zb: ZoomBehavior[dom.EventTarget] = d3.zoom()

  case class onZoom(behavior: ZoomBehavior[dom.EventTarget]) {

    @inline def -->(observer: Observer[ZoomEvent]): Binder[ReactiveElement.Base] =
      ReactiveElement.bindSubscription(_) { context =>
        val selection = d3.select(context.thisNode.ref)

        behavior
          .scaleExtent(Seq(1d, 10d).toJSArray)
          .on("zoom", () => observer.onNext(d3.event))
          .apply(selection)

        new Subscription(
          context.owner,
          cleanup = () =>
            behavior
              .on("zoom", null)
              .apply(selection))
      }

    // And so on…

    //@inline def -->(onNext: ZoomEvent => Unit): Binder[ReactiveElement.Base] =
    //  -->(Observer(onNext))

    //@inline def -->(eventBus: EventBus[ZoomEvent]): Binder[ReactiveElement.Base] =
    //  -->(eventBus.writer)

    //@inline def -->(targetVar: Var[ZoomEvent]): Binder[ReactiveElement.Base] =
    //  -->(targetVar.writer)

  }

  def apply(): HtmlElement = {
    val nodeRadiusVar: Var[Double] = Var(20d)
    val $nodeRadius = nodeRadiusVar.signal

    val centeringXVar = Var(0)
    val $centerX = centeringXVar.signal

    val centeringYVar = Var(0)
    val $centerY = centeringYVar.signal

    val focusedNodeVar: Var[Option[ExperienceNodeUi]] = Var(none)
    val $focusedNode: Signal[Option[ExperienceNodeUi]] = focusedNodeVar.signal

    val diagram = {
      import svg._

      val transformDiagramVar: Var[Option[Transform]] = Var(none)

      val focusedNodeVar: Var[Option[ExperienceNodeUi]] = Var(none)
      val $focusedNode: Signal[Option[ExperienceNodeUi]] = focusedNodeVar.signal

      $focusedNode.foreach(println(_))(unsafeWindowOwner)

      svg(
        className := "flex-fill bg-dark",
        onZoom(zb) --> transformDiagramVar.writer.contramap[ZoomEvent](_.transform.some),
        onClick.mapToValue(none) --> focusedNodeVar.writer,
        g(
          transform <-- transformDiagramVar.signal.map(_.fold("")(_.toString())),
          //experiences.links.map(link =>
          //  link.render(
          //    $focusedNode)),
          experiences.nodes.map(node =>
            node.render(
              $nodeRadius,
              //$centerX,
              //$centerY,
              onClick.map(_.stopPropagation()).mapToValue(node.some.filterNot(_.experience == ExperienceBrief.Blank)) --> focusedNodeVar.writer
            ))
        ),
        inContext { thisNode =>
          val $width =
            windowEvents
              .onResize
              .mapTo(thisNode.ref.clientWidth)
          //.toSignal(thisNode.ref.clientWidth)

          val $height =
            windowEvents
              .onResize
              .mapTo(thisNode.ref.clientHeight)
          //.toSignal(thisNode.ref.clientHeight)

          val $focusTransform =
            for {
              nodeRadius <- $nodeRadius
              //centerX <- $centerX
              //centerY <- $centerY
              focusedNodeF <- $focusedNode
            } yield {
              val centerX = thisNode.ref.clientWidth / 2
              val centerY = thisNode.ref.clientHeight / 2

              focusedNodeF match {
                case None =>
                  d3.zoomIdentity
                    .translate(centerX, centerY)
                case Some(focusedNode) =>
                  println((centerX, centerY))
                  val x = focusedNode.xFor(nodeRadius) //, centerX)
                  val y = focusedNode.yFor(nodeRadius) //, centerY)
                  d3.zoomIdentity
                    .translate(centerX, centerY)
                    .scale(5)
                    .translate(-x, -y)
              }
            }

          val $resizeTransform =
            for {
              _ <- $width
              _ <- $height
            } yield {
              val centerX = thisNode.ref.clientWidth / 2
              val centerY = thisNode.ref.clientHeight / 2
              d3.zoomIdentity
                .translate(centerX, centerY)
            }

          $focusTransform --> (zb.transform(d3.select(thisNode.ref).transition().duration(1500d), _)) ::
            $resizeTransform --> (zb.transform(d3.select(thisNode.ref), _)) ::
            $width.map(_ / 2) --> centeringXVar.writer ::
            $height.map(_ / 2) --> centeringYVar.writer ::
            Nil
        },
        onMountCallback { context =>
          import context.thisNode
          val centerX = thisNode.ref.clientWidth / 2
          val centerY = thisNode.ref.clientHeight / 2
          val zoomIdentity = d3.zoomIdentity.translate(centerX, centerY)
          zb.transform(d3.select(thisNode.ref), zoomIdentity.scale(0.75d))
          zb.transform(d3.select(thisNode.ref).transition().duration(1500d), zoomIdentity.scale(1d))
          centeringXVar.set(centerX)
          centeringYVar.set(centerY)
        }
      )
    }

    val onEnterPress = onKeyPress.filter(_.keyCode == KeyCode.Enter)

    article(
      className := "d-flex flex-column h-100",
      diagram,
      div(
        className := "container-fluid",
        div(
          className := "row",
          div(
            className := "col-12",
            span("Node Radius: "),
            input(
              value <-- $nodeRadius.map(_.toString()),
              inContext(el => onEnterPress.mapTo(el.ref.value).map(_.toDouble) --> nodeRadiusVar.writer))
          )
        )
      )
    )

  }

  //implicit class ExperienceLinkSyntax(private val link: ExperienceLinkUi) extends AnyVal {
  //
  //  @inline def render(
  //    $focusedNode: Signal[Option[ExperienceNodeUi]]
  //  ) = {
  //    import svg._
  //
  //    val $display =
  //      $focusedNode.map {
  //        case Some(focusedNode) if experiences.adjacentLinks(focusedNode).contains(link) => "inline"
  //        case _ => "none"
  //      }
  //
  //    line(
  //      display <-- $display,
  //      stroke := "black",
  //      x1 <-- link.source.$x.map(_.fold("")(_.toString)),
  //      y1 <-- link.source.$y.map(_.fold("")(_.toString)),
  //      x2 <-- link.target.$x.map(_.fold("")(_.toString)),
  //      y2 <-- link.target.$y.map(_.fold("")(_.toString))
  //    )
  //  }
  //
  //}

  implicit class ExperienceNodeSyntax(private val node: ExperienceNodeUi) extends AnyVal {

    def xFor(nodeRadius: Double): Double = { //, centerX: Double): Double = {
      val theta = Math.PI * (3 - Math.sqrt(5))
      val i = node.index
      val step = nodeRadius * 2d
      val radius = step * Math.sqrt(i + 0.25d)
      val a = theta * (i + 0.25d)
      //centerX + radius * Math.cos(a)
      radius * Math.cos(a)
    }

    def yFor(nodeRadius: Double): Double = { //, centerY: Double): Double = {
      val theta = Math.PI * (3 - Math.sqrt(5))
      val i = node.index
      val step = nodeRadius * 2d
      val radius = step * Math.sqrt(i + 0.25d)
      val a = theta * (i + 0.25d)
      //centerY + radius * Math.sin(a)
      radius * Math.sin(a)
    }

    @inline def render(
      $nodeRadius: Signal[Double],
      //$centerX: Signal[Int],
      //$centerY: Signal[Int],
      //$focusedNode: Signal[Option[ExperienceNodeUi]],
      modifiers: Modifier[ReactiveSvgElement[dom.raw.SVGElement]]*
    ) = {
      import svg._

      val $x =
        for {
          nodeRadius <- $nodeRadius
          //centerX <- $centerX
        } yield xFor(nodeRadius) //, centerX)

      val $y =
        for {
          nodeRadius <- $nodeRadius
          //centerY <- $centerY
        } yield yFor(nodeRadius) //, centerY)

      //val transformNodeVar: Var[Transform] = Var(d3.zoomIdentity)
      g(
        //transform <-- transformNodeVar.signal.map(_.toString()),
        node.experience match {
          case experience: ExperienceBrief.Skill =>
            experience.logo
              .map(logo =>
                image(
                  x <-- $nodeRadius.flatMap(nodeRadius => $x.map(_ - nodeRadius)).map(_.toString),
                  y <-- $nodeRadius.flatMap(nodeRadius => $y.map(_ - nodeRadius)).map(_.toString),
                  width <-- $nodeRadius.map(_ * 2d).map(_.toString),
                  height <-- $nodeRadius.map(_ * 2d).map(_.toString),
                  xlinkHref := logo
                ))
              .getOrElse(circle(
                r <-- $nodeRadius.map(_.toString),
                cx <-- $x.map(_.toString()),
                cy <-- $y.map(_.toString()),
                fill := "green"
              ))
          case experience: ExperienceBrief.Employment =>
            experience.logo
              .map(logo =>
                image(
                  x <-- $nodeRadius.flatMap(nodeRadius => $x.map(_ - nodeRadius)).map(_.toString),
                  y <-- $nodeRadius.flatMap(nodeRadius => $y.map(_ - nodeRadius)).map(_.toString),
                  width <-- $nodeRadius.map(_ * 2d).map(_.toString),
                  height <-- $nodeRadius.map(_ * 2d).map(_.toString),
                  xlinkHref := logo
                ))
              .getOrElse(circle(
                r <-- $nodeRadius.map(_.toString),
                cx <-- $x.map(_.toString()),
                cy <-- $y.map(_.toString()),
                fill := "blue"
              ))
          case _ =>
            circle(
              //r <-- node.$radius.map(_.fold("")(_.toString)),
              r <-- $nodeRadius.map(_.toString),
              cx <-- $x.map(_.toString()),
              cy <-- $y.map(_.toString()),
              fill := "#333"
            )
        },
        text(
          x <-- $x.map(_.toString()),
          y <-- $y.map(_.toString()),
          style := "15px sans-serif",
          (node.experience match {
            case ExperienceBrief.Blank => ""
            case experience: ExperienceBrief.Skill => experience.logo.fold(experience.id.toText)(_ => "")
            case experience: ExperienceBrief.Employment => experience.logo.fold(experience.id.toText)(_ => "")
          })
        ),
        //inContext { context =>
        //  $radius.map(_.some) --> node.radiusVar.writer ::
        //    $fx --> node.fxVar.writer ::
        //    $fy --> node.fyVar.writer ::
        //    Nil
        //},
        //onMountCallback { context =>
        //  import context.thisNode
        //
        //  d3.zoom()
        //    .on("zoom", () => transformNodeVar.set(d3.event.transform))
        //    .scaleBy(d3.select(thisNode.ref), 1.1d)
        //
        //},
        //onClick.mapToValue(node.some) --> focusedExperienceVar
        modifiers
      )
    }

  }

}
