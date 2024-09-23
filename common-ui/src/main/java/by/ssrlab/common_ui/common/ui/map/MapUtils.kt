package by.ssrlab.common_ui.common.ui.map

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import by.ssrlab.common_ui.R
import by.ssrlab.common_ui.databinding.ViewMapPointBinding
import by.ssrlab.data.data.common.DescriptionData
import com.mapbox.geojson.Point
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.viewannotation.ViewAnnotationManager
import com.mapbox.maps.viewannotation.viewAnnotationOptions

fun isLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    return isGpsEnabled && isNetworkEnabled
}

fun showLocationDialog(activity: Activity, onEnable: () -> Unit) {
    AlertDialog.Builder(activity)
        .setMessage("To display the map, you must enable geolocation. Want to turn it on?")
        .setPositiveButton("YES") { _, _ -> onEnable() }
        .setNegativeButton("NO") { dialog, _ ->
            activity.finish()
            Toast.makeText(activity, "Location needed", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        .setCancelable(false)
        .create()
        .show()
}

fun addMapPoint(
    activity: MapActivity,
    pointObject: DescriptionData,
    viewAnnotationManager: ViewAnnotationManager,
    annotationArray: ArrayList<View>,
    context: Context
) {
    val point = Point.fromLngLat(pointObject.lon!!, pointObject.lat!!)
    val pointNumber = pointObject.keyName.substringBefore(".")
    val viewAnnotation = viewAnnotationManager.addViewAnnotation(
        resId = R.layout.view_map_point,
        options = viewAnnotationOptions { geometry(point) }
    )

    annotationArray.add(viewAnnotation)
    viewAnnotation.findViewById<TextView>(R.id.view_map_text).text = pointNumber
    viewAnnotation.setOnClickListener {
        setMapPointClicked(viewAnnotation, pointObject, activity, context, annotationArray)
    }

    ViewMapPointBinding.bind(viewAnnotation)
}

private fun setMapPointClicked(
    viewAnnotation: View,
    pointObject: DescriptionData,
    activity: MapActivity,
    context: Context,
    annotationArray: ArrayList<View>
) {
    val point = Point.fromLngLat(pointObject.lon!!, pointObject.lat!!)
    activity.mapView.camera.flyTo(
        cameraOptions { center(point); zoom(16.0) }
    )

    var currentPoint = Point.fromLngLat(0.0, 0.0)

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        return
    }
    activity.fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) currentPoint = Point.fromLngLat(location.longitude, location.latitude)
        showMapDialog(activity, pointObject, viewAnnotation, currentPoint, 0, annotationArray)
    }.addOnFailureListener {
        showMapDialog(activity, pointObject, viewAnnotation, currentPoint, 1, annotationArray)
    }
}

private fun showMapDialog(
    activity: MapActivity,
    pointObject: DescriptionData,
    viewAnnotation: View,
    currentPoint: Point,
    errorCode: Int,
    annotationArray: ArrayList<View>
) {
    MapDialog(
        activity,
        pointObject,
        viewAnnotation,
        annotationArray,
        currentPoint,
        activity.mapboxNavigation,
        errorCode
    )
        .show(activity.supportFragmentManager, pointObject.keyName)

    viewAnnotation.findViewById<ConstraintLayout>(R.id.view_map_parent).background =
        ContextCompat.getDrawable(activity, R.drawable.background_map_point_active)
    viewAnnotation.findViewById<TextView>(R.id.view_map_text).setTextColor(
        ContextCompat.getColor(activity, R.color.map_point_text_active)
    )
}