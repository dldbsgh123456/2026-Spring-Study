package com.sist.main2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// => Boot에서 중심 
@Configuration
public class SawonConfig {
	/*
	 *     <bean id="sa" class="com.sist.main2.Sawon"
	 *        p:sabun="1"
	 *        p:name="심청이"
	 *        p:dept="개발부"
	 *        p:loc="부산"
	 *     />
	 * 
	 */	
   @Bean("sa")
   public Sawon sawon()
   {
	   Sawon s=new Sawon();
	   s.setSabun(1);
	   s.setName("심청이");
	   s.setDept("개발부");
	   s.setLoc("부산");
	   return s;
   }
}
