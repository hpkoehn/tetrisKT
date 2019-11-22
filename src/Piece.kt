

data class Piece(val anchorPoint: Pair<Int, Int>, val body: Array<BooleanArray>, val rotation: Rotation, val type: PieceType) {

    fun getClockwise(): Piece {
        return createPiece(type, rotation.rotateClockwise())
    }

    fun getCounterClockwise(): Piece {
        return createPiece(type, rotation.rotateCounterClockwise())
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Piece) {
            return false
        }
        return this.rotation == other.rotation && this.type == other.type
    }

    override fun hashCode(): Int {
        return Pair(rotation.hashCode(), type.hashCode()).hashCode()
    }
}