package com.sist.main;

import lombok.Data;

@Data
public class Sawon {
      private int sabun;
      private String name;
      private String dept;
      private String job;
      private int pay;
      
      public void print()
      {
    	  System.out.println("사번:"+sabun);
    	  System.out.println("이름:"+name);
    	  System.out.println("부서:"+dept);
    	  System.out.println("직위:"+job);
    	  System.out.println("연봉:"+pay);
      }
}
