package pe.cajamapp.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.outlined.LocationOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.Locale
import kotlin.math.roundToInt
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import pe.cajamapp.data.model.Destination
import pe.cajamapp.data.repository.RoutePoint
import pe.cajamapp.data.repository.VisitRoute
import pe.cajamapp.data.repository.loadDrivingRoute
import pe.cajamapp.ui.components.LeafletMapView
import pe.cajamapp.ui.viewmodel.UserLocation

private val plaza = RoutePoint(-7.1578, -78.5175)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun VisitScreen(destination: Destination, onBack: () -> Unit) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val destinationPoint = remember(destination.id) { RoutePoint(destination.latitude, destination.longitude) }
    var following by remember { mutableStateOf(false) }
    var lastFix by remember { mutableLongStateOf(0L) }
    var liveLocation by remember { mutableStateOf<UserLocation?>(null) }
    var locationMessage by remember { mutableStateOf<String?>(null) }
    var origin by remember(destination.id) { mutableStateOf(plaza) }
    var attempt by remember { mutableIntStateOf(0) }
    var route by remember(destination.id) { mutableStateOf<VisitRoute?>(null) }
    var routingFailed by remember { mutableStateOf(false) }

    LaunchedEffect(destinationPoint, origin, attempt) {
        route = null
        routingFailed = false
        delay(1000)
        try {
            route = loadDrivingRoute(origin, destinationPoint)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            routingFailed = true
        }
    }

    val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    val locationPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        following = result.values.any { it }
        locationMessage = if (following) null else "Activa el permiso de ubicación en los ajustes de Android."
    }

    DisposableEffect(following, lifecycle, context) {
        val client = LocationServices.getFusedLocationProviderClient(context)
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                if (!following || !lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) return
                result.lastLocation?.let {
                    liveLocation = UserLocation(it.latitude, it.longitude)
                    lastFix = it.elapsedRealtimeNanos
                    locationMessage = null
                }
            }
        }
        fun start() {
            try {
                val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000)
                    .setMinUpdateIntervalMillis(1500)
                    .setMaxUpdateAgeMillis(0)
                    .build()
                client.requestLocationUpdates(request, callback, Looper.getMainLooper())
                    .addOnFailureListener {
                        if (following) {
                            following = false
                            locationMessage = "No se pudo activar el GPS. Revisa la ubicación del dispositivo."
                        }
                    }
            } catch (_: SecurityException) {
                following = false
                locationMessage = "Activa el permiso de ubicación para seguir tu posición."
            }
        }
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> start()
                Lifecycle.Event.ON_PAUSE -> client.removeLocationUpdates(callback)
                else -> Unit
            }
        }
        if (following) lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            client.removeLocationUpdates(callback)
        }
    }

    LaunchedEffect(following, lastFix) {
        if (following) {
            delay(15000)
            locationMessage = if (liveLocation == null) "Todavía no hay señal GPS. Verifica que la ubicación esté activada."
            else "Señal GPS interrumpida. El mapa muestra tu última posición."
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(destination.name, style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver a la ficha") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding)) {
            val points = route?.points.orEmpty()
            LeafletMapView(
                destinations = listOf(destination),
                userLocation = liveLocation.takeIf { following },
                onDestinationClick = {},
                locationRequest = 0,
                fitResults = false,
                focusDestination = destination,
                routePoints = points,
                followLocation = following,
                modifier = Modifier.weight(1f).fillMaxWidth()
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Column(
                Modifier.fillMaxWidth().heightIn(max = 154.dp).verticalScroll(rememberScrollState())
                    .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 2.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val originLabel = if (origin == plaza) "Desde la Plaza de Armas" else "Desde tu ubicación al calcular"
                Text(originLabel, style = MaterialTheme.typography.titleMedium)
                val itinerary = route
                if (itinerary != null) {
                    Text(
                        "${distanceLabel(itinerary.meters)} · ${(itinerary.seconds / 60).roundToInt().coerceAtLeast(1)} min aprox. en auto",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else if (routingFailed) {
                    Text("No se pudo calcular la ruta. El destino sigue visible en el mapa.")
                    TextButton(onClick = { attempt++ }) { Text("Reintentar ruta") }
                } else {
                    Text("Calculando ruta en auto…", style = MaterialTheme.typography.bodyMedium)
                }
                if (following) {
                    val current = liveLocation
                    val message = locationMessage ?: if (current == null) "Buscando señal GPS…"
                    else "${distanceLabel(distanceTo(current, destinationPoint))} al destino en línea recta"
                    Text(message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (current != null && locationMessage == null) {
                        TextButton(onClick = {
                            origin = RoutePoint(current.latitude, current.longitude)
                            attempt++
                        }) { Text("Calcular desde mi ubicación") }
                        Text("Al calcular, el punto de partida se envía a OSRM.", style = MaterialTheme.typography.bodySmall)
                    }
                } else {
                    locationMessage?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                }
            }
            FilledTonalButton(
                onClick = {
                    locationMessage = null
                    if (following) {
                        following = false
                        liveLocation = null
                    } else if (permissions.any { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }) {
                        following = true
                    } else {
                        locationPermission.launch(permissions)
                    }
                },
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .heightIn(min = 48.dp)
            ) {
                Icon(if (following) Icons.Outlined.LocationOff else Icons.Default.MyLocation, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (following) "Detener seguimiento" else "Activar ubicación en vivo")
            }
        }
    }
}

private fun distanceTo(location: UserLocation, destination: RoutePoint): Double {
    val distance = FloatArray(1)
    Location.distanceBetween(location.latitude, location.longitude, destination.latitude, destination.longitude, distance)
    return distance[0].toDouble()
}

private fun distanceLabel(meters: Double): String =
    if (meters >= 1000) String.format(Locale.forLanguageTag("es-PE"), "%.1f km", meters / 1000)
    else "${meters.roundToInt()} m"
