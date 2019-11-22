package player

import GameController
import view.*

fun humanPlayer() {
    val xs = 10
    val ys = 20

    val game = GameController()
    val listener = InputListener(game)
    val view = BoardView(xs, ys)
    view.addKeyBoardListener(listener)

    game.initializeNewGame(xs, ys)
    game.registerBoardObserver(view)

    view.start()
    game.startGame()
}