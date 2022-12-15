package ui

import settings.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.io.File

@Composable
fun MainPaneActionRow(
    appSettings: Settings,
    filenames: MutableList<File>,
    keystrokeDelay: String,
    onKeystrokeDelayChange: (String) -> Unit,
    applyKeystrokes: () -> Unit,
    isTyping: Boolean,
    numbersOnlyPattern: Regex,
) {

    Row(
        modifier = Modifier.padding(bottom = 2.dp, top = 4.dp, start = 4.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Conditional(
            appSettings.applyStrokesButtonVisible.value,
            @Composable {
                Conditional(
                    !isTyping,
                    @Composable {
                        Button(onClick = applyKeystrokes) {
                            Text("Apply keystrokes")
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    },
                    @Composable {
                        Text("Applying keystrokes...")
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                )
            }
        )
        Conditional(
            appSettings.typingDelayFieldVisible.value,
            @Composable {
                val fw = !appSettings.ocrImagesTextFieldVisible.value
                TextField(
                    modifier = Modifier.doIf(fw, { fillMaxWidth() }, { width(115.dp) }),
                    onValueChange = { newText ->
                        if (newText.isNotEmpty() && newText.matches(numbersOnlyPattern) && !newText.startsWith("0")) {
                            onKeystrokeDelayChange(newText)
                        }
                    },
                    singleLine = true,
                    value = keystrokeDelay,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
        )
        Conditional(condition = appSettings.ocrImagesTextFieldVisible.value, ifTrue = @Composable {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                label = { Text("OCR Images") },
                value = filenames.joinToString(", "),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        })
    }
}

@Composable
fun Conditional(condition: Boolean, ifTrue: @Composable () -> Unit, ifFalse: @Composable () -> Unit = {}) {
    return if (condition) {
        ifTrue()
    } else {
        ifFalse()
    }
}
