package ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material.*
import settings.Settings

@Composable
fun MainPaneTextField(
    appSettings: Settings,
    currentTab: Int,
    texts: Map<Int, String>,
    onTextChange: (String) -> Unit,
    mainTextFieldDescription: String,
) {

    if (appSettings.mainTextFieldVisible.value) {
        TextField(modifier = Modifier.fillMaxSize(),
            value = texts[currentTab] ?: "",
            onValueChange = onTextChange,
            placeholder = { Text(text = mainTextFieldDescription) })
    }
}