package com.collectiondesk.app;
import java.math.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

/** All stored money is integer minor units. Interest never compounds. */
public final class Ledger {
 public static class Payment { public long cents; public LocalDate date; public Payment(long c,String d){cents=c;date=LocalDate.parse(d);} }
 public static class Balance { public long principal,fees,paid; public long total(){return principal+fees;} }
 public static long money(String text) {
  BigDecimal n=new BigDecimal(text.trim());
  if(n.signum()<=0 || n.scale()>2 || n.compareTo(new BigDecimal("1000000000"))>0) throw new IllegalArgumentException("Enter a positive amount up to 1 billion, with at most 2 decimals.");
  return n.movePointRight(2).longValueExact();
 }
 static long units(LocalDate start,LocalDate end,boolean weekly){long d=Math.max(0,ChronoUnit.DAYS.between(start,end));return weekly?(d+6)/7:d;}
 public static Balance balance(long principal,String due,boolean weekly,List<Payment> payments,LocalDate asOf){
  Balance b=new Balance();b.principal=principal;
  LocalDate start=LocalDate.parse(due).plusDays(7),last=start;
  BigDecimal accrued=BigDecimal.ZERO;
  List<Payment> ordered=new ArrayList<>(payments);ordered.sort(Comparator.comparing(p->p.date));
  for(Payment p:ordered){
   if(p.date.isAfter(asOf))continue;
   accrued=accrued.add(charge(b.principal,units(start,p.date,weekly)-units(start,last,weekly),weekly));
   long fee=accrued.setScale(0,RoundingMode.HALF_UP).longValueExact();
   if(p.cents<=0 || p.cents>b.principal+fee)throw new IllegalArgumentException("Payment exceeds the balance on its date.");
   long feePaid=Math.min(fee,p.cents);
   accrued=feePaid==fee?BigDecimal.ZERO:accrued.subtract(BigDecimal.valueOf(feePaid));
   b.principal-=p.cents-feePaid;b.paid+=p.cents;if(p.date.isAfter(last))last=p.date;
  }
  accrued=accrued.add(charge(b.principal,units(start,asOf,weekly)-units(start,last,weekly),weekly));
  b.fees=accrued.setScale(0,RoundingMode.HALF_UP).longValueExact();return b;
 }
 static BigDecimal charge(long principal,long units,boolean weekly){
  if(units<=0 || principal<=0)return BigDecimal.ZERO;
  return BigDecimal.valueOf(principal).multiply(new BigDecimal("0.015")).multiply(BigDecimal.valueOf(units)).divide(BigDecimal.valueOf(weekly?1:7),16,RoundingMode.HALF_UP);
 }
}
