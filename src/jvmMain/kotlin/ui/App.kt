package ui

import Settings
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogState
import java.io.File

@Composable
@Preview
fun MainWindow(
    appSettings: Settings,
    filenames: MutableList<File>,
    currentTab: Int,
    onTabChange: (Int) -> Unit,
    addTabChange: () -> Unit,
    removeTabChange: () -> Unit,
    texts: Map<Int, String>,
    onTextChange: (String) -> Unit,
    keystrokeDelay: String,
    onKeystrokeDelayChange: (String) -> Unit,
    applyKeystrokes: () -> Unit,
    scrollState: ScrollState,
    isTyping: Boolean,
    showDialog: Boolean,
    onShowDialogChange: (Boolean) -> Unit,
) {
    val mainTextFieldDescription = """
        OCR will appear here. You can start key stroke events by clicking 'Apply keystrokes'.
        You can also change the delay before keystroke events are applied with "Typing delay". The delay is in seconds.
        Drag and drop images here to convert the contents of the image to text.
        """.trimIndent()

    MaterialTheme {
        Scaffold(floatingActionButton = {
            Row(modifier = Modifier.padding(4.dp)) {
                FloatingActionButton(onClick = addTabChange) {
                    Text("+")
                }
                Spacer(modifier = Modifier.width(4.dp))
                FloatingActionButton(onClick = removeTabChange) {
                    Text("-")
                }
            }
        }) {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                ActionRow(
                    appSettings = appSettings,
                    filenames = filenames,
                    keystrokeDelay = keystrokeDelay,
                    onKeystrokeDelayChange = onKeystrokeDelayChange,
                    applyKeystrokes = applyKeystrokes,
                    isTyping = isTyping
                )
                TabRow(texts, currentTab, scrollState) { onTabChange(it) }
                if (appSettings.mainTextFieldVisible.value) {
                    TextField(modifier = Modifier.fillMaxSize(),
                        value = texts[currentTab] ?: "",
                        onValueChange = onTextChange,
                        placeholder = { Text(text = mainTextFieldDescription) })
                }
            }

        }
        Dialog(
            title = "Tesseract - Missing dependency",
            state = DialogState(size = DpSize(400.dp, 125.dp)),
            resizable = false,
            visible = showDialog, onCloseRequest = { onShowDialogChange(false) }
        ) {
            Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text(modifier = Modifier.fillMaxWidth(), text = "You need tesseract OCR installed to use this feature.")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    onShowDialogChange(false);
                }) {
                    Text("Close")
                }
            }
        }
    }
}

fun Modifier.doIf(
    condition: Boolean, modifierTrue: Modifier.() -> Modifier, modifierFalse: Modifier.() -> Modifier
): Modifier {
    return if (condition) {
        then(modifierTrue(Modifier))
    } else {
        then(modifierFalse(Modifier))
    }
}

