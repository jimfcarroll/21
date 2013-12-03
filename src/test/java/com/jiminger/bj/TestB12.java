package com.jiminger.bj;
import static com.jiminger.bj.B21.dealerHasToHit;
import static com.jiminger.bj.B21.makeDeck;
import static com.jiminger.bj.B21.*;
import static com.jiminger.bj.B21.oddsOfBust;
import static com.jiminger.bj.B21.oddsOfDealerBustOnNextCardIfDealerHits;
import static com.jiminger.bj.B21.value;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class TestB12
{
   @Test
   public void testValue()
   {
      assertEquals(21,value(Arrays.asList(
            new Card(Suit.club,Face.ace),
            new Card(Suit.spade,Face.jack)
            )));
   }
   
   @Test
   public void testSimpleOddsOfBust()
   {
      assertEquals(1.0,
            oddsOfBust(
                  Arrays.asList(
                        new Card(Suit.club,Face.ten),
                        new Card(Suit.spade,Face.jack),
                        new Card(Suit.spade,Face.queen)),
                        new Card(Suit.spade,Face.king),20).value(), 0.0);

      assertEquals(0.0,
            oddsOfBust(
                  Arrays.asList(
                        new Card(Suit.club,Face.two),
                        new Card(Suit.spade,Face.two)),
                        new Card(Suit.spade,Face.king),1).value(), 0.0);

      assertEquals(0.0,
            oddsOfBust(
                  Arrays.asList(
                        new Card(Suit.club,Face.jack),
                        new Card(Suit.spade,Face.ace)),
                        new Card(Suit.spade,Face.king),1).value(), 0.0);
   }
   
   @Test
   public void testOddsOfBust()
   {
      // odds of busting when the dealer is showing a 10 card is less
      // than the odds of busting when the dealer is showing a 2
      assertTrue(
            oddsOfBust(
                  Arrays.asList(
                        new Card(Suit.club,Face.jack),
                        new Card(Suit.spade,Face.two)),
                        new Card(Suit.heart,Face.two),1).value() >
            oddsOfBust(
                  Arrays.asList(
                        new Card(Suit.club,Face.jack),
                        new Card(Suit.spade,Face.two)),
                        new Card(Suit.spade,Face.king),1).value());
   }
   
   @Test
   public void testDealerHasToHit()
   {
      assertFalse(dealerHasToHit(new Card(Suit.club,Face.jack),new Card(Suit.club,Face.jack)));
      assertTrue(dealerHasToHit(new Card(Suit.club,Face.six),new Card(Suit.club,Face.ace)));
      assertFalse(dealerHasToHit(false,new Card(Suit.club,Face.six),new Card(Suit.club,Face.ace)));
      assertFalse(dealerHasToHit(new Card(Suit.club,Face.seven),new Card(Suit.club,Face.ace)));
   }

   @Test
   public void testSimpleOddsOfDealerBust()
   {
      assertEquals(1.0,
            oddsOfDealerBustOnNextCardIfDealerHits(
                  Arrays.asList(
                        new Card(Suit.club,Face.ten),
                        new Card(Suit.spade,Face.jack),
                        new Card(Suit.spade,Face.queen)),
                        new Card(Suit.spade,Face.king),1).value(), 0.0);
   }
   
   public final static List<Card> emptyHand = Collections.emptyList();
   
   @Test
   public void testOddsDealerHasToHit()
   {
      Card dealerCard = new Card(Suit.club,Face.two);
      assertEquals(1.0,oddsDealerWillDraw(emptyHand, dealerCard,8).value(),0.0);
      
      // now force calculate the odds if I'm (strangely enough) holding every other card
      dealerCard = new Card(Suit.club,Face.queen);
      Set<Card> deck = makeDeck();
      removeCard(deck,dealerCard);
      
      List<Card> cards = new ArrayList<Card>(deck);
      assertTrue(oddsDealerWillDraw(cards,dealerCard,1).wtf());
      
      // now lets do this when we leave a card in the shoe that
      // forces guarantees the dealer will not bust.
      assertTrue(cards.remove(new Card(Suit.diamond,Face.six))); // this means the dealer will have to draw
      
      assertEquals(1.0,oddsDealerWillDraw(cards,dealerCard,1).value(),0.0);
      
      // now leave the only card that means the dealer wont have to hit.
      cards = new ArrayList<Card>(deck);
      assertTrue(cards.remove(new Card(Suit.diamond,Face.ten))); // this means the dealer wont have to draw
      assertEquals(0.0,oddsDealerWillDraw(cards,dealerCard,1).value(),0.0);
   }
   
   @Test
   public void testOddsOfDealerBust()
   {
      // now force calculate the odds if I'm (strangely enough) holding every other card
      Card dealerCard = new Card(Suit.club,Face.queen);
      Set<Card> deck = makeDeck();
      removeCard(deck,dealerCard);
      
      List<Card> cards = new ArrayList<Card>(deck);
      assertTrue(oddsOfDealerBustOnNextCardIfDealerHits(cards,dealerCard,1).wtf());
      
      // now lets do this when we leave a card in the shoe that
      // forces guarantees the dealer will not bust.
      assertTrue(cards.remove(new Card(Suit.diamond,Face.six))); // this means the dealer will have to draw
      assertTrue(cards.remove(new Card(Suit.club,Face.six))); // this means the dealer will bust
      
      assertEquals(1.0,oddsOfDealerBustOnNextCardIfDealerHits(cards,dealerCard,1).value(),0.0);
      
      // now lets remove a card that give the dealer a 1/3 chance
      // dealer can have any combination of down and hit cards
      //    if the 2 is down then there's a 100% chance the dealer wont bust. This covers 2 possibilities
      //    if there's a 6 down then there's a 50/50 chance the dealer will bust 
      //    if the other 6 is down then there's another 50/50 chance the dealer will bust.
      // all of this means there's a 2 out of 6 chance to bust
      assertTrue(cards.remove(new Card(Suit.club,Face.two)));
      Odds odds = oddsOfDealerBustOnNextCardIfDealerHits(cards,dealerCard,1);
      assertEquals(6,odds.getOutOf());
      assertEquals(2,odds.getNumber());
      
      // now leave the only card that means the dealer wont have to hit.
      cards = new ArrayList<Card>(deck);
      assertTrue(cards.remove(new Card(Suit.diamond,Face.ten))); // this means the dealer wont have to draw
      assertEquals(0.0,oddsOfDealerBustOnNextCardIfDealerHits(cards,dealerCard,1).value(),0.0);
      
      // now let's add another card (for the dealer) the means that they will either bust or not have to hit
      assertTrue(cards.remove(new Card(Suit.spade,Face.two)));
      assertEquals(0.5,oddsOfDealerBustOnNextCardIfDealerHits(cards,dealerCard,1).value(),0.0);
   }
   
}
