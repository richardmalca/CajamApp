package pe.cajamapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun DestinationGallery(photos: List<DestinationPhoto>) {
    if (photos.isEmpty()) return
    val pager = rememberPagerState { photos.size }
    val scope = rememberCoroutineScope()
    var showCredits by remember { mutableStateOf(false) }
    val photo = photos[pager.currentPage]
    Box(Modifier.fillMaxWidth().heightIn(min = 210.dp, max = 350.dp).aspectRatio(1.55f)) {
        HorizontalPager(state = pager, beyondViewportPageCount = 1, modifier = Modifier.fillMaxSize()) { page ->
            DestinationPhotoImage(
                photos[page],
                Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }
        if (photos.size > 1) {
            Row(
                Modifier.align(Alignment.BottomEnd).padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val controls = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color(0xE8233B30), contentColor = Color.White,
                    disabledContainerColor = Color(0x99233B30), disabledContentColor = Color(0xFFDFE1D8)
                )
                FilledIconButton(
                    onClick = { scope.launch { pager.animateScrollToPage(pager.currentPage - 1) } },
                    enabled = pager.currentPage > 0, colors = controls
                ) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Foto anterior") }
                FilledIconButton(
                    onClick = { scope.launch { pager.animateScrollToPage(pager.currentPage + 1) } },
                    enabled = pager.currentPage < photos.lastIndex, colors = controls
                ) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Foto siguiente") }
            }
        }
    }
    Row(
        Modifier.fillMaxWidth().padding(start = 20.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val count = if (photos.size > 1) "${pager.currentPage + 1} / ${photos.size} · " else ""
        Text(
            count + photo.caption,
            Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TextButton(onClick = { showCredits = true }) {
            Text(if (photo.license == null) "Fuente" else "Créditos")
        }
    }
    if (showCredits) {
        val uri = LocalUriHandler.current
        AlertDialog(
            onDismissRequest = { showCredits = false },
            title = { Text("Créditos de fotografía") },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    Text(photo.caption, style = MaterialTheme.typography.titleSmall)
                    Text(photo.author)
                    TextButton(onClick = { uri.openUri(photo.source) }) { Text("Ver foto original") }
                    if (photo.license != null) {
                        Text("Ajuste de tamaño y encuadre.", style = MaterialTheme.typography.bodySmall)
                        TextButton(onClick = { uri.openUri(photo.license) }) { Text("Ver licencia") }
                    } else {
                        Text("Disponible en línea desde su fuente.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showCredits = false }) { Text("Cerrar") } }
        )
    }
}
