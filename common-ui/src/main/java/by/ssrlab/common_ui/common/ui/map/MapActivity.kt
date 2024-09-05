package by.ssrlab.common_ui.common.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import by.ssrlab.common_ui.R
import by.ssrlab.common_ui.common.ui.base.BaseActivity
import by.ssrlab.common_ui.databinding.ActivityMapBinding
import by.ssrlab.common_ui.databinding.ViewMapPointBinding
import by.ssrlab.data.data.common.DescriptionData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.extension.style.expressions.generated.Expression
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.locationcomponent.LocationComponentConstants
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.scalebar.scalebar
import com.mapbox.maps.viewannotation.ViewAnnotationManager
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.directions.session.RoutesObserver
import com.mapbox.navigation.core.lifecycle.MapboxNavigationObserver
import com.mapbox.navigation.core.lifecycle.requireMapboxNavigation
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineOptions
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineColorResources
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineResources
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineScaleValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MapActivity : BaseActivity() {

    private lateinit var binding: ActivityMapBinding
    private val scope = CoroutineScope(Dispatchers.IO + Job())

    //List of pressed points
    private var pointsArray = arrayListOf<DescriptionData>()
    private val pointActivatedArray = arrayListOf<Boolean>()
    private val annotationArray = arrayListOf<View>()

    private var mapView: MapView? = null
    private lateinit var mapboxMap: MapboxMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var viewAnnotationManager: ViewAnnotationManager

    private lateinit var routeLineResources: RouteLineResources
    private lateinit var options: MapboxRouteLineOptions
    private lateinit var routeLineApi: MapboxRouteLineApi
    private lateinit var routesObserver: RoutesObserver

    private val mapboxNavigation: MapboxNavigation by requireMapboxNavigation(
        onResumedObserver = object : MapboxNavigationObserver {
            override fun onAttached(mapboxNavigation: MapboxNavigation) {
                mapboxNavigation.registerRoutesObserver(routesObserver)
            }

            override fun onDetached(mapboxNavigation: MapboxNavigation) {
                mapboxNavigation.unregisterRoutesObserver(routesObserver)
            }
        }
    )

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.dark(
                android.graphics.Color.TRANSPARENT
            )
        )

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapView = binding.map
        viewAnnotationManager = binding.map.viewAnnotationManager

        //62 line through binding.map etc. not through property mapboxMap
        mapView?.scalebar?.enabled = false
        mapboxMap = binding.map.getMapboxMap()
        mapboxMap.loadStyleUri(Style.MAPBOX_STREETS).apply {
            for (i in intent.getParcelableArrayListExtra<DescriptionData>(MAPBOX_VIEW_POINT_LIST)!!) {
                addPoint(i)
                pointActivatedArray.add(false)
            }
        }

        if (intent.hasExtra(CAMERA_ACTION) && intent.hasExtra(MAPBOX_VIEW_POINT_LIST)) {
            for (i in intent.getParcelableArrayListExtra<DescriptionData>(MAPBOX_VIEW_POINT_LIST)!!) {
                setLocationForSinglePoint(i)
            }
        } else {
            setLocationAction()
        }

        setMapboxOptions()
        setBackAction()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@MapActivity)
    }

    override fun onStart() {
        super.onStart()

        //TODO make camera translation from the single selected point
    }

    private fun setLocationForSinglePoint(location: DescriptionData) {
        scope.launch {
            withContext(Dispatchers.Main) {
                setupMapbox()
            }

            binding.mapPosition.apply {
                withContext(Dispatchers.Main) {
                        mapView?.camera?.flyTo(
                            cameraOptions {
                                center(Point.fromLngLat(location.lon!!, location.lat!!))
                            }
                        )
                }
            }
        }
    }

    private fun setLocationAction() {
        scope.launch {
            if (checkPermission()) {

                withContext(Dispatchers.Main) {
                    setupMapbox()
                }

                binding.mapPosition.apply {
                    withContext(Dispatchers.Main) {
                        setImageResource(R.drawable.ic_location)
                        setOnClickListener {
                            //Current location check and then center camera on user
                            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                mapView?.camera?.flyTo(
                                    cameraOptions {
                                        center(
                                            Point.fromLngLat(
                                                location.longitude,
                                                location.latitude
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            } else {
                binding.mapPosition.apply {
                    binding.mapPosition.setImageResource(R.drawable.ic_location_disable)
                    setOnClickListener {
                        requestPermission()
                    }
                }
            }

            delay(MAPBOX_LOCATION_RECHECK_TIME)
            checkPermission()
            setLocationAction()
        }
    }

    private fun setBackAction() {
        binding.mapBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupMapbox() {
        mapView?.location?.apply {
            enabled = true
            pulsingEnabled = true
            pulsingColor = ContextCompat.getColor(this@MapActivity, R.color.map_red)

            locationPuck = LocationPuck2D(
                topImage = AppCompatResources.getDrawable(
                    this@MapActivity,
                    R.drawable.ic_location_point
                ),
                bearingImage = AppCompatResources.getDrawable(
                    this@MapActivity,
                    R.drawable.ic_location_point
                ),
                shadowImage = AppCompatResources.getDrawable(
                    this@MapActivity,
                    R.drawable.ic_location_point
                ),

                //This setting defines how lines and other will be interpolated on map scrolling
                scaleExpression = interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0.0)
                        literal(0.6)
                    }
                    stop {
                        literal(18.0)
                        literal(1.0)
                    }
                }.toJson()
            )
        }
    }

    private fun addPoint(pointObject: DescriptionData) {
        //Be careful with !! operator
        val point = Point.fromLngLat(pointObject.lon!!, pointObject.lat!!)
        val pointNumber = pointObject.pk.toString()
        val viewAnnotation = viewAnnotationManager.addViewAnnotation(
            resId = R.layout.view_map_point,
            options = viewAnnotationOptions { geometry(point) }
        )

        annotationArray.add(viewAnnotation)

        viewAnnotation.findViewById<TextView>(R.id.view_map_text).text = pointNumber
        viewAnnotation.setOnClickListener {
            setMapPointClicked(viewAnnotation, pointObject)
        }

        ViewMapPointBinding.bind(viewAnnotation)
    }

    @SuppressLint("MissingPermission")
    private fun setMapPointClicked(viewAnnotation: View, pointObject: DescriptionData) {
        val point = Point.fromLngLat(pointObject.lon!!, pointObject.lat!!)
        mapView?.camera?.flyTo(
            cameraOptions {
                center(point)
                zoom(16.0)
            })

        var currentPoint = Point.fromLngLat(0.0, 0.0)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) currentPoint =
                Point.fromLngLat(location.longitude, location.latitude)

            MapDialog(
                this@MapActivity,
                pointObject,
                viewAnnotation,
                annotationArray,
                currentPoint,
                mapboxNavigation,
                0
            )
                .show(supportFragmentManager, pointObject.keyName)

            viewAnnotation.findViewById<ConstraintLayout>(R.id.view_map_parent).background =
                ContextCompat.getDrawable(this@MapActivity, R.drawable.background_map_point_active)
            viewAnnotation.findViewById<TextView>(R.id.view_map_text).setTextColor(
                ContextCompat.getColor(
                    this@MapActivity,
                    R.color.map_point_text_active
                )
            )
        }.addOnFailureListener {
            MapDialog(
                this@MapActivity,
                pointObject,
                viewAnnotation,
                annotationArray,
                currentPoint,
                mapboxNavigation,
                1
            )
                .show(supportFragmentManager, pointObject.keyName)

            viewAnnotation.findViewById<ConstraintLayout>(R.id.view_map_parent).background =
                ContextCompat.getDrawable(this@MapActivity, R.drawable.background_map_point_active)
            viewAnnotation.findViewById<TextView>(R.id.view_map_text).setTextColor(
                ContextCompat.getColor(
                    this@MapActivity,
                    R.color.map_point_text_active
                )
            )
        }
    }

    private fun buildScaleExpression(scalingValues: List<RouteLineScaleValue>): Expression {
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

    private fun setRouteLineResources() {
        routeLineResources = RouteLineResources.Builder()
            .routeLineColorResources(
                RouteLineColorResources.Builder()
                    .routeDefaultColor(
                        ContextCompat.getColor(
                            this@MapActivity,
                            R.color.map_red_secondary
                        )
                    )
                    .routeCasingColor(
                        ContextCompat.getColor(
                            this@MapActivity,
                            R.color.map_red_secondary
                        )
                    )
                    .routeClosureColor(
                        ContextCompat.getColor(
                            this@MapActivity,
                            R.color.map_red_secondary
                        )
                    )
                    .routeHeavyCongestionColor(
                        ContextCompat.getColor(
                            this@MapActivity,
                            R.color.map_red_secondary
                        )
                    )
                    .routeLowCongestionColor(
                        ContextCompat.getColor(
                            this@MapActivity,
                            R.color.map_red_secondary
                        )
                    )
                    .routeModerateCongestionColor(
                        ContextCompat.getColor(
                            this@MapActivity,
                            R.color.map_red_secondary
                        )
                    )
                    .routeSevereCongestionColor(
                        ContextCompat.getColor(
                            this@MapActivity,
                            R.color.map_red_secondary
                        )
                    )
                    .routeUnknownCongestionColor(
                        ContextCompat.getColor(
                            this@MapActivity,
                            R.color.map_red_secondary
                        )
                    )
                    .routeDefaultColor(
                        ContextCompat.getColor(
                            this@MapActivity,
                            R.color.map_red_secondary
                        )
                    )
                    .alternativeRouteCasingColor(
                        ContextCompat.getColor(
                            this@MapActivity,
                            R.color.map_red_secondary
                        )
                    )
                    .alternativeRouteClosureColor(
                        ContextCompat.getColor(
                            this@MapActivity,
                            R.color.map_red_secondary
                        )
                    )
                    .alternativeRouteHeavyCongestionColor(
                        ContextCompat.getColor(
                            this@MapActivity,
                            R.color.map_red_secondary
                        )
                    )
                    .alternativeRouteLowCongestionColor(
                        ContextCompat.getColor(
                            this@MapActivity,
                            R.color.map_red_secondary
                        )
                    )
                    .alternativeRouteModerateCongestionColor(
                        ContextCompat.getColor(
                            this@MapActivity,
                            R.color.map_red_secondary
                        )
                    )
                    .alternativeRouteRestrictedRoadColor(
                        ContextCompat.getColor(
                            this@MapActivity,
                            R.color.map_red_secondary
                        )
                    )
                    .alternativeRouteSevereCongestionColor(
                        ContextCompat.getColor(
                            this@MapActivity,
                            R.color.map_red_secondary
                        )
                    )
                    .alternativeRouteUnknownCongestionColor(
                        ContextCompat.getColor(
                            this@MapActivity,
                            R.color.map_red_secondary
                        )
                    )
                    .alternativeRouteDefaultColor(
                        ContextCompat.getColor(
                            this@MapActivity,
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

    private fun setOptions() {
        options = MapboxRouteLineOptions.Builder(this@MapActivity)
            .withVanishingRouteLineEnabled(true)
            .withRouteLineResources(routeLineResources)
            .withRouteLineBelowLayerId(LocationComponentConstants.LOCATION_INDICATOR_LAYER)
            .build()
    }

    private fun setRoutesApi() {
        routeLineApi = MapboxRouteLineApi(options)

        routesObserver = RoutesObserver {
            routeLineApi.setNavigationRoutes(it.navigationRoutes) { value ->
                mapboxMap.getStyle()?.apply {
                    MapboxRouteLineView(options).renderRouteDrawData(this, value)
                }
            }
        }
    }

    private fun setMapboxOptions() {
        setRouteLineResources()
        setOptions()
        setRoutesApi()
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this@MapActivity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            PackageManager.PERMISSION_GRANTED
        )
    }

    private fun checkPermission() = ContextCompat.checkSelfPermission(
        this@MapActivity,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        ActivityCompat.requestPermissions(this@MapActivity, permissions, 0)
    }
}