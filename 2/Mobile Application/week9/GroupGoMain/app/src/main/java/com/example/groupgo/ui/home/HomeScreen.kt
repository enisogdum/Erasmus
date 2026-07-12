package com.example.groupgo.ui.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.groupgo.domain.model.TransportOption
import com.google.android.gms.location.LocationServices
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import org.json.JSONArray
import org.json.JSONObject
import com.google.android.gms.location.Priority

private const val TAG = "GroupGoMap"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    
    var originName by remember { mutableStateOf("") }
    var destName by remember { mutableStateOf("") }
    var originLat by remember { mutableStateOf("") }
    var originLon by remember { mutableStateOf("") }
    var destLat by remember { mutableStateOf("") }
    var destLon by remember { mutableStateOf("") }

    var selectedPreference by remember { mutableStateOf("Balanced") }
    var currentMapLocation by remember { mutableStateOf("Tap GPS or enter a location") }
    var zoomLevel by remember { mutableStateOf(5) }
    var isGeocodingLocal by remember { mutableStateOf(false) }
    var geocodingErrorLocal by remember { mutableStateOf<String?>(null) }

    var originSuggestions by remember { mutableStateOf<List<PlaceSuggestion>>(emptyList()) }
    var destSuggestions by remember { mutableStateOf<List<PlaceSuggestion>>(emptyList()) }
    var showOriginSuggestions by remember { mutableStateOf(false) }
    var showDestSuggestions by remember { mutableStateOf(false) }
    var lastSelectedOrigin by remember { mutableStateOf<PlaceSuggestion?>(null) }
    var lastSelectedDest by remember { mutableStateOf<PlaceSuggestion?>(null) }

    var showPayerDialog by remember { mutableStateOf(false) }
    var pendingSelectedOption by remember { mutableStateOf<TransportOption?>(null) }

    // Debounced autocomplete search for Start Location (Origin)
    LaunchedEffect(originName) {
        val trimmed = originName.trim()
        val currentSelectedName = lastSelectedOrigin?.let { 
            it.name + if (it.description.isNotEmpty()) ", ${it.description}" else "" 
        }
        if (trimmed.length >= 3 && originName != currentSelectedName) {
            kotlinx.coroutines.delay(400) // 400ms debounce
            val results = fetchPlaceSuggestions(trimmed)
            originSuggestions = results
            showOriginSuggestions = results.isNotEmpty()
        } else {
            originSuggestions = emptyList()
            showOriginSuggestions = false
        }
    }

    // Debounced autocomplete search for Destination
    LaunchedEffect(destName) {
        val trimmed = destName.trim()
        val currentSelectedName = lastSelectedDest?.let { 
            it.name + if (it.description.isNotEmpty()) ", ${it.description}" else "" 
        }
        if (trimmed.length >= 3 && destName != currentSelectedName) {
            kotlinx.coroutines.delay(400) // 400ms debounce
            val results = fetchPlaceSuggestions(trimmed)
            destSuggestions = results
            showDestSuggestions = results.isNotEmpty()
        } else {
            destSuggestions = emptyList()
            showDestSuggestions = false
        }
    }

    val selectedGroup = uiState.groups.find { it.id == uiState.selectedGroupId }
    val showResults = uiState.routes.isNotEmpty()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var locationPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        locationPermissionGranted = fineGranted || coarseGranted
        if (locationPermissionGranted) {
            fetchCurrentLocation(context, fusedLocationClient, coroutineScope) { lat, lon, addressName ->
                originLat = lat.toString()
                originLon = lon.toString()
                originName = addressName
            }
        }
    }

    // State-driven osmdroid MapView instance to handle Jetpack Compose navigation detaches safely
    var mapView by remember { mutableStateOf<MapView?>(null) }

    val myLocationOverlay = remember(mapView) {
        mapView?.let { org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay(it) }
    }

    // Sync Zoom Level changes
    LaunchedEffect(zoomLevel, mapView) {
        mapView?.controller?.setZoom(zoomLevel.toDouble())
    }

    // Keep track of markers and polylines inside MapView based on coordinates state
    val originPoint = remember(originLat, originLon) {
        val lat = originLat.toDoubleOrNull()
        val lon = originLon.toDoubleOrNull()
        if (lat != null && lon != null) GeoPoint(lat, lon) else null
    }
    
    val destPoint = remember(destLat, destLon) {
        val lat = destLat.toDoubleOrNull()
        val lon = destLon.toDoubleOrNull()
        if (lat != null && lon != null) GeoPoint(lat, lon) else null
    }

    LaunchedEffect(originPoint, destPoint, showResults, locationPermissionGranted, mapView) {
        val currentMap = mapView ?: return@LaunchedEffect
        Log.d(TAG, "LaunchedEffect triggered: originPoint=$originPoint, destPoint=$destPoint, showResults=$showResults")
        currentMap.overlays.clear()
        
        // Add live GPS position overlay if permission is active
        if (locationPermissionGranted && myLocationOverlay != null) {
            try {
                if (!myLocationOverlay.isMyLocationEnabled) {
                    myLocationOverlay.enableMyLocation()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error enabling my location overlay", e)
            }
            currentMap.overlays.add(myLocationOverlay)
        }
        
        // Add Origin Marker
        if (originPoint != null) {
            Log.d(TAG, "Adding origin marker at $originPoint for '$originName'")
            try {
                val originMarker = Marker(currentMap).apply {
                    position = originPoint
                    title = "Origin: $originName"
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                }
                currentMap.overlays.add(originMarker)
            } catch (e: Exception) {
                Log.e(TAG, "Error creating origin marker", e)
            }
        }
        
        // Add Destination Marker
        if (destPoint != null) {
            Log.d(TAG, "Adding destination marker at $destPoint for '$destName'")
            try {
                val destMarker = Marker(currentMap).apply {
                    position = destPoint
                    title = "Destination: $destName"
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                }
                currentMap.overlays.add(destMarker)
            } catch (e: Exception) {
                Log.e(TAG, "Error creating destination marker", e)
            }
        }
        
        // Draw Polyline connecting them
        if (originPoint != null && destPoint != null) {
            try {
                val line = Polyline(currentMap).apply {
                    setPoints(listOf(originPoint, destPoint))
                    outlinePaint.color = android.graphics.Color.parseColor("#8B5CF6") // purple
                    outlinePaint.strokeWidth = 6f
                }
                currentMap.overlays.add(line)
                
                // Auto center and zoom to fit bounds
                Log.d(TAG, "Zooming to bounding box between origin and destination")
                try {
                    val bounds = org.osmdroid.util.BoundingBox.fromGeoPoints(listOf(originPoint, destPoint))
                    currentMap.zoomToBoundingBox(bounds, true, 100)
                } catch (e: Exception) {
                    Log.e(TAG, "Error zooming to bounds: ${e.message}")
                    currentMap.controller.animateTo(originPoint)
                    currentMap.controller.setZoom(10.0)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error creating route polyline", e)
            }
        } else if (destPoint != null) {
            Log.d(TAG, "Animating map to destination point")
            currentMap.controller.animateTo(destPoint)
            currentMap.controller.setZoom(12.0)
        } else if (originPoint != null) {
            Log.d(TAG, "Animating map to origin point")
            currentMap.controller.animateTo(originPoint)
            currentMap.controller.setZoom(12.0)
        }
        
        currentMap.invalidate() // Redraw
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // 1. Interactive Map Section (Top Header)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                AndroidView(
                    factory = { ctx ->
                        MapView(ctx).apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
                            zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER)
                            controller.setZoom(5.0) // Start zoomed out to show Europe
                            val europeCenter = GeoPoint(50.0, 15.0)
                            controller.setCenter(europeCenter)
                            
                            mapView = this
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    update = {}
                )

                // Map controls overlay (zoom + / - / GPS tracking)
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Zoom In Button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable {
                                zoomLevel += 1
                                mapView?.controller?.setZoom(zoomLevel.toDouble())
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Zoom In", tint = MaterialTheme.colorScheme.onSurface)
                    }

                    // Zoom Out Button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable {
                                if (zoomLevel > 1) {
                                    zoomLevel -= 1
                                    mapView?.controller?.setZoom(zoomLevel.toDouble())
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Remove, contentDescription = "Zoom Out", tint = MaterialTheme.colorScheme.onSurface)
                    }

                    // GPS FAB Button: Centers map and starts active tracking follow mode
                    var isFollowing by remember { mutableStateOf(false) }
                    
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                if (isFollowing) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surface
                            )
                            .border(
                                width = if (isFollowing) 2.dp else 0.dp,
                                color = if (isFollowing) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable {
                                if (locationPermissionGranted) {
                                    isFollowing = !isFollowing
                                    try {
                                        myLocationOverlay?.enableMyLocation()
                                        if (isFollowing) {
                                            myLocationOverlay?.enableFollowLocation()
                                            // Animate map view to current GPS location and update text fields
                                            fetchCurrentLocation(context, fusedLocationClient, coroutineScope) { lat, lon, addressName ->
                                                originLat = lat.toString()
                                                originLon = lon.toString()
                                                originName = addressName
                                                val geo = GeoPoint(lat, lon)
                                                mapView?.controller?.animateTo(geo)
                                            }
                                        } else {
                                            myLocationOverlay?.disableFollowLocation()
                                        }
                                    } catch (e: Exception) {
                                        Log.e(TAG, "Error enabling or following my location", e)
                                    }
                                } else {
                                    permissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isFollowing) Icons.Filled.GpsFixed else Icons.Filled.GpsNotFixed,
                            contentDescription = "My Location",
                            tint = if (isFollowing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Current location banner
                Card(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                        Text(
                            text = "Map Location",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Text(
                            text = if (originName.isNotBlank()) originName else currentMapLocation,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // 2. Overlapping Search and Preference Panel Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-40).dp)
                        .shadow(12.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Plan Your Journey",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Compare transit choices and calculate best route scores",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )

                        // Select Travel Group (Dropdown)
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = selectedGroup?.name ?: "Select a Travel Group",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Active Travel Group") },
                                leadingIcon = { Icon(Icons.Filled.Group, contentDescription = "Group") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                uiState.groups.forEach { group ->
                                    DropdownMenuItem(
                                        text = { Text(group.name) },
                                        onClick = {
                                            viewModel.selectGroup(group.id)
                                            expanded = false
                                        }
                                    )
                                }
                                if (uiState.groups.isEmpty()) {
                                    DropdownMenuItem(
                                        text = { Text("Create a group in Groups tab first!") },
                                        onClick = { expanded = false }
                                    )
                                }
                            }
                        }

                        // Search Inputs (Origin & Destination)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = originName,
                                    onValueChange = { newValue ->
                                        originName = newValue
                                        val currentSelectedName = lastSelectedOrigin?.let { 
                                            it.name + if (it.description.isNotEmpty()) ", ${it.description}" else "" 
                                        }
                                        if (newValue != currentSelectedName) {
                                            originLat = ""
                                            originLon = ""
                                            lastSelectedOrigin = null
                                        }
                                    },
                                    label = { Text("Start Location") },
                                    placeholder = { Text("e.g. Berlin") },
                                    leadingIcon = { Icon(Icons.Filled.MyLocation, contentDescription = "From", tint = MaterialTheme.colorScheme.primary) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )
                                
                                DropdownMenu(
                                    expanded = showOriginSuggestions && originSuggestions.isNotEmpty(),
                                    onDismissRequest = { showOriginSuggestions = false },
                                    properties = androidx.compose.ui.window.PopupProperties(focusable = false),
                                    modifier = Modifier
                                        .fillMaxWidth(0.9f)
                                        .background(MaterialTheme.colorScheme.surface)
                                ) {
                                    originSuggestions.forEach { suggestion ->
                                        DropdownMenuItem(
                                            text = {
                                                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                                    Text(
                                                        text = suggestion.name,
                                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    if (suggestion.description.isNotEmpty()) {
                                                        Text(
                                                            text = suggestion.description,
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                                        )
                                                    }
                                                }
                                            },
                                            onClick = {
                                                val displayName = suggestion.name + if (suggestion.description.isNotEmpty()) ", ${suggestion.description}" else ""
                                                lastSelectedOrigin = suggestion
                                                originName = displayName
                                                originLat = suggestion.lat.toString()
                                                originLon = suggestion.lon.toString()
                                                showOriginSuggestions = false
                                                
                                                try {
                                                    mapView?.controller?.animateTo(GeoPoint(suggestion.lat, suggestion.lon))
                                                    mapView?.controller?.setZoom(13.0)
                                                } catch (e: Exception) {
                                                    Log.e(TAG, "Failed to center map: ${e.message}")
                                                }
                                            }
                                        )
                                    }
                                }
                            }

                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = destName,
                                    onValueChange = { newValue ->
                                        destName = newValue
                                        val currentSelectedName = lastSelectedDest?.let { 
                                            it.name + if (it.description.isNotEmpty()) ", ${it.description}" else "" 
                                        }
                                        if (newValue != currentSelectedName) {
                                            destLat = ""
                                            destLon = ""
                                            lastSelectedDest = null
                                        }
                                    },
                                    label = { Text("Destination") },
                                    placeholder = { Text("e.g. Paris") },
                                    leadingIcon = { Icon(Icons.Filled.Place, contentDescription = "To", tint = MaterialTheme.colorScheme.secondary) },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true
                                )
                                
                                DropdownMenu(
                                    expanded = showDestSuggestions && destSuggestions.isNotEmpty(),
                                    onDismissRequest = { showDestSuggestions = false },
                                    properties = androidx.compose.ui.window.PopupProperties(focusable = false),
                                    modifier = Modifier
                                        .fillMaxWidth(0.9f)
                                        .background(MaterialTheme.colorScheme.surface)
                                ) {
                                    destSuggestions.forEach { suggestion ->
                                        DropdownMenuItem(
                                            text = {
                                                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                                    Text(
                                                        text = suggestion.name,
                                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    if (suggestion.description.isNotEmpty()) {
                                                        Text(
                                                            text = suggestion.description,
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                                        )
                                                    }
                                                }
                                            },
                                            onClick = {
                                                val displayName = suggestion.name + if (suggestion.description.isNotEmpty()) ", ${suggestion.description}" else ""
                                                lastSelectedDest = suggestion
                                                destName = displayName
                                                destLat = suggestion.lat.toString()
                                                destLon = suggestion.lon.toString()
                                                showDestSuggestions = false
                                                
                                                try {
                                                    mapView?.controller?.animateTo(GeoPoint(suggestion.lat, suggestion.lon))
                                                    mapView?.controller?.setZoom(13.0)
                                                } catch (e: Exception) {
                                                    Log.e(TAG, "Failed to center map: ${e.message}")
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Current Location Button trigger
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                if (locationPermissionGranted) {
                                    fetchCurrentLocation(context, fusedLocationClient, coroutineScope) { lat, lon, addressName ->
                                        originLat = lat.toString()
                                        originLon = lon.toString()
                                        originName = addressName
                                    }
                                } else {
                                    permissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Navigation,
                                contentDescription = "Current Location",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Use current location",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Preference Toggles ("What matters most?")
                        Column {
                            Text(
                                text = "What matters most?",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val preferences = listOf(
                                    Triple("Fastest", Icons.Filled.Speed, MaterialTheme.colorScheme.primary),
                                    Triple("Cheapest", Icons.Filled.AttachMoney, Color(0xFF22C55E)), // Green
                                    Triple("Balanced", Icons.Filled.Favorite, MaterialTheme.colorScheme.secondary)
                                )

                                preferences.forEach { (pref, icon, color) ->
                                    val isSelected = selectedPreference == pref
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (isSelected) color.copy(alpha = 0.08f)
                                                else MaterialTheme.colorScheme.surface
                                            )
                                            .border(
                                                width = if (isSelected) 2.dp else 1.dp,
                                                color = if (isSelected) color else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clickable { selectedPreference = pref }
                                            .padding(vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = pref,
                                                tint = if (isSelected) color else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = pref,
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Search on Map Button (works WITHOUT group selection — just geocodes and shows on map)
                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    isGeocodingLocal = true
                                    geocodingErrorLocal = null
                                    Log.d(TAG, "Search on Map clicked. origin='$originName', dest='$destName'")
                                    
                                    // Geocode origin if user typed something and we don't have coords yet
                                    if (originName.isNotBlank()) {
                                        val currentLat = originLat.toDoubleOrNull()
                                        val currentLon = originLon.toDoubleOrNull()
                                        if (currentLat == null || currentLon == null) {
                                            val startPoint = geocodeAddress(context, originName)
                                            if (startPoint != null) {
                                                Log.d(TAG, "Origin geocoded: ${startPoint.latitude}, ${startPoint.longitude}")
                                                originLat = startPoint.latitude.toString()
                                                originLon = startPoint.longitude.toString()
                                            } else {
                                                Log.w(TAG, "Failed to geocode origin: $originName")
                                                geocodingErrorLocal = "Could not find: $originName. Check spelling."
                                            }
                                        }
                                    }
                                    
                                    // Geocode destination if user typed something and we don't have coords yet
                                    if (destName.isNotBlank()) {
                                        val currentLat = destLat.toDoubleOrNull()
                                        val currentLon = destLon.toDoubleOrNull()
                                        if (currentLat == null || currentLon == null) {
                                            val endPoint = geocodeAddress(context, destName)
                                            if (endPoint != null) {
                                                Log.d(TAG, "Destination geocoded: ${endPoint.latitude}, ${endPoint.longitude}")
                                                destLat = endPoint.latitude.toString()
                                                destLon = endPoint.longitude.toString()
                                            } else {
                                                Log.w(TAG, "Failed to geocode destination: $destName")
                                                geocodingErrorLocal = (geocodingErrorLocal ?: "") + " Could not find: $destName. Check spelling."
                                            }
                                        }
                                    }
                                    
                                    isGeocodingLocal = false
                                }
                            },
                            enabled = (originName.isNotBlank() || destName.isNotBlank()) && !isGeocodingLocal,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isGeocodingLocal) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Finding locations...", fontSize = 14.sp)
                            } else {
                                Icon(Icons.Filled.Search, contentDescription = "Search", modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Search on Map",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        // Compare button (requires group selection for route API call)
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    isGeocodingLocal = true
                                    geocodingErrorLocal = null
                                    Log.d(TAG, "Compare clicked. origin='$originName', dest='$destName'")
                                    
                                    // Re-geocode if needed
                                    var startLat = originLat.toDoubleOrNull()
                                    var startLon = originLon.toDoubleOrNull()
                                    var endLat = destLat.toDoubleOrNull()
                                    var endLon = destLon.toDoubleOrNull()
                                    
                                    if ((startLat == null || startLon == null) && originName.isNotBlank()) {
                                        val startPoint = geocodeAddress(context, originName)
                                        if (startPoint != null) {
                                            startLat = startPoint.latitude
                                            startLon = startPoint.longitude
                                            originLat = startLat.toString()
                                            originLon = startLon.toString()
                                        }
                                    }
                                    
                                    if ((endLat == null || endLon == null) && destName.isNotBlank()) {
                                        val endPoint = geocodeAddress(context, destName)
                                        if (endPoint != null) {
                                            endLat = endPoint.latitude
                                            endLon = endPoint.longitude
                                            destLat = endLat.toString()
                                            destLon = endLon.toString()
                                        }
                                    }
                                    
                                    if (startLat != null && startLon != null && endLat != null && endLon != null) {
                                        Log.d(TAG, "Calling searchRoute: ($startLat,$startLon) -> ($endLat,$endLon)")
                                        viewModel.searchRoute(startLat, startLon, endLat, endLon, originName, destName)
                                    } else {
                                        geocodingErrorLocal = "Could not resolve both locations. Please enter valid city/address names and try 'Search on Map' first."
                                    }
                                    isGeocodingLocal = false
                                }
                            },
                            enabled = selectedGroup != null && originName.isNotBlank() && destName.isNotBlank() && !uiState.isLoading && !isGeocodingLocal,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    "Compare Transportation Options",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }

                        if (selectedGroup == null) {
                            Text(
                                text = "↑ Select a travel group above to compare routes",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        val currentError = uiState.errorMessage ?: geocodingErrorLocal
                        currentError?.let {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                            ) {
                                Text(
                                    text = it,
                                    modifier = Modifier.padding(12.dp),
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Results Section (Origin -> Destination comparisons)
        if (showResults) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .offset(y = (-20).dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Origin",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                                Text(
                                    text = originName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "To",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = "Destination",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                                Text(
                                    text = destName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }

                    Text(
                        text = "Available Transportation Routes",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            // Options List
            items(uiState.routes) { option ->
                // Identify if recommended based on the active preference tab
                val isRecommended = when (selectedPreference) {
                    "Cheapest" -> uiState.routes.minByOrNull { it.costPln } == option
                    "Fastest" -> uiState.routes.minByOrNull { it.durationMinutes } == option
                    else -> uiState.routes.minByOrNull { it.costPln + it.timeValueScore } == option
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .offset(y = (-20).dp)
                ) {
                    PremiumTransportOptionCard(
                        option = option,
                        isRecommended = isRecommended,
                        originName = originName,
                        destName = destName,
                        onSelect = {
                            if (selectedGroup != null && selectedGroup.memberNames.isNotEmpty()) {
                                pendingSelectedOption = option
                                showPayerDialog = true
                            } else {
                                viewModel.saveTrip(originName, destName, option, "")
                            }
                        }
                    )
                }
            }
        }

        // 4. "Why GroupGo?" Features Grid (Bottom Section)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Why GroupGo?",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                val features = listOf(
                    Triple(Icons.AutoMirrored.Filled.CompareArrows, "Smart Comparison", "Compare travel time & true cost values side-by-side."),
                    Triple(Icons.AutoMirrored.Filled.AltRoute, "Optimal Splitting", "Divide group transit charges cleanly and automatically."),
                    Triple(Icons.Filled.Bolt, "True Value Optimization", "Choose options scored on your precise salary time-values.")
                )

                features.forEach { (icon, title, desc) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = title,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = desc,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showPayerDialog && pendingSelectedOption != null) {
        val option = pendingSelectedOption!!
        val members = selectedGroup?.memberNames ?: emptyList()
        var selectedPayer by remember { mutableStateOf(members.firstOrNull() ?: "") }
        val (currencySymbol, exchangeRate) = remember(originName, destName) {
            getDisplayCurrency(if (destName.isNotBlank()) destName else originName)
        }
        val displayCost = option.costPln * exchangeRate
        val formattedCost = String.format(Locale.getDefault(), "%.2f %s", displayCost, currencySymbol)

        AlertDialog(
            onDismissRequest = {
                showPayerDialog = false
                pendingSelectedOption = null
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        val icon = when (option.mode.lowercase()) {
                            "car", "ride-share" -> Icons.Filled.DirectionsCar
                            "taxi" -> Icons.Filled.LocalTaxi
                            "bus", "transit", "train" -> Icons.Filled.DirectionsTransit
                            "flight" -> Icons.Filled.LocalAirport
                            "bike" -> Icons.Filled.DirectionsBike
                            else -> Icons.AutoMirrored.Filled.DirectionsWalk
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = option.mode,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Who paid for this trip?",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Trip Cost: $formattedCost",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(members) { member ->
                        val isSelected = member == selectedPayer
                        val avatarBgColor = remember(member) {
                            val hash = member.hashCode()
                            val colors = listOf(
                                Color(0xFF3B82F6), Color(0xFF10B981), Color(0xFF8B5CF6),
                                Color(0xFFEF4444), Color(0xFFF59E0B), Color(0xFFEC4899)
                            )
                            colors[kotlin.math.abs(hash) % colors.size]
                        }
                        val initial = member.firstOrNull()?.uppercaseChar()?.toString() ?: "?"

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPayer = member },
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.04f) else Color.Transparent
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(avatarBgColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = initial,
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = member,
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedPayer = member },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (selectedPayer.isNotEmpty()) {
                            viewModel.saveTrip(originName, destName, option, selectedPayer)
                        } else {
                            viewModel.saveTrip(originName, destName, option, "")
                        }
                        showPayerDialog = false
                        pendingSelectedOption = null
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPayerDialog = false
                        pendingSelectedOption = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

fun getDisplayCurrency(address: String): Pair<String, Double> {
    val normalized = address.lowercase()
    if (normalized.contains("spain") || normalized.contains("barcelona") || normalized.contains("madrid") ||
        normalized.contains("germany") || normalized.contains("berlin") ||
        normalized.contains("france") || normalized.contains("paris") || normalized.contains("italy") || normalized.contains("rome")
    ) {
        return Pair("€", 1.0 / 4.3) // 1 PLN = 0.23 EUR
    }
    if (normalized.contains("united kingdom") || normalized.contains("london") || normalized.contains("uk") || normalized.contains("gbp")) {
        return Pair("£", 1.0 / 5.0)
    }
    if (normalized.contains("united states") || normalized.contains("us") || normalized.contains("new york") || normalized.contains("usd")) {
        return Pair("$", 1.0 / 4.0)
    }
    return Pair("zł", 1.0)
}

@Composable
fun PremiumTransportOptionCard(
    option: TransportOption,
    isRecommended: Boolean,
    originName: String = "",
    destName: String = "",
    onSelect: () -> Unit
) {
    val (currencySymbol, exchangeRate) = remember(originName, destName) {
        getDisplayCurrency(if (destName.isNotBlank()) destName else originName)
    }
    val displayCost = option.costPln * exchangeRate

    // Mode specific metadata
    val (modeIcon, iconBg, description) = when (option.mode.lowercase()) {
        "car", "ride-share" -> Triple(
            Icons.Filled.DirectionsCar,
            Brush.linearGradient(colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)),
            "Door-to-door comfort and privacy"
        )
        "taxi" -> Triple(
            Icons.Filled.LocalTaxi,
            Brush.linearGradient(colors = listOf(Color(0xFFEAB308), Color(0xFFCA8A04))),
            "Fast personal door-to-door service"
        )
        "bus", "transit", "train" -> Triple(
            Icons.Filled.DirectionsTransit,
            Brush.linearGradient(colors = listOf(Color(0xFF3B82F6), Color(0xFF06B6D4))),
            "Affordable green group transit"
        )
        "flight" -> Triple(
            Icons.Filled.LocalAirport,
            Brush.linearGradient(colors = listOf(Color(0xFF8B5CF6), Color(0xFFEC4899))),
            "Fast long-distance travel"
        )
        "bike" -> Triple(
            Icons.Filled.DirectionsBike,
            Brush.linearGradient(colors = listOf(Color(0xFF10B981), Color(0xFF059669))),
            "Eco-friendly active transport"
        )
        else -> Triple(
            Icons.AutoMirrored.Filled.DirectionsWalk,
            Brush.linearGradient(colors = listOf(Color(0xFF6B7280), Color(0xFF4B5563))),
            "Healthy and zero carbon emissions"
        )
    }

    val mockCo2 = when (option.mode.lowercase()) {
        "car", "ride-share" -> "245g CO₂"
        "taxi" -> "255g CO₂"
        "bus", "transit", "train" -> "85g CO₂"
        "flight" -> "190g CO₂"
        else -> "0g CO₂"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isRecommended) Modifier.border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) else Modifier
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isRecommended) MaterialTheme.colorScheme.primary.copy(alpha = 0.03f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isRecommended) 6.dp else 2.dp)
    ) {
        Column {
            // Recommendation banner
            if (isRecommended) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        )
                        .padding(vertical = 6.dp, horizontal = 16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Recommended",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Recommended for Optimal Time-Value Score",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Header details
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(iconBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = modeIcon,
                                contentDescription = option.mode,
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = option.mode.uppercase(),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }

                    Text(
                        text = String.format(Locale.getDefault(), "%.1f km", option.distanceKm),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Triple metrics container grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Duration
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.AccessTime,
                                    contentDescription = "Time",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${option.durationMinutes}m",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Duration",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }

                    // Cost
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.AttachMoney,
                                    contentDescription = "Cost",
                                    tint = Color(0xFF22C55E),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = if (option.costPln == 0.0) "Free" else String.format(Locale.getDefault(), "%.2f %s", displayCost, currencySymbol),
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Cost",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }

                    // CO2
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Co2,
                                    contentDescription = "CO2",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = mockCo2,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Emissions",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))

                Spacer(modifier = Modifier.height(12.dp))

                // Score + Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Time-Value Score",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Text(
                            text = String.format(Locale.getDefault(), "%.2f %s", option.timeValueScore * exchangeRate, currencySymbol),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Button(
                        onClick = onSelect,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (isRecommended) "Choose Recommended" else "Select Route",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

// ── Geocoding & GPS Location Helper Functions ────────────────────────────────
private suspend fun geocodeAddress(context: Context, address: String): GeoPoint? = withContext(Dispatchers.IO) {
    if (address.isBlank()) return@withContext null
    Log.d(TAG, "geocodeAddress: Starting geocoding for '$address'")
    
    // 1. Try system Geocoder first
    try {
        if (Geocoder.isPresent()) {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocationName(address, 1)
            if (!addresses.isNullOrEmpty()) {
                val location = addresses[0]
                Log.d(TAG, "geocodeAddress: System geocoder found: ${location.latitude}, ${location.longitude}")
                return@withContext GeoPoint(location.latitude, location.longitude)
            } else {
                Log.d(TAG, "geocodeAddress: System geocoder returned empty results")
            }
        } else {
            Log.d(TAG, "geocodeAddress: System Geocoder not present on this device")
        }
    } catch (e: Exception) {
        Log.w(TAG, "geocodeAddress: System geocoder failed: ${e.message}")
    }
    
    // 2. Fallback to OpenStreetMap Nominatim Web API (completely free, no key required)
    try {
        val encodedAddress = java.net.URLEncoder.encode(address, "UTF-8")
        val urlString = "https://nominatim.openstreetmap.org/search?q=$encodedAddress&format=json&limit=1"
        Log.d(TAG, "geocodeAddress: Nominatim request: $urlString")
        val connection = java.net.URL(urlString).openConnection() as java.net.HttpURLConnection
        connection.connectTimeout = 10000
        connection.readTimeout = 10000
        connection.setRequestProperty("User-Agent", "GroupGo/1.0 (Android; com.example.groupgo)")
        connection.setRequestProperty("Accept", "application/json")
        
        val responseCode = connection.responseCode
        Log.d(TAG, "geocodeAddress: Nominatim response code: $responseCode")
        
        if (responseCode == 200) {
            val jsonText = connection.inputStream.bufferedReader().use { it.readText() }
            Log.d(TAG, "geocodeAddress: Nominatim response: ${jsonText.take(500)}")
            val jsonArray = JSONArray(jsonText)
            if (jsonArray.length() > 0) {
                val obj = jsonArray.getJSONObject(0)
                val lat = obj.getDouble("lat")
                val lon = obj.getDouble("lon")
                Log.d(TAG, "geocodeAddress: Nominatim found: $lat, $lon")
                return@withContext GeoPoint(lat, lon)
            } else {
                Log.w(TAG, "geocodeAddress: Nominatim returned empty array")
            }
        } else {
            val errorText = try { connection.errorStream?.bufferedReader()?.readText() } catch (_: Exception) { "n/a" }
            Log.e(TAG, "geocodeAddress: Nominatim HTTP error $responseCode: $errorText")
        }
    } catch (e: Exception) {
        Log.e(TAG, "geocodeAddress: Nominatim request failed: ${e.message}", e)
    }
    
    Log.w(TAG, "geocodeAddress: All geocoding methods failed for '$address'")
    null
}

private suspend fun reverseGeocodeLocation(context: Context, lat: Double, lon: Double): String = withContext(Dispatchers.IO) {
    // 1. Try system Geocoder first
    try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(lat, lon, 1)
        if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            val cityName = address.locality ?: address.subAdminArea ?: address.adminArea ?: "My Location"
            val thoroughfare = address.thoroughfare
            return@withContext if (thoroughfare != null) "$thoroughfare, $cityName" else cityName
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    
    // 2. Fallback to OpenStreetMap Nominatim Web API (completely free, no key required)
    try {
        val urlString = "https://nominatim.openstreetmap.org/reverse?lat=$lat&lon=$lon&format=json"
        val connection = java.net.URL(urlString).openConnection() as java.net.HttpURLConnection
        connection.connectTimeout = 5000
        connection.readTimeout = 5000
        connection.setRequestProperty("User-Agent", "GroupGo/1.0 (Android; com.example.groupgo)")
        
        if (connection.responseCode == 200) {
            val jsonText = connection.inputStream.bufferedReader().use { it.readText() }
            val obj = JSONObject(jsonText)
            val displayName = obj.optString("display_name")
            val addressObj = obj.optJSONObject("address")
            if (addressObj != null) {
                val city = addressObj.optString("city", addressObj.optString("town", addressObj.optString("village", "")))
                val road = addressObj.optString("road", "")
                if (city.isNotEmpty() && road.isNotEmpty()) {
                    return@withContext "$road, $city"
                } else if (city.isNotEmpty()) {
                    return@withContext city
                }
            }
            if (!displayName.isNullOrBlank()) {
                val segments = displayName.split(",")
                if (segments.size >= 2) {
                    return@withContext "${segments[0].trim()}, ${segments[1].trim()}"
                }
                return@withContext displayName
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    
    "Coordinates: ${String.format(Locale.US, "%.4f, %.4f", lat, lon)}"
}

private fun fetchCurrentLocation(
    context: Context,
    fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    onLocationFetched: (Double, Double, String) -> Unit
) {
    Log.d(TAG, "fetchCurrentLocation: Starting GPS location request")
    try {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            // Request fresh current location with HIGH accuracy first (bulletproof on emulators/devices)
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        Log.d(TAG, "fetchCurrentLocation: Got GPS fix: ${location.latitude}, ${location.longitude}")
                        coroutineScope.launch {
                            val addressName = reverseGeocodeLocation(context, location.latitude, location.longitude)
                            Log.d(TAG, "fetchCurrentLocation: Reverse geocoded to: $addressName")
                            onLocationFetched(location.latitude, location.longitude, addressName)
                        }
                    } else {
                        Log.w(TAG, "fetchCurrentLocation: getCurrentLocation returned null, trying lastLocation")
                        // Fallback to last known location as secondary measure
                        fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                            if (lastLoc != null) {
                                Log.d(TAG, "fetchCurrentLocation: Got lastLocation: ${lastLoc.latitude}, ${lastLoc.longitude}")
                                coroutineScope.launch {
                                    val addressName = reverseGeocodeLocation(context, lastLoc.latitude, lastLoc.longitude)
                                    onLocationFetched(lastLoc.latitude, lastLoc.longitude, addressName)
                                }
                            } else {
                                Log.e(TAG, "fetchCurrentLocation: lastLocation is also null")
                                android.widget.Toast.makeText(context, "GPS Location not found. Please verify location is enabled in settings.", android.widget.Toast.LENGTH_LONG).show()
                            }
                        }.addOnFailureListener {
                            Log.e(TAG, "fetchCurrentLocation: lastLocation failed: ${it.message}")
                            android.widget.Toast.makeText(context, "GPS Location is null and last location failed.", android.widget.Toast.LENGTH_LONG).show()
                        }
                    }
                }
                .addOnFailureListener {
                    Log.e(TAG, "fetchCurrentLocation: getCurrentLocation failed: ${it.message}")
                    // Failback to last known location if getCurrentLocation fails
                    fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                        if (lastLoc != null) {
                            coroutineScope.launch {
                                val addressName = reverseGeocodeLocation(context, lastLoc.latitude, lastLoc.longitude)
                                onLocationFetched(lastLoc.latitude, lastLoc.longitude, addressName)
                            }
                        } else {
                            android.widget.Toast.makeText(context, "GPS Query failed and last location is null: ${it.localizedMessage}", android.widget.Toast.LENGTH_LONG).show()
                        }
                    }
                }
        } else {
            Log.e(TAG, "fetchCurrentLocation: No location permission granted")
        }
    } catch (e: SecurityException) {
        Log.e(TAG, "fetchCurrentLocation: SecurityException: ${e.message}")
    }
}

data class PlaceSuggestion(
    val name: String,
    val description: String,
    val lat: Double,
    val lon: Double
)

private suspend fun fetchPlaceSuggestions(query: String): List<PlaceSuggestion> = withContext(Dispatchers.IO) {
    if (query.trim().length < 3) return@withContext emptyList()
    try {
        val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
        val urlString = "https://photon.komoot.io/api/?q=$encodedQuery&limit=5"
        Log.d(TAG, "fetchPlaceSuggestions: Photon request: $urlString")
        val connection = java.net.URL(urlString).openConnection() as java.net.HttpURLConnection
        connection.connectTimeout = 5000
        connection.readTimeout = 5000
        connection.setRequestProperty("User-Agent", "GroupGo/1.0 (Android; com.example.groupgo)")
        connection.setRequestProperty("Accept", "application/json")
        
        val responseCode = connection.responseCode
        if (responseCode == 200) {
            val jsonText = connection.inputStream.bufferedReader().use { it.readText() }
            val root = JSONObject(jsonText)
            val features = root.optJSONArray("features") ?: return@withContext emptyList()
            val list = mutableListOf<PlaceSuggestion>()
            for (i in 0 until features.length()) {
                val feature = features.getJSONObject(i)
                val geometry = feature.getJSONObject("geometry")
                val coordinates = geometry.getJSONArray("coordinates")
                val lon = coordinates.getDouble(0)
                val lat = coordinates.getDouble(1)
                
                val properties = feature.getJSONObject("properties")
                val name = properties.optString("name", "")
                
                val details = mutableListOf<String>()
                val city = properties.optString("city", "")
                if (city.isNotEmpty() && city != name) details.add(city)
                val state = properties.optString("state", "")
                if (state.isNotEmpty() && state != name && state != city) details.add(state)
                val country = properties.optString("country", "")
                if (country.isNotEmpty()) details.add(country)
                
                val description = details.joinToString(", ")
                if (name.isNotEmpty()) {
                    list.add(PlaceSuggestion(name = name, description = description, lat = lat, lon = lon))
                }
            }
            return@withContext list
        } else {
            Log.e(TAG, "fetchPlaceSuggestions HTTP error $responseCode")
        }
    } catch (e: Exception) {
        Log.e(TAG, "fetchPlaceSuggestions failed: ${e.message}", e)
    }
    emptyList()
}

