package pe.cajamapp.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import pe.cajamapp.data.model.Destination
import pe.cajamapp.ui.theme.AccentAmber
import pe.cajamapp.ui.theme.EmeraldGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    destination: Destination,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onToggleFavorite,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (destination.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Guardar",
                            tint = if (destination.isFavorite) AccentAmber else MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (destination.isFavorite) "Guardado" else "Guardar")
                    }

                    Button(
                        onClick = {
                            val uri = Uri.parse("geo:${destination.latitude},${destination.longitude}?q=${destination.latitude},${destination.longitude}(${destination.name})")
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            context.startActivity(intent)
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1.5f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.Navigation, contentDescription = "Navegar")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Iniciar Ruta GPS")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                AsyncImage(
                    model = destination.imageUrl,
                    contentDescription = destination.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0x66000000), Color.Transparent, Color(0xD90F172A))
                            )
                        )
                )

                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(12.dp)
                        .background(Color(0x66000000), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = destination.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = destination.name,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "${destination.province}, Cajamarca", style = MaterialTheme.typography.bodySmall, color = Color.White)
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(Icons.Default.Terrain, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = destination.altitude, style = MaterialTheme.typography.bodySmall, color = Color.White)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoSection(title = "Descripción Turística") {
                    Text(
                        text = destination.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                InfoCard(
                    icon = Icons.Default.Payments,
                    iconTint = EmeraldGreen,
                    title = "Tarifas de Ingreso",
                    content = destination.entryFee
                )

                InfoCard(
                    icon = Icons.Default.DirectionsBus,
                    iconTint = MaterialTheme.colorScheme.primary,
                    title = "Cómo Llegar & Transporte",
                    content = "${destination.howToGetThere}\n\n• Modalidad: ${destination.transportType}\n• Tiempo estimado: ${destination.travelTime}\n• Pasaje aprox.: ${destination.estimatedFare}"
                )

                InfoCard(
                    icon = Icons.Default.Schedule,
                    iconTint = AccentAmber,
                    title = "Horario de Visita",
                    content = destination.openingHours
                )

                if (destination.tips.isNotEmpty()) {
                    InfoCard(
                        icon = Icons.Default.Lightbulb,
                        iconTint = AccentAmber,
                        title = "Recomendaciones Locales",
                        content = destination.tips
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun InfoSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

@Composable
fun InfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    content: String
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
