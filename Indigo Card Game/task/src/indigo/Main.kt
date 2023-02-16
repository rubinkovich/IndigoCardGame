package indigo

import kotlin.system.exitProcess

fun main() {

    val generalDeck = takeNewDeck()
    val cardsOnTable = mutableListOf<Card>()
    val track = mutableListOf<Card>()
    val player = Player("Player")
    val computer = Player("Computer")

    println("Indigo Card Game")

    var playerTurn = playerIsFirst()
    var lastWinner = if (playerTurn) player else computer
    dealCards(4, generalDeck, cardsOnTable)
    dealCards(6, generalDeck, player.cardsInHand)
    dealCards(6, generalDeck, computer.cardsInHand)
    println("Initial cards on the table: ${deckToString(cardsOnTable)}")

    while (true) {

        var currentPlayer = if (playerTurn) player else computer

        if (cardsOnTable.isEmpty()) {
            println("\nNo cards on the table")
        } else {
            println("\n${cardsOnTable.size} cards on the table, and the top card is ${cardsOnTable.last()}")
        }

        if (player.cardsInHand.isEmpty() && computer.cardsInHand.isEmpty() && generalDeck.isEmpty()) {
            if (player.cardsTaken.isEmpty() && computer.cardsTaken.isEmpty()) {
                lastWinner = currentPlayer
            }
            track.addAll(cardsOnTable)
            takeCards(cardsOnTable, lastWinner)

            if (player.cardsTaken.size == computer.cardsTaken.size) {
                currentPlayer.score += 3
            } else {
                if (player.cardsTaken.size > computer.cardsTaken.size) {
                    player.score += 3
                } else {
                    computer.score += 3
                }
            }
            printScore(player, computer)
            break
        }

        val card = if (currentPlayer == player) playerTurn(player) else computerTurn(computer, cardsOnTable)
        if (currentPlayer == computer) println("Computer plays $card")
        currentPlayer.cardsInHand.remove(card)
        cardsOnTable.add(card)

        if (sameCards(cardsOnTable)) {
            println("${currentPlayer.name} wins cards")
            track.addAll(cardsOnTable)
            lastWinner = currentPlayer
            takeCards(cardsOnTable, currentPlayer)
            printScore(player, computer)
        }

        if (player.cardsInHand.isEmpty() && computer.cardsInHand.isEmpty() && generalDeck.isNotEmpty()) {
            dealCards(6, generalDeck, player.cardsInHand)
            dealCards(6, generalDeck, computer.cardsInHand)
        }

        playerTurn = !playerTurn
    }

    println("Game Over")
}

fun takeCards(cardsOnTable: MutableList<Card>, player: Player) {
    player.score += cardsOnTable.sumOf { it.rank.weight }
    player.cardsTaken.addAll(cardsOnTable)
    cardsOnTable.clear()
}

fun randomCard(cards: MutableList<Card>): Card {
    var candidates: MutableList<Card>

    val suits = Suit.values().toList()
    val maxSuit = suits.maxByOrNull { suit -> cards.count { it.suit == suit } }
    candidates = cards.filter { it.suit == maxSuit }.toMutableList()
    if (candidates.size > 1) return candidates.random()

    val ranks = Rank.values().toList()
    val maxRank = ranks.maxByOrNull { rank -> cards.count { it.rank == rank } }
    candidates = cards.filter { it.rank == maxRank }.toMutableList()
    if (candidates.size > 1) return candidates.random()

    return cards.random()
}

fun computerTurn(computer: Player, cardsOnTable: MutableList<Card>): Card {
    println(deckToString(computer.cardsInHand))
    if (computer.cardsInHand.size == 1) return computer.cardsInHand.first()
    if (cardsOnTable.isEmpty()) {
        return randomCard(computer.cardsInHand)
    }
    val topCard = cardsOnTable.last()
    val candidates = computer.cardsInHand.filter { it.rank == topCard.rank || it.suit == topCard.suit }.toMutableList()
    if (candidates.isEmpty()) return randomCard(computer.cardsInHand)
    if (candidates.size == 1) return candidates.first()
    return randomCard(candidates)
}

fun playerTurn(player: Player): Card {
    println("Cards in hand: ${deckToStringWithIndexes(player.cardsInHand)}")
    while (true) {
        println("Choose a card to play (1-${player.cardsInHand.size}):")
        val str = readln()
        if (str == "exit") println("Game over").also { exitProcess(0) }
        try {
            return player.cardsInHand[str.toInt() - 1]
        } catch (e: Exception) {
            continue
        }
    }
}

fun printScore(player: Player, computer: Player) {
    println("Score: Player ${player.score} - Computer ${computer.score}")
    println("Cards: Player ${player.cardsTaken.size} - Computer ${computer.cardsTaken.size}")
}

fun sameCards(cardsOnTable: MutableList<Card>): Boolean {
    if (cardsOnTable.size < 2) return false
    val card1 = cardsOnTable.last()
    val card2 = cardsOnTable[cardsOnTable.size - 2]
    return ((card1.rank == card2.rank) || (card1.suit == card2.suit))
}

fun playerIsFirst(): Boolean {
    val playerTurn: Boolean
    while (true) {
        println("Play first?")
        when (readln().lowercase()) {
            "yes" -> {
                playerTurn = true
                break
            }
            "no" -> {
                playerTurn = false
                break
            }
            else -> continue
        }
    }
    return playerTurn
}

fun dealCards(i: Int, generalDeck: MutableList<Card>, deck: MutableList<Card>) {
    val list = generalDeck.subList(0, i)
    deck.addAll(list)
    generalDeck.removeAll(list)
}

fun takeNewDeck(): MutableList<Card>{
    val deck = mutableListOf<Card>()
    Suit.values().forEach {suit ->
        Rank.values().forEach {rank ->
            deck.add(Card(suit = suit, rank = rank))
        }
    }
    deck.shuffle()
    return deck
}

fun deckToStringWithIndexes (deck: MutableList<Card>): String {
    return deck.mapIndexed { index, card -> "${index + 1})$card" }.joinToString (" ")
}

fun deckToString (deck: MutableList<Card>): String {
    return deck.joinToString (" ")
}

