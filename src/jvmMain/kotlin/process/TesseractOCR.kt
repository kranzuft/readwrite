package process

import util.FileUtil
import util.OSUtil
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.absolutePathString

class TesseractOCR {
    private val fileUtil = FileUtil()
    private val osUtil = OSUtil()

    // We need to take the resource Tesseract-OCR.zip and extract it to a folder /data and then unzip it into "tess_ocr" folder
    fun prepOCR(workingDirectory: String, customTesseractPath: String): Optional<String> {
        var customTesseract = customTesseractPath
        if (customTesseractPath.isBlank()) {
            customTesseract = workingDirectory + File.separator + "tesseract"
        }
        // do only for windows
        // check if tesseract is in the system path
        val tesseractPath1 = Paths.get("/usr/local/bin/tesseract")
        val tesseractPath2 = Paths.get("/usr/bin/tesseract")
        val customTesseractPath = Paths.get(customTesseract)
        val tesseractWin = Paths.get(customTesseract, File.separator, "tesseract.exe");

        // First check if the override path exists
        // Then the unix paths
        // Then the windows path
        // Else default to no functionality
        return if (Files.exists(customTesseractPath)) {
            Optional.of(customTesseract)
        } else if (Files.exists(tesseractPath1) || Files.exists(tesseractPath2)) {
            Optional.of("tesseract")
        } else if (osUtil.isWindows() && Files.exists(tesseractWin)) {
            Optional.of(tesseractWin.absolutePathString())
        } else {
            Optional.empty()
        }
    }

    // old method to install for windows.
    private fun prepOCRWin(workingDirectory: String): Optional<String> {
        // Get path to unzip ocr library zip to
        val unzipPath = Paths.get(workingDirectory + File.separator + "data")

        // Extract (by copy) ocr library zip from jar
        val zipExportFullPath: Optional<String> = if (!Files.exists(unzipPath)) {
            Files.createDirectory(unzipPath)
            val whereTo = System.getProperty("user.dir").replace("\\", "/") + "/tess_ocr/Tesseract-OCR.zip"
            fileUtil.exportResource("/Tesseract-OCR.zip", whereTo) // should match whereTo if successful
        } else {
            Optional.of(workingDirectory.replace("\\", "/") + "/tess_ocr/Tesseract-OCR.zip")
        }

        if (zipExportFullPath.isEmpty) { // Couldn't get copiedTo path
            Files.delete(unzipPath)
            System.err.println("Couldn't copy file out from jar")
            return zipExportFullPath
        }

        // Now we know the zip location, so let's create a path for that
        val zipLocation = Paths.get(zipExportFullPath.get())

        // Unzip location is the same as the zip location, but without the .zip extension
        val unzipLocation = zipExportFullPath.get().substring(0, zipExportFullPath.get().lastIndexOf("."))
        val unzipLocationPath = Paths.get(unzipLocation)

        if (!Files.exists(unzipLocationPath)) {
            val inData = Files.newInputStream(zipLocation)
            fileUtil.unzip(inData, zipLocation.parent)
        }

        return Optional.of(unzipLocation)
    }

    fun doOCR(tesseractLocation: String, imageToOCRLocation: String): String {
        // determine platform and set up command
        val commandArgs = if (osUtil.isWindows()) {
            val program = tesseractLocation.replace("/", File.separator) + File.separator + "tesseract"
            listOf("cmd.exe", "/c", program, imageToOCRLocation, "stdout", "nobatch", "keys")
        } else {
            listOf("tesseract", imageToOCRLocation, "stdout", "nobatch", "keys")
        }
        val builder = ProcessBuilder(commandArgs)
        builder.redirectErrorStream(true)
        val p = builder.start()
        val r = BufferedReader(InputStreamReader(p.inputStream))
        val stringBuilder = StringBuilder()
        var line: String? = r.readLine()
        while (line != null) {
            if (line != "read_params_file: Can't open keys") {
                stringBuilder.append(line).append("\n")
            }
            line = r.readLine()
        }
        return stringBuilder.toString()
    }
}