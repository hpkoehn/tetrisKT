

class Board<T>(val xs: Int, val ys: Int) {
    var body: Array<Array<T?>> = Array(xs) { emptyArray<T?>(ys) {null} }
    var currentPiece: Piece? = null
    var currentPieceLocation: Pair<Int, Int>? = null

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
                if (body[x - anchorX + i][y - anchorY + j] && p.body[i][j])
                    return false
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
                    body[anchorX - pAnchor.first + i][anchorY - pAnchor.second + j] = false
                }
            }
        }
    }

}

