
package ui
import Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.io.File

@Composable
fun ActionRow(
    appSettings: Settings,
    filenames: MutableList<File>,
    keystrokeDelay: String,
    onKeystrokeDelayChange: (String) -> Unit,
    applyKeystrokes: () -> Unit,
    isTyping: Boolean
) {
    val numbersOnlyPattern = Regex("^\\d+\$")

    Row(
        modifier = Modifier.padding(bottom = 2.dp, top =4.dp, start = 4.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (appSettings.applyStrokesButtonVisible.value && !isTyping) {
            Button(onClick = applyKeystrokes) {
                Text("Apply keystrokes")
            }
            Spacer(modifier = Modifier.width(12.dp))
        } else if (isTyping) {
            Text("Applying keystrokes...")
        }
        if (appSettings.typingDelayFieldVisible.value) {
            val fw = !appSettings.ocrImagesTextFieldVisible.value
            TextField(
                modifier = Modifier.doIf(fw, { fillMaxWidth() }, { width(115.dp) }),
                onValueChange = { newText ->
                    if (newText.isNotEmpty() && newText.matches(numbersOnlyPattern)) {
                        onKeystrokeDelayChange(newText)
                    }
                },
                singleLine = true,
                label = { Text("Typing delay") },
                value = keystrokeDelay,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.width(12.dp))
        }
        if (appSettings.ocrImagesTextFieldVisible.value) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                label = { Text("OCR Images") },
                value = filenames.joinToString(", "),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    }
}