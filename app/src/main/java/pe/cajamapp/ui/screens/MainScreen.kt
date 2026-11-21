package pe.cajamapp.ui.screens

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.selection.selectable
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import pe.cajamapp.R
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import pe.cajamapp.data.model.Destination
import pe.cajamapp.ui.components.DestinationListItem
import pe.cajamapp.ui.components.DestinationGallery
import pe.cajamapp.ui.components.DestinationVisual
import pe.cajamapp.ui.components.destinationPhotos
import androidx.compose.foundation.clickable
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import pe.cajamapp.ui.components.LeafletMapView
import pe.cajamapp.ui.viewmodel.DestinationsViewModel

enum class NavigationTab(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector
) {
    EXPLORAR("Explorar", Icons.Outlined.Explore, Icons.Filled.Explore),
    MAPA("Mapa", Icons.Outlined.Map, Icons.Filled.Map),
    FAVORITOS("Favoritos", Icons.Outlined.BookmarkBorder, Icons.Filled.Bookmark)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: DestinationsViewModel) {
    val destinations by viewModel.destinations.collectAsState()
    val favorites by viewModel.favoriteDestinations.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    val selectedDestination by viewModel.selectedDestination.collectAsState()

    var currentTab by rememberSaveable { mutableStateOf(NavigationTab.EXPLORAR) }
    var visitDestinationId by rememberSaveable { mutableStateOf<Long?>(null) }
    val visiting = visitDestinationId != null && visitDestinationId == selectedDestination?.id
    val tabState = rememberSaveableStateHolder()
    val context = LocalContext.current
    var locationRequest by remember { mutableIntStateOf(0) }

    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val granted = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                      perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            locationRequest++
            obtainLocation(context) { lat, lng -> viewModel.updateUserLocation(lat, lng) }
        }
    }

    val requestLocation = {
        if (listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).any {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }) {
            locationRequest++
            obtainLocation(context) { lat, lng -> viewModel.updateUserLocation(lat, lng) }
        } else {
            locationLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    BackHandler(selectedDestination != null) {
        if (visiting) {
            visitDestinationId = null
        } else {
            selectedDestination?.let { tabState.removeState("detail-${it.id}") }
            viewModel.selectDestination(null)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (selectedDestination == null) NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                NavigationTab.entries.forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.icon,
                                contentDescription = null
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .imePadding()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (selectedDestination == null) tabState.SaveableStateProvider(currentTab.name) {
                when (currentTab) {
                    NavigationTab.EXPLORAR -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                            ExplorarView(
                                destinations = destinations,
                                searchQuery = searchQuery,
                                categories = viewModel.categories,
                                selectedCategory = selectedCategory,
                                onOpenMap = { currentTab = NavigationTab.MAPA },
                                onSearchChange = { viewModel.onSearchQueryChanged(it) },
                                onCategorySelect = { viewModel.onCategorySelected(it) },
                                onDestinationClick = { viewModel.selectDestination(it) },
                                onToggleFavorite = { viewModel.toggleFavorite(it) }
                            )
                        }
                    }
                    NavigationTab.MAPA -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            LeafletMapView(
                                destinations = destinations,
                                userLocation = userLocation,
                                onDestinationClick = { id -> viewModel.selectDestinationById(id) },
                                locationRequest = locationRequest,
                                fitResults = searchQuery.isNotBlank() || selectedCategory != "Todos",
                                modifier = Modifier.fillMaxSize()
                            )

                            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                                DestinationSearch(searchQuery, viewModel::onSearchQueryChanged, "Buscar en el mapa")
                                if (selectedCategory != "Todos") {
                                    InputChip(
                                        selected = true,
                                        onClick = { viewModel.onCategorySelected("Todos") },
                                        label = { Text(selectedCategory) },
                                        trailingIcon = { Icon(Icons.Default.Close, "Quitar filtro") }
                                    )
                                }
                                if (destinations.isEmpty() && (searchQuery.isNotBlank() || selectedCategory != "Todos")) {
                                    Surface(shape = RoundedCornerShape(8.dp)) {
                                        Text("No hay destinos para esta búsqueda.", Modifier.padding(8.dp))
                                    }
                                }
                            }
                            FilledTonalIconButton(
                                onClick = requestLocation,
                                modifier = Modifier.align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 32.dp).size(48.dp)
                            ) {
                                Icon(Icons.Default.MyLocation, contentDescription = "Mostrar mi ubicación")
                            }
                        }
                    }
                    NavigationTab.FAVORITOS -> {
                        FavoritosView(
                            favorites = favorites,
                            onExplore = { currentTab = NavigationTab.EXPLORAR },
                            onDestinationClick = { viewModel.selectDestination(it) },
                            onToggleFavorite = { viewModel.toggleFavorite(it) }
                        )
                    }
                }
            }

            selectedDestination?.let { destination ->
                if (visiting) {
                    VisitScreen(destination, onBack = { visitDestinationId = null })
                } else {
                    tabState.SaveableStateProvider("detail-${destination.id}") {
                        DestinationDetailView(
                            destination = destination,
                            onBack = {
                                tabState.removeState("detail-${destination.id}")
                                viewModel.selectDestination(null)
                            },
                            onToggleFavorite = { viewModel.toggleFavorite(destination) },
                            onVisit = { visitDestinationId = destination.id }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DestinationSearch(
    query: String,
    onChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onChange,
        placeholder = { Text(hint, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Limpiar búsqueda")
                }
            }
        },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        singleLine = true
    )
}

@Composable
private fun CajamarcaHero(
    highlights: List<Destination>,
    onOpenMap: () -> Unit,
    onDestinationClick: (Destination) -> Unit
) {
    val pager = rememberPagerState { highlights.size + 1 }
    Column {
        HorizontalPager(pager, modifier = Modifier.fillMaxWidth()) { page ->
            val destination = highlights.getOrNull(page - 1)
            Box(
                Modifier.fillMaxWidth().heightIn(min = 224.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { if (destination == null) onOpenMap() else onDestinationClick(destination) }
            ) {
                if (destination == null) {
                    Image(
                        painterResource(R.drawable.plaza_cajamarca), null,
                        contentScale = ContentScale.Crop, modifier = Modifier.matchParentSize()
                    )
                } else {
                    DestinationVisual(destination, modifier = Modifier.matchParentSize())
                }
                Box(
                    Modifier.matchParentSize().background(
                        Brush.verticalGradient(listOf(Color.Transparent, Color(0xF21B291E)))
                    )
                )
                Column(
                    Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        destination?.name ?: "Cajamarca",
                        style = MaterialTheme.typography.headlineLarge.copy(fontFamily = FontFamily.Serif),
                        color = Color.White
                    )
                    Text(
                        destination?.let { "${it.category} · ${it.province}" } ?: "Explorar en el mapa",
                        style = MaterialTheme.typography.labelLarge, color = Color.White
                    )
                }
            }
        }
        if (highlights.isNotEmpty()) {
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.Center) {
                repeat(pager.pageCount) { page ->
                    Box(
                        Modifier.padding(horizontal = 3.dp)
                            .width(if (page == pager.currentPage) 16.dp else 5.dp)
                            .height(5.dp)
                            .background(
                                if (page == pager.currentPage) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outlineVariant,
                                RoundedCornerShape(3.dp)
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun ExplorarView(
    destinations: List<Destination>,
    searchQuery: String,
    categories: List<String>,
    selectedCategory: String,
    onOpenMap: () -> Unit,
    onSearchChange: (String) -> Unit,
    onCategorySelect: (String) -> Unit,
    onDestinationClick: (Destination) -> Unit,
    onToggleFavorite: (Destination) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxHeight().widthIn(max = 728.dp).fillMaxWidth(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item("search") {
            DestinationSearch(
                searchQuery, onSearchChange, "Busca un lugar o provincia",
                Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
        if (searchQuery.isBlank() && selectedCategory == "Todos") {
            item("hero") {
                val highlights = listOf(2L, 5L, 10L).mapNotNull { id ->
                    destinations.find { it.id == id && destinationPhotos(id).isNotEmpty() }
                }
                CajamarcaHero(highlights, onOpenMap, onDestinationClick)
            }
        }
        item("categories") {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp),
                modifier = Modifier.padding(top = 6.dp)
            ) {
                items(categories, key = { it }) { category ->
                    val selected = category == selectedCategory
                    Column(
                        Modifier.selectable(selected, role = Role.Tab) { onCategorySelect(category) }
                            .heightIn(min = 48.dp).padding(horizontal = 8.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            category,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            Modifier.width(24.dp).height(2.dp)
                                .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
                        )
                    }
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }
        item("results") {
            val count = destinations.size
            Text(
                if (searchQuery.isBlank() && selectedCategory == "Todos") "Lugares para visitar"
                else if (count == 1) "1 resultado" else "$count resultados",
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp),
                style = MaterialTheme.typography.headlineSmall
            )
        }
        if (destinations.isEmpty() && (searchQuery.isNotBlank() || selectedCategory != "Todos")) {
            item {
                Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("No encontramos destinos", style = MaterialTheme.typography.titleMedium)
                    Text("Prueba con otro nombre o vuelve a ver todos los lugares.")
                    TextButton(onClick = {
                        onSearchChange("")
                        onCategorySelect("Todos")
                    }) { Text("Limpiar filtros") }
                }
            }
        }
        items(destinations, key = { it.id }) { destination ->
            DestinationListItem(
                destination = destination,
                onClick = { onDestinationClick(destination) },
                onToggleFavorite = { onToggleFavorite(destination) }
            )
        }
    }
}

@Composable
fun FavoritosView(
    favorites: List<Destination>,
    onExplore: () -> Unit,
    onDestinationClick: (Destination) -> Unit,
    onToggleFavorite: (Destination) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Favoritos",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.BookmarkBorder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Aún no has guardado destinos",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Guarda un lugar mientras exploras para encontrarlo aquí.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    TextButton(onClick = onExplore) { Text("Explorar destinos") }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(favorites, key = { it.id }) { item ->
                    DestinationListItem(
                        destination = item,
                        onClick = { onDestinationClick(item) },
                        onToggleFavorite = { onToggleFavorite(item) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DestinationDetailView(
    destination: Destination,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onVisit: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") }
                },
                actions = {
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            if (destination.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            if (destination.isFavorite) "Quitar de favoritos" else "Guardar en favoritos"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        },
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.background) {
                Column {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Button(
                        onClick = onVisit,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp).heightIn(min = 48.dp)
                    ) {
                        Icon(Icons.Default.Directions, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Cómo llegar")
                    }
                }
            }
        }
    ) { insets ->
        Box(Modifier.fillMaxSize().padding(insets), contentAlignment = Alignment.TopCenter) {
            Column(Modifier.widthIn(max = 728.dp).fillMaxWidth().verticalScroll(rememberScrollState())) {
                DestinationGallery(destinationPhotos(destination.id))
                Column(Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                    Text(
                        destination.name,
                        style = MaterialTheme.typography.headlineLarge.copy(fontFamily = FontFamily.Serif)
                    )
                    Spacer(Modifier.height(5.dp))
                    Text(
                        "${destination.category} · ${destination.province} · ${destination.altitude}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(16.dp))
                    TripSummary(destination)
                    if (destination.id in listOf(9L, 12L, 17L)) {
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "Foto del entorno. Confirma con un guía local la ubicación exacta y el acceso.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(destination.description, style = MaterialTheme.typography.bodyLarge)
                    VisitInformation("Horario de atención", destination.openingHours)
                    VisitInformation("Tarifas de ingreso", destination.entryFee)
                    VisitInformation(
                        "Transporte local",
                        "${destination.howToGetThere}\n\nTransporte: ${destination.transportType}\nTiempo estimado: ${destination.travelTime}\nPasaje aproximado: ${destination.estimatedFare}"
                    )
                    if (destination.tips.isNotBlank()) VisitInformation("Recomendaciones locales", destination.tips)
                }
            }
        }
    }
}

@Composable
private fun TripSummary(destination: Destination) {
    val firstFare = destination.estimatedFare.substringBefore('|').trim()
    val price = Regex("S/\\s*(\\d+(?:[.,]\\d+)?)").find(firstFare)?.groupValues?.get(1)
    val fare = price?.let { "S/ $it" } ?: if (firstFare.startsWith("Gratis", ignoreCase = true)) "Gratis" else "Consultar"
    val transport = firstFare.substringBefore(':', "").trim()
    val time = when (destination.id) {
        4L -> "2 min"
        7L -> "1 h 15 min"
        9L -> "1 h 30 min"
        10L -> "2 h 15 min"
        15L -> "2 h 30 min"
        17L -> "3 h + 30 min"
        else -> destination.travelTime.substringBefore(" (").substringBefore(" desde ").substringBefore(" de subida")
            .replace("horas", "h").replace(" - ", "–")
    }
    Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant).padding(14.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            val fareLabel = if (price != null && firstFare.count { it == '/' } > 1) "Pasaje desde" else "Pasaje estimado"
            TripMetric(fareLabel, fare, transport, Modifier.weight(1f))
            TripMetric("Tiempo de viaje", time, "", Modifier.weight(1f))
        }
    }
}

@Composable
private fun TripMetric(label: String, value: String, detail: String, modifier: Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
        if (detail.isNotBlank()) Text(detail, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun VisitInformation(title: String, content: String) {
    Spacer(Modifier.height(20.dp))
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    Spacer(Modifier.height(14.dp))
    Text(title, style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(5.dp))
    Text(content, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

private fun obtainLocation(context: Context, onLocation: (Double, Double) -> Unit) {
    val fused = LocationServices.getFusedLocationProviderClient(context)
    try {
        fused.getCurrentLocation(
            com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            null
        ).addOnCompleteListener { result ->
            val location = if (result.isSuccessful) result.result else null
            if (location != null) {
                onLocation(location.latitude, location.longitude)
            } else {
                android.widget.Toast.makeText(context, "No se pudo obtener tu ubicación.", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    } catch (_: SecurityException) {
        android.widget.Toast.makeText(context, "Activa el permiso de ubicación para usar esta función.", android.widget.Toast.LENGTH_SHORT).show()
    }
}
