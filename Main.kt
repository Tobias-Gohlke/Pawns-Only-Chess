package chess

import java.lang.Exception
import kotlin.system.exitProcess

fun main() {
    val game = PawnChess()
}

class Player(val name: String, val color: String)

class PawnChess() {
    private val firstPlayer: Player
    private val secondPlayer: Player
    private var currentPlayerName: String
    private var board: Array<Array<String>> = Array(18) {Array(17) { "" } }
    private var numberOfBoard: Int = 8
    private var char: Char = '`'
    private val blackRow: Int = 3
    private val whiteRow: Int = 13
    private var field: String = ""
    private var currentPosition: String = ""
    private var regex = Regex("[a-h][1-9][a-h][1-9]")
    private var firstRowPosition: Int = 0
    private var firstColumnPosition: Int = 0
    private var secondRowPosition: Int = 0
    private var secondColumnPosition: Int = 0
    private var currentColor: String = ""
    private var lastColor: String = " B "
    private var movedPosition: String = ""
    private var lastMovefirstRowPosition = 0
    private var lastMovefirstColumnPosition = 0
    private var lastMoveSecondRowPosition = 0
    private var lastMoveSecondColumnPosition = 0
    private var listForColumn = listOf(0,1,3,5,7,9,11,13,15)
    private var lostOfPlayer = true
    private var stalemate = true

    init {
        println("Pawns-Only Chess")
        println("First Player's name:")
        firstPlayer =  Player(readLine()!!, " W ") // White
        println("Second Player's name:")
        secondPlayer = Player(readLine()!!, " B ") // Black
        currentPlayerName = firstPlayer.name
        currentColor = firstPlayer.color
        board = boardConstruction()
        printBoard(board)
        startGame()
    }

    private fun boardConstruction(): Array<Array<String>> {
        for (i in board.indices) {
            for (j in 0 until board[0].size) {
                board[i][j] += if (j == 0 && i % 2 == 1) "${(numberOfBoard--)} " else if (j == 0) "  " else ""
                when (i % 2) {
                    0 -> board[i][j] += if (j % 2 == 0 && j != 1) "+" else "---"
                    1 -> board[i][j] += if (j % 2 == 0 && j != 1 && i != 17) "|" else if(i != 17) "   " else if (j < 9) "  ${char++} " else ""
                }
                when(i) {
                    blackRow -> board[i][j] = if (j % 2 == 1 && j != 0) " B " else board[i][j]
                    whiteRow -> board[i][j] = if (j % 2 == 1 && j != 0) " W " else board[i][j]
                }
            }
        }
        board[17][0] = "  "
        return board
    }

    private fun printBoard(board: Array<Array<String>>) {
        for (element in board) {
            for (j in 0 until board[0].size) {
                print(element[j])
            }
            println()
        }
    }

    private fun startGame() {
        println("$currentPlayerName's turn:")
        field = readLine()!!
        checkInput(field)
        makeMove()
        printBoard(board)
        checkWinConditions()
        changePlayer()
    }

    private fun checkInput(field: String) {
        if(field == "exit") exit()
        if(!field.matches(regex)) invalid()
        initiateFields()

        if(board[firstRowPosition][firstColumnPosition] != currentColor && currentColor == " W ") {
            println("No white pawn at $currentPosition")
            startGame()
        } else if (board[firstRowPosition][firstColumnPosition] != currentColor && currentColor == " B ") {
            println("No black pawn at $currentPosition")
            startGame()
        }
    }

    private fun makeMove() {
        checkPossibleMove()
        board[firstRowPosition][firstColumnPosition] = "   "
        board[secondRowPosition][secondColumnPosition] = currentColor
        // En Passante
        if ((currentColor == " S " && (lastMovefirstRowPosition - lastMoveSecondRowPosition == 4 && (lastMoveSecondRowPosition + 2 == secondRowPosition && (secondColumnPosition == lastMoveSecondColumnPosition && (lastMoveSecondColumnPosition - 2 == firstColumnPosition || lastMoveSecondColumnPosition + 2 == firstColumnPosition)))))
            || (currentColor == " W " && (lastMovefirstRowPosition - lastMoveSecondRowPosition == -4 && (lastMoveSecondRowPosition - 2 == secondRowPosition && (secondColumnPosition == lastMoveSecondColumnPosition && (lastMoveSecondColumnPosition - 2 == firstColumnPosition || lastMoveSecondColumnPosition + 2 == firstColumnPosition)))))) {
            board[lastMoveSecondRowPosition][lastMoveSecondColumnPosition] = "   "
        }
    }

    private fun checkPossibleMove() {
        if (currentColor == " W ") {
            if (!((firstRowPosition == 13 && secondRowPosition == 9) || secondRowPosition + 2 == firstRowPosition)) invalid() // it's allowed to move two steps at the beginning
        } else if (!((firstRowPosition == 3 && secondRowPosition == 7) || (secondRowPosition - 2 == firstRowPosition))) invalid() // it's allowed to move two steps at the beginning
        if (firstColumnPosition == secondColumnPosition && board[secondRowPosition][secondColumnPosition] == lastColor) invalid() // if its same column you can't take the other pawn
        if (firstColumnPosition != secondColumnPosition) {
            if (!(((firstColumnPosition + 2 == secondColumnPosition || firstColumnPosition - 2 == secondColumnPosition)
                        && board[secondRowPosition][secondColumnPosition] == lastColor) // take another pawn
                        || (currentColor == " S " && (lastMovefirstRowPosition - lastMoveSecondRowPosition == 4 && (lastMoveSecondRowPosition + 2 == secondRowPosition && (secondColumnPosition == lastMoveSecondColumnPosition && (lastMoveSecondColumnPosition - 2 == firstColumnPosition || lastMoveSecondColumnPosition + 2 == firstColumnPosition)))))
                        || (currentColor == " W " && (lastMovefirstRowPosition - lastMoveSecondRowPosition == -4 && (lastMoveSecondRowPosition - 2 == secondRowPosition && (secondColumnPosition == lastMoveSecondColumnPosition && (lastMoveSecondColumnPosition - 2 == firstColumnPosition || lastMoveSecondColumnPosition + 2 == firstColumnPosition))))))) invalid()
        }
    }

    private fun checkWinConditions() {
        for (i in board.indices) {
            for (j in 0 until board[0].size) {
                if (board[i][j] == lastColor) {
                    lostOfPlayer = false
                    try {
                        if (lastColor == " W ") {
                            if (board[i - 2][j] == "   " || (board[i - 2][j - 2] == " B " || board[i - 2][j + 2] == " B ")) stalemate =
                                false
                        } else if (lastColor == " B ") {
                            if (board[i + 2][j] == "   " || (board[i + 2][j - 2] == " W " || board[i + 2][j + 2] == " W ")) stalemate =
                                false
                        }
                    } catch (e: Exception) {
                        null
                    }
                }
            }
        }
        if (secondRowPosition == 1 && currentColor == " W ") lostOfPlayer = true
        if (secondRowPosition == 15 && currentColor == " B ") lostOfPlayer = true

        if (lostOfPlayer) {
            if (lastColor == " B ") println("White Wins!") else println("Black Wins!")
            println("Bye!")
            exitProcess(0)
        }
        if (stalemate) {
            println("Stalemate!")
            println("Bye!")
            exitProcess(0)
        }
    }

    private fun changePlayer() {
        lastMovefirstRowPosition = firstRowPosition
        lastMoveSecondRowPosition = secondRowPosition
        lastMovefirstColumnPosition = firstColumnPosition
        lastMoveSecondColumnPosition = secondColumnPosition
        currentPlayerName = if (currentPlayerName == firstPlayer.name) secondPlayer.name else firstPlayer.name
        lastColor = currentColor
        currentColor = if (currentColor == firstPlayer.color) secondPlayer.color else firstPlayer.color
        lostOfPlayer = true
        stalemate = true
        startGame()
    }

    private fun initiateFields() {
        currentPosition = field.substring(0..1)
        movedPosition = field.substring(2..3)
        firstRowPosition = 17 - currentPosition[1].toString().toInt() * 2
        firstColumnPosition = listForColumn[board[17].joinToString().indexOf(currentPosition[0]) / 6]
        secondRowPosition = 17 - movedPosition[1].toString().toInt() * 2
        secondColumnPosition = listForColumn[board[17].joinToString().indexOf(movedPosition[0]) / 6]
    }

    private fun invalid() {
        println("Invalid Input")
        startGame()
    }

    private fun exit() {
        println("Bye!")
        exitProcess(0)
    }
}

