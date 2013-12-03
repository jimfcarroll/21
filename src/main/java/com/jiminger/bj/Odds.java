package com.jiminger.bj;

public class Odds
{
   private long number;
   private long outOf;
   
   public Odds(long number, long outOf) { this.number = number; this.outOf = outOf; }
   
   public String toString() { return "" + number + " out of " + outOf + " is " + value(); }
   
   public double value() { return (double)number / (double)outOf; }
   
   public boolean wtf() { return number == 0 && outOf == 0; }
   
   public long getNumber() { return number; }
   public long getOutOf() { return outOf; }
}

