package com.sist.main4;

public class Proxy {
   private MyDAO m;
   public Proxy(MyDAO m)
   {
	   this.m=m;
   }
   public void getConnection()
   {
	   System.out.println("오라클 연결...");
   }
   
   public void disConnection()
   {
	   System.out.println("오라클 연결 해제...");
   }
   // Weaving => 호출
   // AOP 대신 호출 => 대리자 (proxy 패턴)
   public void select()
   {
	   getConnection();
	   m.select();
	   disConnection();
   }
}
