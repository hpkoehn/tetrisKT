package view

import java.awt.Canvas
import java.awt.Dimension
import java.awt.event.KeyListener
import javax.swing.JFrame

class Display(title: String, width: Int, height: Int) {

    private var frame: JFrame = JFrame(title)
    var canvas: Canvas = Canvas()

    init {
        // setup frame
        frame.setSize(width, height)
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.isResizable = false
        frame.setLocationRelativeTo(null)
        frame.isVisible = true

        // setup canvas
        canvas.preferredSize = Dimension(width, height)
        canvas.maximumSize = Dimension(width, height)
        canvas.minimumSize = Dimension(width, height)

        frame.add(canvas)
        frame.pack()
    }

    fun addKeyListener(kl: KeyListener) {
        frame.addKeyListener(kl)
    }

}