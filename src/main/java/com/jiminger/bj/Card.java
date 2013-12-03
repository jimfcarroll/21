package com.jiminger.bj;

public class Card
{
   Suit suit;
   Face face;
   
   public Card(final Suit suit, final Face face) { this.suit = suit; this.face = face; }
   
   public boolean isAce() { return face == Face.ace; }
   
   @Override
   public String toString() { return face + " of " + suit + "s"; }
   
   @Override
   public int hashCode() { return face.ordinal() | (suit.ordinal() << 3);  }

   @Override
   public boolean equals(final Object o) { final Card c = (Card)o; return face == c.face && suit == c.suit;  }
}

