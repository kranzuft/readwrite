package ui


import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import settings.Settings

@Preview
@Composable
fun SettingsPane(
    appSettings: Settings,
    tesseractPathOnChange: (String) -> Unit,
    defaultTypingDelayOnChange: (Int) -> Unit,
    rememberVisibilityOnChange: (Boolean) -> Unit,
    onBack: (Unit) -> Unit,
    numbersOnlyPattern: Regex,
) {
    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row() {
            Column() {
                Row() {
                    TextField(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        label = { Text("Custom Tesseract Path") },
                        value = appSettings.customTesseractToolFullPath.value,
                        onValueChange = tesseractPathOnChange
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row() {
                    TextField(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        label = { Text("Custom Default Type Delay") },
                        value = appSettings.customDefaultTypeDelay.value.toString(),
                        onValueChange = {
                            if (it.isNotEmpty() && it.matches(numbersOnlyPattern) && !it.startsWith("0")) {
                                defaultTypingDelayOnChange(it.toInt())
                            }
                        })
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.padding(8.dp).align(Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Remember visibility options")
                    Switch(
                        checked = appSettings.rememberVisibilityOptions.value,
                        onCheckedChange = rememberVisibilityOnChange
                    )
                }
            }
        }
        Row(verticalAlignment = Alignment.Bottom) {
            TextButton(onClick = { onBack(Unit) }) {
                Text("Back")
            }
        }
    }
}