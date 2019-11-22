import java.util.*
import kotlin.random.Random

fun createPiece(type: PieceType, rotation: Rotation): Piece {
    return when(type) {
        PieceType.I -> {
            when(rotation) {
                // o
                // x
                // o
                // o
                Rotation.UP, Rotation.DOWN -> {
                    val body = arrayOf(booleanArrayOf(true, true, true, true))
                    val anchor = Pair(0, 1)
                    Piece(anchor, body, rotation, type)
                }
                // o x o o
                Rotation.LEFT, Rotation.RIGHT -> {
                    val body = arrayOf(booleanArrayOf(true), booleanArrayOf(true), booleanArrayOf(true), booleanArrayOf(true))
                    val anchor = Pair(1, 0)
                    Piece(anchor, body, rotation, type)
                }
            }
        }
        PieceType.L -> {
            when(rotation) {
                // o
                // x
                // oo
                Rotation.UP -> {
                    val body = arrayOf(booleanArrayOf(true, true, true), booleanArrayOf(false, false, true))
                    val anchor = Pair(0, 1)
                    Piece(anchor, body, rotation, type)
                }
                //     o
                // o x o
                Rotation.LEFT -> {
                    val body = arrayOf(booleanArrayOf(false, true), booleanArrayOf(false, true), booleanArrayOf(true, true))
                    val anchor = Pair(1, 1)
                    Piece(anchor, body, rotation, type)
                }
                // o o
                //   x
                //   o
                Rotation.DOWN -> {
                    val body = arrayOf(booleanArrayOf(true, false, false), booleanArrayOf(true, true, true))
                    val anchor = Pair(1, 1)
                    Piece(anchor, body, rotation, type)
                }
                // o x o
                // o
                Rotation.RIGHT -> {
                    val body = arrayOf(booleanArrayOf(true, true), booleanArrayOf(true, false), booleanArrayOf(true, false))
                    val anchor = Pair(1, 0)
                    Piece(anchor, body, rotation, type)
                }
            }
        }
        PieceType.T -> {
            when(rotation) {
                // o x o
                //   o
                Rotation.UP -> {
                    val body = arrayOf(booleanArrayOf(true, false), booleanArrayOf(true, true), booleanArrayOf(true, false))
                    val anchor = Pair(1, 0)
                    Piece(anchor, body, rotation, type)
                }
                // o
                // x o
                // o
                Rotation.LEFT -> {
                    val body = arrayOf(booleanArrayOf(true, true, true), booleanArrayOf(false, true, false))
                    val anchor = Pair(0, 1)
                    Piece(anchor, body, rotation, type)
                }
                //   o
                // o x o
                Rotation.DOWN -> {
                    val body = arrayOf(booleanArrayOf(false, true), booleanArrayOf(true, true), booleanArrayOf(false, true))
                    val anchor = Pair(1, 1)
                    Piece(anchor, body, rotation, type)
                }
                //   o
                // o x
                //   o
                Rotation.RIGHT -> {
                    val body = arrayOf(booleanArrayOf(false, true, false), booleanArrayOf(true, true, true))
                    val anchor = Pair(1, 1)
                    Piece(anchor, body, rotation, type)
                }
            }
        }
        PieceType.O ->  {
            // x o
            // o o
            val body = arrayOf(booleanArrayOf(true, true), booleanArrayOf(true, true))
            Piece(Pair(0, 0), body, rotation, type)
        }
        PieceType.S -> {
            when(rotation) {
                //   o
                // x o
                // o
                Rotation.UP, Rotation.DOWN -> {
                    val body = arrayOf(booleanArrayOf(false, true, true), booleanArrayOf(true, true, false))
                    val anchor = Pair(0, 1)
                    Piece(anchor, body, rotation, type)
                }
                // o x
                //   o o
                Rotation.LEFT, Rotation.RIGHT -> {
                    val body = arrayOf(booleanArrayOf(true, false), booleanArrayOf(true, true), booleanArrayOf(false, true))
                    val anchor = Pair(1, 0)
                    Piece(anchor, body, rotation, type)
                }
            }
        }
        PieceType.Z -> {
            when(rotation) {
                // o
                // o x
                //   o
                Rotation.UP, Rotation.DOWN -> {
                    val body = arrayOf(booleanArrayOf(true, true, false), booleanArrayOf(false, true, true))
                    val anchor = Pair(1, 1)
                    Piece(anchor, body, rotation, type)
                }
                //   o o
                // o x
                Rotation.LEFT, Rotation.RIGHT -> {
                    val body = arrayOf(booleanArrayOf(false, true), booleanArrayOf(true, true), booleanArrayOf(true, false))
                    val anchor = Pair(1, 1)
                    Piece(anchor, body, rotation, type)
                }
            }
        }
    }
}

class PieceRandomizer(seed: Int) {
    val rand = Random(seed)

    fun nextPiece(): Piece {
        val pieceType =  when(rand.nextInt(6)) {
            0 -> PieceType.T
            1 -> PieceType.Z
            2 -> PieceType.I
            3 -> PieceType.L
            4 -> PieceType.O
            5 -> PieceType.S
            else -> PieceType.O
        }
        return createPiece(pieceType, Rotation.UP)
    }
}