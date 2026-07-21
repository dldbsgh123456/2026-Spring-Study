package com.sist.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MainClass {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
          ApplicationContext app=new ClassPathXmlApplicationContext("app.xml");
          Sawon s=(Sawon)app.getBean("sa");
          System.out.println("사번:"+s.getSabun());
          System.out.println("이름:"+s.getName());
          System.out.println("부서:"+s.getDept());
          System.out.println("직위:"+s.getJob());
          
	}

}
