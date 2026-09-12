package com.collectiondesk.app;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import org.json.*;
import java.util.*;

public class Database extends SQLiteOpenHelper {
 public Database(Context c){super(c,"collections.db",null,1);}
 public void onConfigure(SQLiteDatabase d){d.setForeignKeyConstraintsEnabled(true);}
 public void onCreate(SQLiteDatabase d){
  d.execSQL("CREATE TABLE customers(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT NOT NULL,phone TEXT NOT NULL,address TEXT NOT NULL,emergency_name TEXT NOT NULL,emergency_phone TEXT NOT NULL,notes TEXT NOT NULL)");
  d.execSQL("CREATE TABLE invoices(id INTEGER PRIMARY KEY AUTOINCREMENT,customer_id INTEGER NOT NULL REFERENCES customers(id),reference TEXT NOT NULL UNIQUE,amount INTEGER NOT NULL CHECK(amount>0),issued TEXT NOT NULL,due TEXT NOT NULL,weekly INTEGER NOT NULL,notes TEXT NOT NULL)");
  d.execSQL("CREATE TABLE payments(id INTEGER PRIMARY KEY AUTOINCREMENT,invoice_id INTEGER NOT NULL REFERENCES invoices(id),amount INTEGER NOT NULL CHECK(amount>0),date TEXT NOT NULL,method TEXT NOT NULL,note TEXT NOT NULL,voided INTEGER NOT NULL DEFAULT 0,void_reason TEXT NOT NULL DEFAULT '')");
  d.execSQL("CREATE INDEX ix_invoices_customer ON invoices(customer_id)");d.execSQL("CREATE INDEX ix_payments_invoice ON payments(invoice_id,date)");
 }
 public void onUpgrade(SQLiteDatabase d,int oldV,int newV){throw new IllegalStateException("Unsupported database version");}
 public List<JSONObject> rows(String sql,String... args){List<JSONObject> a=new ArrayList<>();try(Cursor c=getReadableDatabase().rawQuery(sql,args)){while(c.moveToNext()){JSONObject o=new JSONObject();for(int i=0;i<c.getColumnCount();i++){try{o.put(c.getColumnName(i),c.getType(i)==Cursor.FIELD_TYPE_INTEGER?c.getLong(i):c.getString(i));}catch(Exception e){throw new IllegalStateException(e);}}a.add(o);}}return a;}
 public JSONObject one(String table,long id){return rows("SELECT * FROM "+table+" WHERE id=?",Long.toString(id)).get(0);}
 public long insert(String table,ContentValues v){return getWritableDatabase().insertOrThrow(table,null,v);}
 public List<Ledger.Payment> payments(long invoice){List<Ledger.Payment> list=new ArrayList<>();for(JSONObject p:rows("SELECT * FROM payments WHERE invoice_id=? AND voided=0 ORDER BY date,id",Long.toString(invoice)))list.add(new Ledger.Payment(p.optLong("amount"),p.optString("date")));return list;}
}
