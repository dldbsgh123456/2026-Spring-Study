package com.sist.main3;

import org.springframework.stereotype.Repository;

@Repository
public class MyDAO {
   public void getConnection()
   {
	   System.out.println("오라클 연결...");
   }
   
   public void disConnection()
   {
	   System.out.println("오라클 연결 해제...");
   }
   // OOP => AOP 
   public void dbselect()
   {
	   //getConnection(); // => 공통 모듈
	   System.out.println("데이터 읽기"); // => 핵심모듈
	   //disConnection(); // => 공통 모듈
   }
   public void dbinsert()
   {
	   //getConnection();
	   System.out.println("데이터 추가");
	   //disConnection();
   }
   public void dbupdate()
   {
	   //getConnection();
	   System.out.println("데이터 수정");
	   //disConnection();
   }
   public void dbdelete()
   {
	  // getConnection();
	   System.out.println("데이터 삭제");
	   //disConnection();
   }
}
