package ahlers.presence.web.client

//import com.raquo.domtypes.generic.builders.SvgAttrBuilder
import com.raquo.laminar.api.Laminar
import com.raquo.laminar.api.L.svg._
//import com.raquo.laminar.builders.SvgBuilders
//import com.raquo.laminar.keys.ReactiveSvgAttr
import com.raquo.laminar.nodes.ReactiveSvgElement
import org.scalajs.dom.svg.SVG

/**
 * @since January 01, 2021
 * @author <a href="mailto:michael@ahlers.consulting">Michael Ahlers</a>
 */
object LogoView { //extends SvgAttrBuilder[ReactiveSvgAttr] with SvgBuilders {

  /**
   * This needs to be part of the DOM to allow styling, but there's currently no means of using the original asset directly with Laminar. In the meantime, use [[Laminar.svg]], converted using [[https://github.com/simerplaha/html-to-scalatags-converter]].
   *
   * @see [[https://gitter.im/Laminar_/Lobby?at=5fef9d9fde608143155838ff]]
   */
  def apply(): ReactiveSvgElement[SVG] =
    svg(
      className := "ahlers-consulting-logo",
      viewBox := "0 0 1259 809",
      style := "fill-rule:evenodd;clip-rule:evenodd;stroke-linejoin:round;stroke-miterlimit:2;",
      g(
        transform := "matrix(1,0,0,1,-270.537,-270.537)",
        g(
          g(
            transform := "matrix(1,0,0,0.833333,0,50)",
            path(d := "M914.131,1212.75C962.207,1130.67 1009.07,1059.34 1055.85,998.195C1113.29,923.1 1169.89,862.982 1225.34,815.034C1269.74,776.638 1313.5,746.198 1356.46,722.242C1405.52,694.895 1453.64,676.246 1500.67,663.694L1503.75,662.887L1502.41,655.499L1499.33,656.306C1451.77,668.546 1403.24,687.386 1353.37,714.09C1309.63,737.513 1264.98,767.193 1219.08,804.249C1161.64,850.624 1102.56,908.788 1041.09,981.048C990.933,1040 939.032,1107.88 885.869,1187.25L875.24,1204.2L903.503,1229.71L914.131,1212.75Z")
          ),
          g(
            transform := "matrix(1,0,0,0.833333,600,50)",
            path(d := "M606.807,301.266C605.987,308.112 605.145,314.942 604.27,321.76C585.295,488.861 610.84,647.594 662.023,790.04C719.378,949.666 808.802,1088.73 902.187,1197.28L904.456,1199.9L900.081,1205.35L897.813,1202.72C803.385,1093.93 713.469,954.056 654.531,793.964C614.714,685.806 589.004,568.38 586.015,444.629C565.976,562.078 539.343,673.855 507.457,777.891C455.776,946.511 390.03,1094.75 313.83,1213.22L302.812,1229.82L275.152,1203.37L286.17,1186.78C363.624,1073.72 432.001,931.346 486.157,768.105C533.515,625.358 569.888,466.827 593.193,298.734L606.807,301.266Z")
          ),
          g(
            transform := "matrix(1,0,0,0.666667,300,250)",
            path(d := "M606.258,301.819C604.215,318.88 602.032,335.844 599.706,352.717C577.968,528.993 567.777,694.714 569.208,841.486C570.556,979.803 582.221,1101.18 602.999,1198.55L603.966,1203.05L597.968,1205.95L597.001,1201.45C575.774,1103.42 564.123,981.12 561.873,841.699C560.265,742.05 563.464,633.612 571.877,518.993C553.102,613.944 530.037,704.761 503.449,790.368C452.1,955.699 387.369,1101.48 312.529,1218.72L300.052,1237.51L274.994,1200.08L287.471,1181.28C363.147,1071.68 430.188,934.066 483.639,775.919C532.526,631.277 569.956,469.74 593.742,298.181L606.258,301.819Z")
          ),
          g(
            transform := "matrix(1,0,0,0.833333,0,50)",
            path(d := "M606.428,301.196C604.343,318.631 602.112,335.967 599.732,353.177C578.048,529.084 567.843,694.462 569.222,840.96C570.527,979.703 582.222,1101.44 603.051,1199.06L603.838,1202.72L597.736,1204.61L596.949,1200.94C575.717,1102.89 564.088,980.56 561.845,841.095C560.25,741.932 563.405,634.081 571.726,520.081C553.858,610.058 532.125,696.282 507.187,777.767C455.576,946.404 389.954,1094.69 313.83,1213.22L302.812,1229.82L275.152,1203.37L286.17,1186.78C363.7,1073.78 432.2,931.46 486.427,768.229C533.849,625.483 570.23,466.921 593.572,298.804L606.428,301.196Z")
          )
        )
      )
    )

  //lazy val href: ReactiveSvgAttr[String] = stringSvgAttr("href")
  //
  //svg(
  //  width := "100%",
  //  height := "100%",
  //  viewBox := "0 0 1259 809",
  //  use(href := Asset.versioned("ahlers/presence/web/client/Ahlers Consulting (dark).svg#foobear").absoluteUrl)
  //)

}
