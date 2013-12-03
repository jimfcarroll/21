package com.jiminger.bj;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class B21
{
   static private int baseValue[] = new int[Face.values().length];
   
   static private List<Card> flatten(Collection<Set<Card>> toFlatten)
   {
      List<Card> ret = new ArrayList<Card>();
      for (Set<Card> deck : toFlatten)
         for (Card card : deck)
            ret.add(card);
      return ret;
   }
   
   static {
      baseValue[Face.two.ordinal()] = 2;
      baseValue[Face.three.ordinal()] = 3;
      baseValue[Face.four.ordinal()] = 4;
      baseValue[Face.five.ordinal()] = 5;
      baseValue[Face.six.ordinal()] = 6;
      baseValue[Face.seven.ordinal()] = 7;
      baseValue[Face.eight.ordinal()] = 8;
      baseValue[Face.nine.ordinal()] = 9;
      baseValue[Face.ten.ordinal()] = 10;
      baseValue[Face.jack.ordinal()] = 10;
      baseValue[Face.queen.ordinal()] = 10;
      baseValue[Face.king.ordinal()] = 10;
      baseValue[Face.ace.ordinal()] = 11;
   }
   
   static public Set<Card> makeDeck()
   {
      Set<Card> ret = new HashSet<Card>();
      
      for (Suit suit : Suit.values())
      {
         for (Face face : Face.values())
            ret.add(new Card(suit, face));
      }
      return ret;
   }
   
   public static Collection<Set<Card>> makeDecks(int numDecks)
   {
      Collection<Set<Card>> ret = new ArrayList<Set<Card>>(numDecks);
      
      for (int i = 0; i < numDecks; i++)
         ret.add(makeDeck());
      
      return ret;
   }
   
   public static void removeCards(Collection<Card> shoe, Collection<Card> toRemove)
   {
      for (Card card : toRemove)
         removeCard(shoe,card);
   }
   
   public static void removeCard(Collection<Card> shoe, Card toRemove)
   {
      if (!shoe.remove(toRemove))
         throw new RuntimeException("Couldn't remove " + toRemove + " from " + shoe + " because there wasn't one.");
   }
   
   public static Collection<Card> makeHand(Collection<Card> hand, Card... cards)
   {
      Collection<Card> ret = new ArrayList<Card>();
      ret.addAll(hand);
      for (Card c : cards)
         ret.add(c);
      return ret;
   }
   
   public static Collection<Card> makeHand(Card... cards)
   {
      Collection<Card> ret = new ArrayList<Card>();
      for (Card c : cards)
         ret.add(c);
      return ret;
   }
   
   public static int value(Collection<Card> hand)
   {
      int val = 0;
      int canSubtractTen = 0;
      for (Card card : hand)
      {
         val += baseValue[card.face.ordinal()];
         if (card.isAce())
            canSubtractTen++;
      }
      
      while (val > 21 && canSubtractTen > 0)
      {
         val -= 10;
         canSubtractTen--;
      }
      return val;
   }
   
   public static int value(Card... hand) {  return value(Arrays.asList(hand)); }
   
   public static boolean valueIsSoft(Card... hand)
   {
      int val = 0;
      int canSubtractTen = 0;
      for (Card card : hand)
      {
         val += baseValue[card.face.ordinal()];
         if (card.isAce())
            canSubtractTen++;
      }
      
      while (val > 21 && canSubtractTen > 0)
      {
         val -= 10;
         canSubtractTen--;
      }
      
      // if we didn't use all of our canSubtractTens then we're a soft value
      return canSubtractTen > 0;
   }
   
   public static Odds oddsOfBust(List<Card> hand, Card dealerShowing, int numDecks)
   {
      // brute force the odds calculation
      Collection<Card> shoe = flatten(makeDecks(numDecks));
      removeCards(shoe,hand);
      removeCard(shoe,dealerShowing);
      int totalPossibilities = 0;
      int numBusted = 0;
      for (Card card : shoe)
      {
         totalPossibilities++;
         if (value(makeHand(hand,card)) > 21)
            numBusted++;
      }
      
      return new Odds(numBusted,totalPossibilities);
   }
   
   public static boolean dealerHasToHit(Card... dealerHand)
   {
      return dealerHasToHit(true,dealerHand);
   }
   
   public static boolean dealerHasToHit(boolean hitsOnSoft17, Card... dealerHand)
   {
      int val = value(dealerHand);
      if (val < 17)
         return true;
      if (hitsOnSoft17 && valueIsSoft(dealerHand) && val < 18)
         return true;
      return false;
   }
   
   public static Odds oddsOfDealerBustOnNextCardIfDealerHits(List<Card> hand, Card dealerShowing, int numDecks)
   {
      return oddsOfDealerBustOnNextCardIfDealerHits(true,hand,dealerShowing,numDecks);
   }
   
   public static Odds oddsOfDealerBustOnNextCardIfDealerHits(boolean hitsOnSoft17, List<Card> hand, Card dealerShowing, int numDecks)
   {
      // brute force the odds calculation
      List<Card> shoe = flatten(makeDecks(numDecks));
      removeCards(shoe,hand);
      removeCard(shoe,dealerShowing);
      int totalPossibilities = 0;
      int numBusted = 0;
      
      // edge condition which should never be the case in reality
      if (shoe.size() == 1 && !dealerHasToHit(hitsOnSoft17,shoe.get(0),dealerShowing))
         totalPossibilities = Integer.MAX_VALUE;
      else
      {
         for (int i = 0; i < shoe.size(); i++)
         {
            Card dealerDown = shoe.get(i);
            if (!dealerHasToHit(hitsOnSoft17,dealerDown,dealerShowing))
               totalPossibilities += (shoe.size() - 1); // -1 because of the case where i == j, in any case there's no busting here.
            else
            {
               for (int j = 0; j < shoe.size(); j++)
               {
                  if (j != i)
                  {
                     totalPossibilities++;
                     if (value(makeHand(dealerShowing,dealerDown,shoe.get(j))) > 21)
                        numBusted++;
                  }
               }
            }
         }
      }
      
      return new Odds(numBusted,totalPossibilities);
   }
   
   public static Odds oddsDealerWillDraw(List<Card> hand, Card dealerShowing, int numDecks)
   {
      return oddsDealerWillDraw(true,hand,dealerShowing,numDecks);
   }
   
   public static Odds oddsDealerWillDraw(boolean hitsOnSoft17, List<Card> hand, Card dealerShowing, int numDecks)
   {
      List<Card> shoe = flatten(makeDecks(numDecks));
      removeCards(shoe,hand);
      removeCard(shoe,dealerShowing);
      int totalPossibilities = 0;
      int numDealerHasToHit = 0;
      
      for (int i = 0; i < shoe.size(); i++)
      {
         Card dealerDown = shoe.get(i);
         totalPossibilities++;
         if (dealerHasToHit(hitsOnSoft17,dealerDown,dealerShowing))
            numDealerHasToHit++;
      }
      
      return new Odds(numDealerHasToHit,totalPossibilities);
   }

}