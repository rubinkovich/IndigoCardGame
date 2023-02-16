package indigo

class Player(val name: String) {
    val cardsInHand = mutableListOf<Card>()
    val cardsTaken = mutableListOf<Card>()
    var score = 0
}