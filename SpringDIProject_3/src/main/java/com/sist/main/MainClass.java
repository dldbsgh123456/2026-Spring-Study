package com.sist.main;
import java.util.*;
public class MainClass {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path="C:\\springDev\\springStudy\\SpringDIProject_3\\src\\main\\java\\app.xml";
        ApplicationContext app=new ClassPathXmlApplicationContext(path);
        Sawon s=(Sawon)app.getbean("sa");
        System.out.println("사번:"+s.getSabun());
        System.out.println("이름:"+s.getName());
        System.out.println("부서:"+s.getDept());
        System.out.println("직위:"+s.getJob());
	}

}
