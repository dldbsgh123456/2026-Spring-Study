package com.sist.main3;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainClass {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
        AnnotationConfigApplicationContext app=new AnnotationConfigApplicationContext(DAOConfig.class);
        MyDAO dao=(MyDAO)app.getBean("myDAO");
        dao.dbselect();
        dao.dbinsert();
        dao.dbupdate();
        dao.dbdelete();
        
        app.close();
	}

}
