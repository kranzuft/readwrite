package util

import java.util.*

class OSUtil {
    fun isWindows(): Boolean {
        return System.getProperty("os.name").lowercase(Locale.getDefault()).contains("win")
    }
}