enum class Rotation {
    UP,
    LEFT,
    DOWN,
    RIGHT
}

fun Rotation.rotateClockwise(): Rotation {
    return when(this) {
        Rotation.UP -> Rotation.RIGHT
        Rotation.RIGHT -> Rotation.DOWN
        Rotation.DOWN -> Rotation.LEFT
        Rotation.LEFT -> Rotation.UP
    }
}

fun Rotation.rotateCounterClockwise(): Rotation {
    return when(this) {
        Rotation.UP -> Rotation.LEFT
        Rotation.LEFT -> Rotation.DOWN
        Rotation.DOWN -> Rotation.RIGHT
        Rotation.RIGHT -> Rotation.UP
    }
}