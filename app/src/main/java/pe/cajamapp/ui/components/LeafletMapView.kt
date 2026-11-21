package pe.cajamapp.ui.components

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.json.JSONArray
import org.json.JSONObject
import pe.cajamapp.data.model.Destination
import pe.cajamapp.data.repository.RoutePoint
import pe.cajamapp.ui.viewmodel.UserLocation

class WebAppInterface(private val onDestinationClicked: (Long) -> Unit) {
    @JavascriptInterface
    fun onDestinationClick(id: Long) {
        onDestinationClicked(id)
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LeafletMapView(
    destinations: List<Destination>,
    userLocation: UserLocation?,
    onDestinationClick: (Long) -> Unit,
    locationRequest: Int,
    fitResults: Boolean,
    modifier: Modifier = Modifier,
    focusDestination: Destination? = null,
    routePoints: List<RoutePoint> = emptyList(),
    followLocation: Boolean = false
) {
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var isMapLoaded by remember { mutableStateOf(false) }

    val destinationsJson = remember(destinations) {
        val jsonArray = JSONArray()
        destinations.forEach { dest ->
            val obj = JSONObject().apply {
                put("id", dest.id)
                put("name", dest.name)
                put("latitude", dest.latitude)
                put("longitude", dest.longitude)
            }
            jsonArray.put(obj)
        }
        jsonArray.toString()
    }

    LaunchedEffect(isMapLoaded, destinationsJson, fitResults) {
        if (isMapLoaded) {
            webViewRef?.evaluateJavascript("setDestinations($destinationsJson, $fitResults);", null)
        }
    }

    LaunchedEffect(isMapLoaded, focusDestination?.id, routePoints) {
        val destination = focusDestination
        if (isMapLoaded && destination != null) {
            val points = JSONArray()
            routePoints.forEach { point ->
                points.put(JSONArray().put(point.latitude).put(point.longitude))
            }
            webViewRef?.evaluateJavascript(
                "showVisit(${destination.latitude}, ${destination.longitude}, $points);", null
            )
        }
    }

    LaunchedEffect(isMapLoaded, userLocation, locationRequest, followLocation) {
        if (isMapLoaded) {
            val script = when {
                userLocation == null -> "clearUserLocation();"
                followLocation -> "followUser(${userLocation.latitude}, ${userLocation.longitude});"
                else -> "centerOnUser(${userLocation.latitude}, ${userLocation.longitude});"
            }
            webViewRef?.evaluateJavascript(script, null)
        }
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { ctx ->
            WebView(ctx).apply {
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    allowFileAccess = false
                    allowContentAccess = false
                    cacheMode = WebSettings.LOAD_DEFAULT
                    userAgentString = "$userAgentString CajamApp/1.0"
                }
                webChromeClient = object : WebChromeClient() {
                    override fun onConsoleMessage(message: ConsoleMessage): Boolean {
                        if (message.messageLevel() == ConsoleMessage.MessageLevel.ERROR) {
                            android.util.Log.w("CajamAppMap", "${message.message()} (línea ${message.lineNumber()})")
                        }
                        return true
                    }
                }
                webViewClient = object : WebViewClient() {
                    override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
                        if (request.url.host != mapAssetHost || request.url.scheme != "https") return null
                        val (path, type) = when (request.url.path) {
                            "/leaflet_map.html" -> "leaflet_map.html" to "text/html"
                            "/leaflet/leaflet.css" -> "leaflet/leaflet.css" to "text/css"
                            "/leaflet/leaflet.js" -> "leaflet/leaflet.js" to "application/javascript"
                            else -> return WebResourceResponse("text/plain", "UTF-8", 404, "Not Found", emptyMap(), null)
                        }
                        return WebResourceResponse(type, "UTF-8", ctx.assets.open(path))
                    }

                    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                        if (request.isForMainFrame && request.url.scheme == "https" &&
                            request.url.host == "www.openstreetmap.org"
                        ) {
                            try {
                                ctx.startActivity(Intent(Intent.ACTION_VIEW, request.url))
                            } catch (_: ActivityNotFoundException) {
                                return true
                            }
                        }
                        return true
                    }

                    override fun onPageFinished(view: WebView, url: String) {
                        isMapLoaded = true
                    }
                }
                addJavascriptInterface(WebAppInterface { id -> post { onDestinationClick(id) } }, "AndroidBridge")
                loadUrl("https://$mapAssetHost/leaflet_map.html")
                webViewRef = this
            }
        },
        onRelease = { webView ->
            webView.removeJavascriptInterface("AndroidBridge")
            webView.destroy()
        }
    )
}

private const val mapAssetHost = "appassets.androidplatform.net"
