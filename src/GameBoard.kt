import java.util.*

class GameBoard(val xs: Int, val ys: Int) {
    private val observers: MutableList<BoardObserver> = LinkedList()
    private var board = Array(xs) { BooleanArray(ys) }

    private var currentPiece: Piece? = null
    private var currentPieceAnchorLocation: Pair<Int, Int>? = null

    private var score = 0

    fun addObserver(ob: BoardObserver) = observers.add(ob)

    fun canPlacePiece(p: Piece, x: Int, y: Int): Boolean {
        val anchorX = p.anchorPoint.first
        val anchorY = p.anchorPoint.second
        // check if we would be out of bounds in x-axis
        if (x - anchorX < 0 || x - anchorX + p.body.size > xs) {
            return false
        }
        // check if we would be out of bounds in y-axis
        if (y - anchorY < 0 || y - anchorY + p.body[0].size > ys) {
            return false
        }
        for (i in 0 until p.body.size) {
            for (j in 0 until p.body[0].size) {
                if (board[x - anchorX + i][y - anchorY + j] && p.body[i][j])
                    return false
            }
        }
        return true
    }

    fun moveCurrentPieceDown(): Boolean {
        return moveCurrentPiece(0, 1)
    }

    fun moveCurrentPieceLeft(): Boolean {
        return moveCurrentPiece(-1, 0)
    }

    fun moveCurrentPieceRight(): Boolean {
        return moveCurrentPiece(1, 0)
    }

    /**
     * x: shift in +x
     * y: shift in +y
     */
    fun moveCurrentPiece(x: Int, y: Int): Boolean {
        // get body and anchor, if no peace return false
        val cPiece = currentPiece ?: return false

        val pLoc = currentPieceAnchorLocation ?: return false

        // remove current piece
        removeCurrentPieceFromBoard()

        // see if we can add piece below us
        return if (canPlacePiece(cPiece, pLoc.first + x, pLoc.second + y)) {
            addPiece(cPiece, pLoc.first + x, pLoc.second + y)
            currentPieceAnchorLocation = Pair(pLoc.first + x, pLoc.second + y)
            observers.map { it.currentPieceMovedBy(x, y) }
            true
        } else {
            addPiece(cPiece, pLoc.first, pLoc.second)
            false
        }
    }

    fun turnCurrentPieceClockwise(): Boolean {
        // get body and anchor, if no peace return false
        val cPiece = currentPiece ?: return false
        val pLoc = currentPieceAnchorLocation ?: return false

        // remove current piece
        removeCurrentPieceFromBoard()

        val clockWisePiece = cPiece.getClockwise()

        return if (canPlacePiece(clockWisePiece, pLoc.first, pLoc.second)) {
            addPiece(clockWisePiece, pLoc.first, pLoc.second)
            currentPiece = clockWisePiece
            observers.map { it.currentPieceRotatedTo(clockWisePiece.rotation) }
            true
        } else {
            addPiece(cPiece, pLoc.first, pLoc.second)
            false
        }
    }

    fun turnCurrentPieceCounterClockwise(): Boolean {
        // get body and anchor, if no peace return false
        val cPiece = currentPiece ?: return false
        val pLoc = currentPieceAnchorLocation ?: return false

        // remove current piece
        removeCurrentPieceFromBoard()

        val counterClockWisePiece = cPiece.getCounterClockwise()

        return if (canPlacePiece(counterClockWisePiece, pLoc.first, pLoc.second)) {
            addPiece(counterClockWisePiece, pLoc.first, pLoc.second)
            currentPiece = counterClockWisePiece
            observers.map { it.currentPieceRotatedTo(counterClockWisePiece.rotation) }
            true
        } else {
            addPiece(cPiece, pLoc.first, pLoc.second)
            false
        }
    }

    fun removeCurrentPieceFromBoard() {
        val pBody = currentPiece?.body ?: return
        val pAnchor = currentPiece?.anchorPoint ?: return

        val anchorX = currentPieceAnchorLocation?.first ?: return
        val anchorY = currentPieceAnchorLocation?.second ?: return

        for (i in 0 until pBody.size) {
            for (j in 0 until pBody[0].size) {
                if (pBody[i][j]) {
                    board[anchorX - pAnchor.first + i][anchorY - pAnchor.second + j] = false
                }
            }
        }
    }

    fun addPiece(p: Piece, x: Int, y: Int): Boolean {
        val pBody = p.body
        val pAnchor = p.anchorPoint

        for (i in 0 until pBody.size) {
            for (j in 0 until pBody[0].size) {
                if (pBody[i][j]) {
                    board[x - pAnchor.first + i][y - pAnchor.second + j] = true
                }
            }
        }
        return true
    }

    fun spawnPiece(p: Piece): Boolean {
        val x = xs / 2
        for (y in 0..1) {
            if (canPlacePiece(p, x, y)) {
                addPiece(p, x, y)
                currentPiece = p
                currentPieceAnchorLocation = Pair(x, y)
                observers.map { it.pieceSpawned(x, y, p.type, p.rotation) }
                return true
            }
        }
        return false
    }

    fun gameover() {
        observers.map { it.gameover() }
    }

    fun scorePoints(num: Int) {
        if (num == 0) return
        score += num
        observers.map { it.scorePoints(num) }
    }

    fun removeAndMoveDownLines(): Int {
        var clearedLines = 0

        // for line
        for (y in 0 until ys) {
            // do we have a full line?
            val needsClear = (0 until xs).all {board[it][y]}
            if (needsClear) {
                // clear line
                (0 until xs).map {board[it][y] = false}
                // move all lines down
                (y downTo 1).map{
                        y_value -> (0 until xs).map {
                            x_value -> board[x_value][y_value] = board[x_value][y_value - 1]
                        }
                }
                clearedLines++
            }
        }
        if (clearedLines > 0) {
            observers.map{ it.clearedLines(clearedLines)}
        }

        return clearedLines
    }
}