package util

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.zip.ZipInputStream


/**
 * FileUtil is a utility class for file operations.
 */
class FileUtil {
    // Makes directory
    private fun mkdir(dirToMake: String) {
        val dir = File(dirToMake)
        if (!dir.exists()) {
            dir.mkdir()
        }
    }

    // Export resource file from Jar
    fun exportResource(resourceName: String, whereTo: String): Optional<String> {
        mkdir(whereTo.substring(0, whereTo.lastIndexOf(File.separator)))
        try {
            FileUtil::class.java.getResourceAsStream(resourceName).use { stream ->
                if (stream == null || whereTo.isBlank()) {
                    return Optional.empty()
                }
                var readBytes: Int
                val buffer = ByteArray(4096)
                println(whereTo)
                mkdir(whereTo.substring(0, whereTo.lastIndexOf(File.separator)))
                FileOutputStream(whereTo).use { resStreamOut ->
                    while (stream.read(buffer).also { readBytes = it } > 0) {
                        resStreamOut.write(buffer, 0, readBytes)
                    }
                }
                return Optional.of(whereTo)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return Optional.empty()
    }

    // Unzip file
    fun unzip(inData: InputStream, targetPath: Path) {
        var targetDir = targetPath
        targetDir = targetDir.toAbsolutePath()

        ZipInputStream(inData).use { zipIn ->
            var ze = zipIn.nextEntry
            while (ze != null) {
                val resolvedPath = targetDir.resolve(ze.name).normalize()
                if (!resolvedPath.startsWith(targetDir)) {
                    // see: https://snyk.io/research/zip-slip-vulnerability
                    throw RuntimeException(
                        "Entry with an illegal path: "
                                + ze.name
                    )
                }
                if (ze.isDirectory) {
                    Files.createDirectories(resolvedPath)
                } else {
                    Files.createDirectories(resolvedPath.parent)
                    Files.copy(zipIn, resolvedPath)
                }
                ze = zipIn.nextEntry
            }
        }
    }
}