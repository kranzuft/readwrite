package ui

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
import settings.Settings
import java.io.File

@Composable
@Preview
fun MainPane(
    appSettings: Settings,
    filenames: MutableList<File>,
    currentTab: Int,
    onTabChange: (Int) -> Unit,
    addTabChange: () -> Unit,
    removeTabChange: () -> Unit,
    textInTabs: Map<Int, String>,
    onTextChange: (String) -> Unit,
    keystrokeDelay: String,
    onKeystrokeDelayChange: (String) -> Unit,
    applyKeystrokes: () -> Unit,
    scrollState: ScrollState,
    isTyping: Boolean,
    showDialog: Boolean,
    onShowDialogChange: (Boolean) -> Unit,
    mainTextFieldDescription: String,
    numbersOnlyPattern : Regex,
) {

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
                MainPaneActionRow(appSettings, filenames, keystrokeDelay, onKeystrokeDelayChange, applyKeystrokes, isTyping, numbersOnlyPattern)
                TabRow(textInTabs, currentTab, scrollState, onTabChange)
                MainPaneTextField(appSettings, currentTab, textInTabs, onTextChange, mainTextFieldDescription)
            }

        }
        Dialog(title = "Tesseract - Missing dependency",
            state = DialogState(size = DpSize(400.dp, 125.dp)),
            resizable = false,
            visible = showDialog,
            onCloseRequest = { onShowDialogChange(false) }) {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
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

