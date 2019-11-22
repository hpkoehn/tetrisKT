package view

import BoardObserver
import Piece
import PieceType
import Rotation
import createPiece
import java.awt.Color
import java.awt.Graphics
import java.awt.event.KeyListener
import java.awt.image.BufferStrategy
import kotlin.random.Random

val SCALE: Float = 1.5f

class BoardView(val xs: Int, val ys: Int): BoardObserver, Runnable {

    // concurrency related stuff
    // lock for whole view.BoardView object
    private val lock = Object()
    // Rendering is running
    private var running = false
    // pointer to thread if thread is running, else null
    private var thread :Thread? = null
    // internal variable for hasChanged
    private var _hasChanged = false
    // flag if board has changed and rendering must be done
    var hasChanged: Boolean
        get() = synchronized(lock) {
            return _hasChanged
        }
        set(value) = synchronized(lock) {
            _hasChanged = value
        }

    val isReady: Boolean
        get() = synchronized(lock) {
            return running
        }

    // GameBoard state
    private val board: Array<Array<Color?>> = Array(xs) { arrayOfNulls<Color?>(ys)}
    private var currentPiece: Piece? = null
    private var currentPieceLocation: Pair<Int, Int>? = null
    private var score = 0

    // Render related variables
    private var blockSize = (10 * SCALE).toInt() // pixel
    private var borderOffset = (50 * SCALE).toInt()
    private var screenWidth = borderOffset * 2 + blockSize * xs
    private var screenHeight = borderOffset * 2 + blockSize * ys
    private var display: Display = Display("Tetres", screenWidth, screenHeight)
    private var bufferStrategy: BufferStrategy
    private var graphics: Graphics

    init {
        display.canvas.createBufferStrategy(3)
        bufferStrategy = display.canvas.bufferStrategy
        graphics = bufferStrategy.drawGraphics
    }

    fun addKeyBoardListener(keyListener: KeyListener) {
        display.addKeyListener(keyListener)
    }

    private fun render() {
        bufferStrategy = display.canvas.bufferStrategy
        graphics = bufferStrategy.drawGraphics

        // clear screen
        graphics.clearRect(0, 0, screenWidth, screenHeight)

        // render GameBoard frame
        graphics.drawRect(borderOffset, borderOffset, blockSize * xs, blockSize * ys)


        // render pieces
        for(x in 0 until xs) {
            for(y in 0 until ys) {
                val color = board[x][y] ?: continue
                graphics.color = color
                graphics.fillRect(borderOffset + x * blockSize, borderOffset + y * blockSize, blockSize, blockSize)
                graphics.color = Color.BLACK
                graphics.drawRect(borderOffset + x * blockSize, borderOffset + y * blockSize, blockSize, blockSize)

            }
        }

        // render score
        val score = "Score: $score".toCharArray()

        val textY = (1.5 * borderOffset).toInt() + blockSize * ys
        val textX = (1.5 * borderOffset).toInt()

        graphics.drawChars(score, 0, score.size, textX, textY)

        // show board
        bufferStrategy.show()
        graphics.dispose()

    }


    fun start() {
        synchronized(lock) {
            if (thread != null) return
            running = true

            thread = Thread(this)
            thread?.start() ?: return
        }
    }

    fun stop() {
        synchronized(lock) {
            running = false
            thread = null
        }
    }

    override fun run() {
        while(running) {
            synchronized(lock) {
                if(!hasChanged) {
                    lock.wait()
                }

                // render board
                render()
                // end rendering

                hasChanged = false
            }

        }
    }

    override fun scorePoints(num: Int) = synchronized(lock) {
        score += num
        lock.notifyAll()
    }

    override fun currentPieceMovedBy(x: Int, y: Int) = synchronized(lock) {
        val cPiece = currentPiece ?: return
        val loc = currentPieceLocation ?: return

        val color = board[loc.first][loc.second] ?: Color.WHITE

        removeCurrentPieceFromBoard()
        addPiece(cPiece, loc.first + x, loc.second + y, color)
        currentPieceLocation = Pair(loc.first + x, loc.second + y)
        hasChanged = true
        lock.notifyAll()
    }

    override fun currentPieceRotatedTo(rotation: Rotation) = synchronized(lock) {
        val type = currentPiece?.type ?: return
        val loc = currentPieceLocation ?: return
        val p = createPiece(type, rotation)

        val color = board[loc.first][loc.second] ?: Color.WHITE

        removeCurrentPieceFromBoard()
        addPiece(p, loc.first, loc.second, color)
        currentPiece = p
        hasChanged = true
        lock.notifyAll()
    }

    override fun gameover() = synchronized(lock) {
        (0 until xs).map{x -> (0 until ys).map{y -> board[x][y] = null}}
        hasChanged = true
        lock.notifyAll()
    }

    override fun pieceSpawned(x: Int, y: Int, type: PieceType, rotation: Rotation) = synchronized(lock) {
        val p = createPiece(type, rotation)

        val color = when(Random(System.currentTimeMillis().toInt()).nextInt(6)) {
            0 -> Color.BLUE
            1 -> Color.GREEN
            2 -> Color.YELLOW
            3 -> Color.RED
            4 -> Color.ORANGE
            5 -> Color.CYAN
            else -> Color.WHITE
        }

        addPiece(p, x, y, color)
        currentPiece = p
        currentPieceLocation = Pair(x, y)

        hasChanged = true
        lock.notifyAll()
    }


    // GameBoard utility functions

    fun addPiece(p: Piece, x: Int, y: Int, color: Color): Boolean {
        val pBody = p.body
        val pAnchor = p.anchorPoint

        for (i in 0 until pBody.size) {
            for (j in 0 until pBody[0].size) {
                if (pBody[i][j]) {
                    board[x - pAnchor.first + i][y - pAnchor.second + j] = color
                }
            }
        }
        return true
    }

    fun removeCurrentPieceFromBoard() {
        val pBody = currentPiece?.body ?: return
        val pAnchor = currentPiece?.anchorPoint ?: return

        val anchorX = currentPieceLocation?.first ?: return
        val anchorY = currentPieceLocation?.second ?: return

        for (i in 0 until pBody.size) {
            for (j in 0 until pBody[0].size) {
                if (pBody[i][j]) {
                    board[anchorX - pAnchor.first + i][anchorY - pAnchor.second + j] = null
                }
            }
        }
    }

    override fun clearedLines(num: Int) = synchronized(lock){
        // for line
        for (y in 0 until ys) {
            // do we have a full line?
            val needsClear = (0 until xs).all {board[it][y] != null}
            if (needsClear) {
                // clear line
                (0 until xs).map {board[it][y] = null}
                // move all lines down
                (y downTo 1).map{
                        y_value -> (0 until xs).map {
                        x_value -> board[x_value][y_value] = board[x_value][y_value - 1]
                }
                }
            }
        }
        hasChanged = true
        lock.notifyAll()
    }

}