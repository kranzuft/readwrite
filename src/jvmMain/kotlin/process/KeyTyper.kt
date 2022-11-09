package process

import java.awt.AWTException
import java.awt.Robot
import java.awt.event.KeyEvent
import java.util.concurrent.TimeUnit

// Types the given text using the keyboard.
class KeyTyper {
    // convert string to java key press events
    fun type(str: String) {
        try {
            val robot = Robot()
            TimeUnit.MILLISECONDS.sleep(500)
            var lines = 1
            for (ch in str.toCharArray()) {
                if (Character.isUpperCase(ch)) {
                    doShifted(robot, ch.code)
                } else if ("!@#$%^&*(){}_:+~<>?<>?|".contains(ch.toString() + "")) {
                    val modified = mapUpperSpecial(ch)
                    doShifted(robot, modified.code)
                } else if (ch == '\n') {
                    doNotShifted(robot, KeyEvent.VK_ENTER)
                    lines++
                } else if (ch == '"') {
                    doShifted(robot, KeyEvent.VK_QUOTE)
                } else if (ch == '\'') {
                    doNotShifted(robot, KeyEvent.VK_QUOTE)
                } else if (ch == '`') {
                    doNotShifted(robot, KeyEvent.VK_BACK_QUOTE)
                } else if (ch == '~') {
                    doShifted(robot, KeyEvent.VK_BACK_QUOTE)
                } else if (Character.isLetterOrDigit(ch) || Character.isWhitespace(ch)) {
                    val upCh = ch.uppercaseChar()
                    doNotShifted(robot, upCh.code)
                } else if (",./;\\=`-[]".contains(ch.toString() + "")) {
                    doNotShifted(robot, ch.code)
                } else {
                    println("Not sure what to do with: $ch")
                }
                if (lines % 5 == 0) {
                    TimeUnit.SECONDS.sleep(5)
                    lines = 1
                }
            }
        } catch (e: AWTException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }

    // Not shifted key press
    private fun doNotShifted(robot: Robot, keyEvent: Int) {
        robot.keyPress(keyEvent)
        robot.keyRelease(keyEvent)
    }

    // Shifted key press
    private fun doShifted(robot: Robot, keyEvent: Int) {
        robot.keyPress(KeyEvent.VK_SHIFT)
        doNotShifted(robot, keyEvent)
        robot.keyRelease(KeyEvent.VK_SHIFT)
    }

    // Special characters that need to be changed to character (non-shift) counterpart on key.
    // When shifted, correct key will be pressed.
    private fun mapUpperSpecial(ch: Char): Char {
        when (ch) {
            '!' -> return '1'
            '@' -> return '2'
            '#' -> return '3'
            '$' -> return '4'
            '%' -> return '5'
            '^' -> return '6'
            '&' -> return '7'
            '*' -> return '8'
            '(' -> return '9'
            ')' -> return '0'
            '+' -> return '='
            ':' -> return ';'
            '"' -> return '\''
            '~' -> return '`'
            '_' -> return '-'
            '<' -> return ','
            '>' -> return '.'
            '?' -> return '/'
            '|' -> return '\\'
            '{' -> return '['
            '}' -> return ']'
            else -> println("Don't know how to handle: $ch")
        }
        return ' '
    }
}