package pe.cajamapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Terrain
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import androidx.compose.ui.unit.dp
import pe.cajamapp.data.model.Destination

@Composable
fun DestinationVisual(destination: Destination, modifier: Modifier = Modifier) {
    val photo = destinationPhotos(destination.id).firstOrNull()
    Box(modifier, contentAlignment = Alignment.Center) {
        if (photo != null) {
            DestinationPhotoImage(photo, Modifier.matchParentSize(), decorative = true)
        } else {
            val icon = when (destination.category) {
                "Arqueología", "Cultura" -> Icons.Outlined.AccountBalance
                "Aguas Termales" -> Icons.Outlined.WaterDrop
                "Gastronomía" -> Icons.Outlined.Restaurant
                else -> Icons.Outlined.Terrain
            }
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun DestinationPhotoImage(photo: DestinationPhoto, modifier: Modifier = Modifier, decorative: Boolean = false) {
    val description = photo.caption.takeUnless { decorative }
    when {
        photo.drawable != null -> Image(
            painter = painterResource(photo.drawable),
            contentDescription = description,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
        photo.imageUrl != null -> {
            var failed by remember(photo.imageUrl) { mutableStateOf(false) }
            Box(modifier, contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = photo.imageUrl,
                    contentDescription = description,
                    onError = { failed = true },
                    onSuccess = { failed = false },
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
                if (failed) {
                    if (decorative) Icon(Icons.Outlined.Terrain, null, tint = MaterialTheme.colorScheme.primary)
                    else Text("Foto en línea no disponible", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
