import java.util.*
import kotlin.math.max

class GameController: Runnable, Controlable {

    // concurrency stuff
    private val lock = Object()
    private val actionQueueLock = Object()
    private var running = false
    private var thread: Thread? = null

    private lateinit var gameBoard: GameBoard

    private val actionQueue: MutableList<GameAction> = LinkedList()

    // thread for running the game

    fun initializeNewGame(xs: Int, ys: Int) = synchronized(lock) {
        if (running) {
            return
        }
        gameBoard = GameBoard(xs, ys)
    }

    fun registerBoardObserver(obs: BoardObserver) {
        synchronized(lock) {
            if (!::gameBoard.isInitialized) {
                print("Could not register observer $obs, as the gameBoard has not been initialized yet!")
                return
            }
            gameBoard.addObserver(obs)
        }
    }

    override fun registerGameAction(gameAction: GameAction) = synchronized(actionQueueLock) {
        actionQueue.add(gameAction)
        Unit
    }

    private fun pollGameAction(): GameAction? = synchronized(actionQueueLock) {
        if(actionQueue.isEmpty()) return null
        return actionQueue.removeAt(0)
    }

    fun startGame() {
        if (thread != null) {
            return
        }
        running = true
        thread = Thread(this)
        thread?.start()
    }

    fun stopGame() {
        running = false
        thread = null
    }

    override fun run() {

        // Number of times the current piece needs to be moved down
        var pieceMovesDown = 0
        // milliseconds to move a piece down at the beginning
        var timeToMoveDown: Long = 1000
        // speedup upon clear
        val speedUp: Long = 20
        // maximal speed
        val minimalSpeed: Long = 100
        // time measurement
        var oldTime: Long = System.currentTimeMillis()
        var currentTime: Long
        var delta: Long = 0

        val randomizer = PieceRandomizer(System.currentTimeMillis().toInt())
        gameBoard.spawnPiece(randomizer.nextPiece())

        gameLoop@ while (running) {

            var playerAction: GameAction? = pollGameAction()
            // execute player actions
            actionLoop@ while(playerAction != null) {
                when(playerAction) {
                    GameAction.MOVE_DOWN -> {
                        if (!gameBoard.moveCurrentPieceDown()) {
                            // don't wait for normal move down time
                            delta = timeToMoveDown
                            break@actionLoop
                        }
                    }
                    GameAction.MOVE_LEFT -> gameBoard.moveCurrentPieceLeft()
                    GameAction.MOVE_RIGHT -> gameBoard.moveCurrentPieceRight()
                    GameAction.ROTATE_LEFT -> gameBoard.turnCurrentPieceCounterClockwise()
                    GameAction.ROTATE_RIGHT -> gameBoard.turnCurrentPieceClockwise()
                    GameAction.EXIT -> {
                        stopGame()
                        break@gameLoop
                    }
                }
                playerAction = pollGameAction()
            }

            // do time computations
            currentTime = System.currentTimeMillis()
            delta += currentTime - oldTime
            pieceMovesDown += (delta / timeToMoveDown).toInt()
            oldTime = currentTime

            // check if it is time to do a game step (aka move piece down)
            if (pieceMovesDown > 0) {

                delta = 0
                pieceMovesDown--

                if (!gameBoard.moveCurrentPieceDown()) {
                    // Piece could not be moved down -> hit other block
                    // therefore check for completed lines
                    val clearedLines = gameBoard.removeAndMoveDownLines()
                    // apply points
                    gameBoard.scorePoints(clearedLines * clearedLines * 100)

                    // do speedup if lines were cleared
                    timeToMoveDown -= speedUp * clearedLines
                    timeToMoveDown = max(timeToMoveDown, minimalSpeed)

                    // and spawn new piece
                    if(!gameBoard.spawnPiece(randomizer.nextPiece())) {
                        // Piece could not be spawned -> other hit the ceiling
                        // therefore gameover
                        gameBoard.gameover()
                        stopGame()
                        break@gameLoop
                    }
                }
            }
        }
    }


}