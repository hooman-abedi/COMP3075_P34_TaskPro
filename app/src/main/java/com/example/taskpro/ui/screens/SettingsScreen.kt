@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.taskpro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.taskpro.ui.components.*

/** Settings: name, dark theme (FR-11), reminders (FR-10), demo data, about. */
@Composable
fun SettingsScreen(
    studentName: String,
    darkTheme: Boolean,
    remindersEnabled: Boolean,
    onNameChange: (String) -> Unit,
    onDarkThemeChange: (Boolean) -> Unit,
    onRemindersChange: (Boolean) -> Unit,
    onLoadSampleData: () -> Unit,
    onClearData: () -> Unit
) {
    var name by remember(studentName) { mutableStateOf(studentName) }
    var confirmClear by remember { mutableStateOf(false) }
    var sampleLoaded by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PageTitle("Settings", "Make TaskPro work for you")

        SettingsCard {
            Column(Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it.take(30) },
                    label = { Text("Your name (shown on the dashboard)") },
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                TextButton(
                    onClick = { onNameChange(name.trim().ifBlank { "Student" }) },
                    enabled = name.trim() != studentName,
                    modifier = Modifier.align(Alignment.End)
                ) { Text("Save name") }
            }
        }

        SettingsCard {
            Column {
                SwitchRow(Icons.Filled.DarkMode, "Dark theme", "Easier on the eyes at night", darkTheme, onDarkThemeChange)
                HorizontalDivider()
                SwitchRow(Icons.Filled.Notifications, "Deadline reminders",
                    "Notify me before tasks are due", remindersEnabled, onRemindersChange)
            }
        }

        SettingsCard {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Data", fontWeight = FontWeight.Bold)
                Text("Everything is stored on this device (Room database) and works offline.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                OutlinedButton(
                    onClick = { onLoadSampleData(); sampleLoaded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Download, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (sampleLoaded) "Sample data added" else "Load sample courses & tasks")
                }
                OutlinedButton(
                    onClick = { confirmClear = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Filled.DeleteForever, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Delete all courses & tasks")
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("TaskPro – Student Planner", style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text("COMP3074 Final Project • Team P34 • Version 1.0",
                    color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text(
                    "Martins Maduabuchi, Hooman Abedi, Bak Aghedo, Winthrop Oviosun",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }

    if (confirmClear) {
        ConfirmDialog(
            title = "Delete everything?",
            message = "All courses and tasks will be removed from this device.",
            confirmLabel = "Delete all",
            onConfirm = { onClearData(); sampleLoaded = false },
            onDismiss = { confirmClear = false }
        )
    }
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) { content() }
}

@Composable
private fun SwitchRow(
    icon: ImageVector, title: String, subtitle: String,
    checked: Boolean, onChecked: (Boolean) -> Unit
) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall)
        }
        Switch(checked = checked, onCheckedChange = onChecked)
    }
}
