package by.ssrlab.common_ui.common.ui.map

import androidx.core.content.ContextCompat
import by.ssrlab.common_ui.R
import com.mapbox.maps.extension.style.expressions.generated.Expression
import com.mapbox.maps.plugin.locationcomponent.LocationComponentConstants
import com.mapbox.navigation.core.directions.session.RoutesObserver
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineOptions
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineColorResources
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineResources
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineScaleValue

fun buildScaleExpression(scalingValues: List<RouteLineScaleValue>): Expression {
    val expressionBuilder = Expression.ExpressionBuilder("interpolate")
    expressionBuilder.addArgument(Expression.exponential { literal(1.5) })
    expressionBuilder.zoom()
    scalingValues.forEach { routeLineScaleValue ->
        expressionBuilder.stop {
            this.literal(routeLineScaleValue.scaleStop.toDouble())
            product {
                literal(routeLineScaleValue.scaleMultiplier.toDouble())
                literal(routeLineScaleValue.scale.toDouble())
            }
        }
    }
    return expressionBuilder.build()
}

fun setRouteLineResources(activity: MapActivity): RouteLineResources {
    return RouteLineResources.Builder()
        .routeLineColorResources(
            RouteLineColorResources.Builder()
                .routeDefaultColor(ContextCompat.getColor(activity, R.color.map_red_secondary))
                .routeCasingColor(ContextCompat.getColor(activity, R.color.map_red_secondary))
                .routeClosureColor(ContextCompat.getColor(activity, R.color.map_red_secondary))
                .routeHeavyCongestionColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.map_red_secondary
                    )
                )
                .routeLowCongestionColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.map_red_secondary
                    )
                )
                .routeModerateCongestionColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.map_red_secondary
                    )
                )
                .routeSevereCongestionColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.map_red_secondary
                    )
                )
                .routeUnknownCongestionColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.map_red_secondary
                    )
                )
                .alternativeRouteCasingColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.map_red_secondary
                    )
                )
                .alternativeRouteClosureColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.map_red_secondary
                    )
                )
                .alternativeRouteHeavyCongestionColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.map_red_secondary
                    )
                )
                .alternativeRouteLowCongestionColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.map_red_secondary
                    )
                )
                .alternativeRouteModerateCongestionColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.map_red_secondary
                    )
                )
                .alternativeRouteSevereCongestionColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.map_red_secondary
                    )
                )
                .alternativeRouteUnknownCongestionColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.map_red_secondary
                    )
                )
                .alternativeRouteDefaultColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.map_red_secondary
                    )
                )
                .build()
        )
        .routeLineScaleExpression(
            buildScaleExpression(
                listOf(
                    RouteLineScaleValue(3f, 2f, 1f),
                    RouteLineScaleValue(5f, 3f, 1f),
                    RouteLineScaleValue(6f, 4f, 1f),
                    RouteLineScaleValue(7f, 5f, 1f),
                    RouteLineScaleValue(8f, 6f, 1f),
                    RouteLineScaleValue(9f, 7f, 1f)
                )
            )
        )
        .build()
}

fun setOptions(
    activity: MapActivity,
    routeLineResources: RouteLineResources
): MapboxRouteLineOptions {
    return MapboxRouteLineOptions.Builder(activity)
        .withVanishingRouteLineEnabled(true)
        .withRouteLineResources(routeLineResources)
        .withRouteLineBelowLayerId(LocationComponentConstants.LOCATION_INDICATOR_LAYER)
        .build()
}

fun setRoutesApi(activity: MapActivity, options: MapboxRouteLineOptions): MapboxRouteLineApi {
    val routeLineApi = MapboxRouteLineApi(options)
    RoutesObserver { result ->
        val navigationRoutes = result.navigationRoutes
        routeLineApi.setNavigationRoutes(navigationRoutes) { value ->
            activity.mapboxMap.getStyle()?.apply {
                MapboxRouteLineView(options).renderRouteDrawData(this, value)
            }
        }
    }

    return routeLineApi
}
