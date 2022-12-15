package settings

import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

class SettingsManager {
    fun loadSettingsFromFile(settingsFile : String): Settings {
        val props = Properties()
        val settingsPath = Paths.get(settingsFile)

        return if (Files.exists(settingsPath)) {
            props.load(FileInputStream(settingsPath.toFile()))
            // Load settings
            val settings = Settings()
            if (props.containsKey("customTesseractToolFullPath")) {
                settings.customTesseractToolFullPath.value = props.getProperty("customTesseractToolFullPath")
            }
            if (props.containsKey("customDefaultTypeDelay")) {
                settings.customDefaultTypeDelay.value = props.getProperty("customDefaultTypeDelay").toInt()
            }
            if (props.containsKey("typingDelayFieldVisible")) {
                settings.typingDelayFieldVisible.value = props.getProperty("typingDelayFieldVisible").toBoolean()
            }
            if (props.containsKey("mainTextFieldVisible")) {
                settings.mainTextFieldVisible.value = props.getProperty("mainTextFieldVisible").toBoolean()
            }
            if (props.containsKey("applyStrokesButtonVisible")) {
                settings.applyStrokesButtonVisible.value = props.getProperty("applyStrokesButtonVisible").toBoolean()
            }
            if (props.containsKey("ocrImagesTextFieldVisible")) {
                settings.ocrImagesTextFieldVisible.value = props.getProperty("ocrImagesTextFieldVisible").toBoolean()
            }
            if (props.containsKey("rememberVisibilityOptions")) {
                settings.rememberVisibilityOptions.value = props.getProperty("rememberVisibilityOptions").toBoolean()
            }
            settings
        } else {
            Settings()
        }
    }

    // Save settings to file
    fun storeSettingsToFile(settings: Settings, settingsFile: String)  {
        val props = Properties()
        val settingsPath = Paths.get(settingsFile)
        props.setProperty("customTesseractToolFullPath", settings.customTesseractToolFullPath.value)
        props.setProperty("customDefaultTypeDelay", settings.customDefaultTypeDelay.value.toString())
        props.setProperty("rememberVisibilityOptions", settings.rememberVisibilityOptions.value.toString())
        if (settings.rememberVisibilityOptions.value) {
            props.setProperty("typingDelayFieldVisible", settings.typingDelayFieldVisible.value.toString())
            props.setProperty("mainTextFieldVisible", settings.mainTextFieldVisible.value.toString())
            props.setProperty("applyStrokesButtonVisible", settings.applyStrokesButtonVisible.value.toString())
            props.setProperty("ocrImagesTextFieldVisible", settings.ocrImagesTextFieldVisible.value.toString())
        }
        Files.newOutputStream(settingsPath).use {
            props.store(it, null)
        }
    }
}
