package indigo

enum class Suit (private val symbol: Char){
    HEARTS('♥'),
    DIAMONDS('♦'),
    CLUBS('♣'),
    SPADES('♠');

    override fun toString(): String {
        return symbol.toString()
    }
}