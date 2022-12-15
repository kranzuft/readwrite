import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import process.KeyTyper
import process.TesseractOCR
import settings.SettingsManager
import ui.MainPane
import ui.SettingsPane
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetAdapter
import java.awt.dnd.DropTargetDropEvent
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService


fun main() = application {

    var currentTab by remember { mutableStateOf(1) }
    var showDialog by remember { mutableStateOf(false) }
    var typing by remember { mutableStateOf(false) }
    var inSettings by remember { mutableStateOf(false) }
    val texts = remember { mutableStateMapOf<Int, String>() }
    val tabScrollState = rememberScrollState(0)
    val settingsManager = SettingsManager()
    val loadedSettings =
        settingsManager.loadSettingsFromFile(System.getProperty("user.dir") + File.separator + "app.properties")
    val settings by remember { mutableStateOf(loadedSettings) }
    var keystrokeDelay by remember { mutableStateOf(settings.customDefaultTypeDelay.value.toString()) }

    val numbersOnlyPattern = Regex("^\\d+\$")

    if (texts.isEmpty()) {
        texts[currentTab] = ""
    }

    val ocr = TesseractOCR()
    val typer = KeyTyper()
    val iconRes = painterResource("icons/icon.png")
    val scheduler = Executors.newSingleThreadScheduledExecutor()

    val mainTextFieldDescription = """
        OCR will appear here. You can start key stroke events by clicking 'Apply keystrokes'.
        You can also change the delay before keystroke events are applied with "Typing delay". The delay is in seconds.
        Drag and drop images here to convert the contents of the image to text.
        """.trimIndent()

    Window(
        onCloseRequest = {
            settingsManager.storeSettingsToFile(
                settings,
                "app.properties"
            );
            this.exitApplication()
        },
        title = "ReadWrite", icon = iconRes
    ) {
        val filenames = remember { mutableStateListOf<File>() }
        if (!inSettings) {
            MainPane(
                appSettings = settings,
                filenames = filenames,
                textInTabs = texts,
                onTextChange = { texts[currentTab] = it },
                currentTab = currentTab,
                onTabChange = { currentTab = it },
                applyKeystrokes = { type(scheduler, typer, keystrokeDelay, texts, currentTab) { typing = it } },
                keystrokeDelay = keystrokeDelay,
                onKeystrokeDelayChange = { keystrokeDelay = it },
                addTabChange = {
                    texts[texts.size + 1] = ""
                    currentTab = texts.size
                },
                removeTabChange = {
                    if (texts.size > 1) {
                        for (i in currentTab until texts.size) {
                            texts[i] = texts[i + 1]!!
                        }
                        texts.remove(texts.size)
                        currentTab = texts.size
                    }
                },
                scrollState = tabScrollState,
                isTyping = typing,
                showDialog = showDialog,
                onShowDialogChange = { showDialog = it },
                mainTextFieldDescription = mainTextFieldDescription,
                numbersOnlyPattern = numbersOnlyPattern,
            )
        } else {
            SettingsPane(
                appSettings = settings,
                tesseractPathOnChange = { settings.customTesseractToolFullPath.value = it },
                defaultTypingDelayOnChange = { settings.customDefaultTypeDelay.value = it },
                rememberVisibilityOnChange = { settings.rememberVisibilityOptions.value = it },
                onBack = {
                    inSettings = false;
                    settingsManager.storeSettingsToFile(settings, "app.properties")
                },
                numbersOnlyPattern = numbersOnlyPattern,
            )
        }
        MenuBar {
            Menu("File") {
                Item("Open Image", onClick = {})
                Item("Save current", onClick = {})
                Item("Exit", onClick = {})
            }
            Menu("View") {
                CheckboxItem(text = "Show main text field",
                    checked = settings.mainTextFieldVisible.value,
                    onCheckedChange = { newCheckedState -> settings.mainTextFieldVisible.value = newCheckedState })
                CheckboxItem(text = "Show 'Apply keystrokes' button",
                    checked = settings.applyStrokesButtonVisible.value,
                    onCheckedChange = { newCheckedState -> settings.applyStrokesButtonVisible.value = newCheckedState })
                CheckboxItem(text = "Show 'Typing delay' text field",
                    checked = settings.typingDelayFieldVisible.value,
                    onCheckedChange = { newCheckedState -> settings.typingDelayFieldVisible.value = newCheckedState })
                CheckboxItem(text = "Show 'OCR Images' text field",
                    checked = settings.ocrImagesTextFieldVisible.value,
                    onCheckedChange = { newCheckedState -> settings.ocrImagesTextFieldVisible.value = newCheckedState })
                CheckboxItem(text = "Settings",
                    checked = inSettings,
                    onCheckedChange = {
                        inSettings = !inSettings
                    })
            }
        }
        LaunchedEffect(Unit) {
            window.dropTarget = DropTarget().apply {
                addDropTargetListener(object : DropTargetAdapter() {
                    override fun drop(event: DropTargetDropEvent) {
                        val tesseractOpt =
                            ocr.prepOCR(System.getProperty("user.dir"), settings.customTesseractToolFullPath.value)
                        if (tesseractOpt.isEmpty) {
                            showDialog = true
                        } else {
                            event.acceptDrop(DnDConstants.ACTION_COPY)
                            val ocrRes = StringBuilder()
                            val files = event.transferable.getTransferData(DataFlavor.javaFileListFlavor)
                            if (files is List<*>) {
                                if (files.isNotEmpty()) {
                                    filenames.clear()
                                }
                                for (file in files) {
                                    if (file is File) {
                                        ocrRes.append(ocr.doOCR(tesseractOpt.get(), file.absolutePath))
                                        filenames.add(file)
                                    }
                                }
                            }
                            texts[currentTab] = ocrRes.toString()
                            println("Dropped files: " + filenames.joinToString(", "))
                        }
                    }
                })
            }
        }
    }
}


fun type(
    scheduler: ScheduledExecutorService,
    typer: KeyTyper,
    keystrokeDelay: String,
    texts: Map<Int, String>,
    currentTab: Int,
    setTyping: (Boolean) -> Unit
) {
    setTyping(true)
    scheduler.schedule(
        { typer.type(texts[currentTab] ?: ""); setTyping(false) },
        keystrokeDelay.toLong(),
        java.util.concurrent.TimeUnit.SECONDS
    )
}