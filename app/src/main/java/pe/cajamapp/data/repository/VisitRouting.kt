package pe.cajamapp.data.repository

import java.net.HttpURLConnection
import java.net.URI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

data class RoutePoint(val latitude: Double, val longitude: Double)

data class VisitRoute(
    val points: List<RoutePoint>,
    val meters: Double,
    val seconds: Double
)

suspend fun loadDrivingRoute(origin: RoutePoint, destination: RoutePoint): VisitRoute = withContext(Dispatchers.IO) {
    val coordinates = "${origin.longitude},${origin.latitude};${destination.longitude},${destination.latitude}"
    val url = URI("https://routing.openstreetmap.de/routed-car/route/v1/driving/$coordinates?overview=simplified&geometries=geojson").toURL()
    val connection = url.openConnection() as HttpURLConnection
    try {
        connection.connectTimeout = 7000
        connection.readTimeout = 7000
        connection.setRequestProperty("User-Agent", "CajamApp/1.0 (Android)")
        val response = connection.inputStream.bufferedReader().use { JSONObject(it.readText()) }
        val route = response.getJSONArray("routes").getJSONObject(0)
        val coordinatesArray = route.getJSONObject("geometry").getJSONArray("coordinates")
        val points = List(coordinatesArray.length()) { index ->
            val point = coordinatesArray.getJSONArray(index)
            RoutePoint(point.getDouble(1), point.getDouble(0))
        }
        check(points.size >= 2)
        VisitRoute(points, route.getDouble("distance"), route.getDouble("duration"))
    } finally {
        connection.disconnect()
    }
}
