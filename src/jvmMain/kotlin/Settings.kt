import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

// Settings to adjust the ui and behaviour of the ocr gui app.
// Includes:
// - Toggle save path text field visibility.
// - Toggle delay number field visibility.
// - Toggle primary text field for ocr and text to apply keystrokes of.
// - Toggle the visibility of the "Apply keystrokes" button.
data class Settings(
    var typingDelayFieldVisible: MutableState<Boolean> = mutableStateOf(true),
    var mainTextFieldVisible: MutableState<Boolean> = mutableStateOf(true),
    var applyStrokesButtonVisible: MutableState<Boolean> = mutableStateOf(true),
    var ocrImagesTextFieldVisible: MutableState<Boolean> = mutableStateOf(false),
)