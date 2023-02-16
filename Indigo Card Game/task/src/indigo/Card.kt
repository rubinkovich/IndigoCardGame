package indigo

class Card(val suit: Suit, val rank: Rank){
    override fun toString(): String {
        return ("$rank$suit")
    }
}