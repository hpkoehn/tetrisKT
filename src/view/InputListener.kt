package view

import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import Controlable

class InputListener(private val controlable: Controlable): KeyListener {
    override fun keyTyped(p0: KeyEvent?) {}

    override fun keyPressed(p0: KeyEvent?) {
        if (p0 == null) {
            return
        }
        when(p0.extendedKeyCode) {
            KeyEvent.VK_LEFT -> controlable.registerGameAction(GameAction.MOVE_LEFT)
            KeyEvent.VK_RIGHT -> controlable.registerGameAction(GameAction.MOVE_RIGHT)
            KeyEvent.VK_DOWN -> controlable.registerGameAction(GameAction.MOVE_DOWN)
            KeyEvent.VK_L -> controlable.registerGameAction(GameAction.ROTATE_RIGHT)
            KeyEvent.VK_X -> controlable.registerGameAction(GameAction.ROTATE_LEFT)

            else -> print("button pressed!")
        }
    }

    override fun keyReleased(p0: KeyEvent?) {
    }
}