package ahlers.presence.web.server

import java.time.Clock

import com.softwaremill.macwire._
import controllers.AssetsComponents
import org.webjars.play.{ RequireJS, WebJarComponents }
import play.api.ApplicationLoader.Context
import play.api.BuiltInComponentsFromContext
import play.filters.HttpFiltersComponents
import play.filters.csrf.CSRFFilter
import play.filters.logging.AccessLogFilter
import router.Routes

/**
 * @author <a href="mailto:michael@ahlers.consulting">Michael Ahlers</a>
 * @since October 06, 2020
 */
@Module
class WebServerModule(
  context: Context)
  extends BuiltInComponentsFromContext(context)
    with AssetsComponents
    with HttpFiltersComponents
    with WebJarComponents {

  implicit val clock = Clock.systemUTC()

  override val httpFilters = {
    val accessLogFilter = wire[AccessLogFilter]

    super.httpFilters
      .filterNot(_.getClass == classOf[CSRFFilter]) :+
      accessLogFilter
  }

  val uiController = wire[WebClientController]

  val router: Routes = {
    val webJarRoutes = {
      val requireJs = wire[RequireJS]
      wire[webjars.Routes]
    }

    val prefix: String = "/"

    wire[Routes]
  }

  //AkkaManagement(actorSystem).start()

}
