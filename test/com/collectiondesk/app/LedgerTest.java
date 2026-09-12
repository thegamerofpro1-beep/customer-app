package com.collectiondesk.app;
import java.time.*;import java.util.*;
public class LedgerTest{
 static int checks=0;
 static void check(long expected,long actual){checks++;if(expected!=actual)throw new AssertionError("Expected "+expected+", got "+actual);}
 static Ledger.Balance b(int days,boolean w,Ledger.Payment...p){return Ledger.balance(100000,"2026-01-01",w,Arrays.asList(p),LocalDate.parse("2026-01-01").plusDays(days));}
 public static void main(String[] a){check(0,b(7,false).fees);check(214,b(8,false).fees);check(1500,b(14,false).fees);check(3000,b(21,false).fees);check(0,b(7,true).fees);check(1500,b(8,true).fees);check(1500,b(14,true).fees);check(3000,b(15,true).fees);check(0,b(30,false,new Ledger.Payment(100000,"2026-01-08")).total());check(50000,b(14,false,new Ledger.Payment(51500,"2026-01-15")).principal);check(750,b(21,false,new Ledger.Payment(51500,"2026-01-15")).fees);check(0,b(100,false,new Ledger.Payment(101500,"2026-01-15")).total());check(750,b(21,true,new Ledger.Payment(51500,"2026-01-15")).fees);check(101500,b(14,false,new Ledger.Payment(1000,"2026-02-01")).total());check(12345,Ledger.money("123.45"));try{Ledger.money("1.001");throw new AssertionError();}catch(IllegalArgumentException e){checks++;}try{b(0,false,new Ledger.Payment(100001,"2026-01-01"));throw new AssertionError();}catch(IllegalArgumentException e){checks++;}check(1000,b(14,false,new Ledger.Payment(500,"2026-01-15")).fees);check(0,b(-2,false).fees);System.out.println(checks+" ledger checks passed");}
}
