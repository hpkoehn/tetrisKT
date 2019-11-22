

interface BoardObserver {
    fun pieceSpawned(x: Int, y: Int, type: PieceType, rotation: Rotation)
    fun currentPieceMovedBy(x: Int, y: Int)
    fun currentPieceRotatedTo(rotation: Rotation)
    fun clearedLines(num: Int)
    fun scorePoints(num: Int)
    fun gameover()
}