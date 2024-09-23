package by.ssrlab.common_ui.common.ui.map

import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.lifecycle.MapboxNavigationObserver
import com.mapbox.navigation.core.lifecycle.requireMapboxNavigation
import kotlin.properties.ReadOnlyProperty

class MapNavigationDelegate(
    private val activity: MapActivity,
    private val onResumedObserver: MapboxNavigationObserver
) {
    private val mapboxNavigationDelegate: ReadOnlyProperty<Any, MapboxNavigation> = requireMapboxNavigation(
        onResumedObserver = onResumedObserver
    )

    val mapboxNavigation: MapboxNavigation
        get() = mapboxNavigationDelegate.getValue(activity, ::mapboxNavigation)
}
