package com.example.gymtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gymtrack.ui.theme.GymTrackTheme
import java.text.SimpleDateFormat
import java.util.*

data class WorkoutDay(
    val dayName: String,
    val focus: String,
    val exercises: List<String>,
    val isCompleted: Boolean = false
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GymTrackTheme {
                GymApp()
            }
        }
    }
}

@Composable
fun GymApp() {
    val navController = rememberNavController()
    
    val sdf = SimpleDateFormat("EEEE", Locale("es", "ES"))
    val todayName = sdf.format(Date()).replaceFirstChar { it.uppercase() }

    var workouts by remember {
        mutableStateOf(
            listOf(
                WorkoutDay("Lunes", "Pecho y Tríceps", listOf("Press Banca 4x12", "Aperturas 3x15", "Extensiones 3x12")),
                WorkoutDay("Miércoles", "Espalda y Bíceps", listOf("Dominadas 4x10", "Remo con barra 4x12", "Curl Martillo 3x15")),
                WorkoutDay(todayName, "Rutina de Hoy", listOf("Sentadilla 4x10", "Press Militar 3x12", "Plancha 3x1min"))
            ).distinctBy { it.dayName } // Evita duplicados si hoy es Lunes o Miércoles
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                val todayWorkout = workouts.find { it.dayName == todayName } ?: workouts.last()
                HomeScreen(
                    navController = navController,
                    todayWorkout = todayWorkout,
                    onComplete = {
                        // 2. Hacer la completitud irreversible
                        workouts = workouts.map {
                            if (it.dayName == todayName) it.copy(isCompleted = true) else it
                        }
                    }
                )
            }
            composable("details") {
                DetailScreen(navController, workouts)
            }
        }
    }
}

@Composable
fun HomeScreen(
    navController: NavController,
    todayWorkout: WorkoutDay,
    onComplete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        

        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
            val primaryColor = MaterialTheme.colorScheme.primary
            val tertiaryColor = MaterialTheme.colorScheme.tertiary
            
            Canvas(modifier = Modifier.size(180.dp)) {
                drawArc(
                    color = Color.DarkGray.copy(alpha = 0.3f),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    color = if (todayWorkout.isCompleted) tertiaryColor else primaryColor,
                    startAngle = -90f,
                    sweepAngle = if (todayWorkout.isCompleted) 360f else 60f, // Simula progreso
                    useCenter = false,
                    style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (todayWorkout.isCompleted) "LISTO" else "PENDIENTE",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (todayWorkout.isCompleted) tertiaryColor else primaryColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = todayWorkout.dayName.uppercase(),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black
                )
            }
        }

        Text(
            text = todayWorkout.focus.uppercase(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { if (!todayWorkout.isCompleted) onComplete() },
            enabled = !todayWorkout.isCompleted,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.tertiary
            ),
            shape = if (todayWorkout.isCompleted) RoundedCornerShape(32.dp) else RoundedCornerShape(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {
            Icon(
                imageVector = if (todayWorkout.isCompleted) Icons.Default.CheckCircle else Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp),
                tint = if (todayWorkout.isCompleted) Color.White else Color.Black
            )
            Text(
                text = if (todayWorkout.isCompleted) "¡RUTINA COMPLETADA!" else "MARCAR COMO COMPLETADA",
                fontWeight = FontWeight.ExtraBold,
                color = if (todayWorkout.isCompleted) Color.White else Color.Black
            )
        }

        OutlinedButton(
            onClick = { navController.navigate("details") },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
        ) {
            Text(
                "VER EJERCICIOS SEMANALES",
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DetailScreen(navController: NavController, workouts: List<WorkoutDay>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "PLANIFICACIÓN SEMANAL",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(workouts) { workout ->
                WorkoutItem(workout)
            }
        }

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text("VOLVER AL INICIO", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun WorkoutItem(workout: WorkoutDay) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (workout.isCompleted) BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary) else null
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = workout.dayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (workout.isCompleted) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                )
                Text(
                    text = workout.focus,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                workout.exercises.forEach { ex ->
                    Text(
                        text = "• $ex",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
            
            if (workout.isCompleted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(32.dp)
                )
            } else {
                Text(
                    text = "PENDIENTE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                )
            }
        }
    }
}
