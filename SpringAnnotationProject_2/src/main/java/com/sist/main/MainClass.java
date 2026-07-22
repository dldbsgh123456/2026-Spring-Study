package com.sist.main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.sist.auto.Oracle;

@Component
public class MainClass {

	@Autowired
	@Qualifier(value="oracleDB")
	private Oracle ora;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
        ApplicationContext app=new ClassPathXmlApplicationContext("app.xml");
        
        MainClass mc=(MainClass)app.getBean("mainClass");
        mc.ora.connection();
        mc.ora.disconnection();
	}

}
