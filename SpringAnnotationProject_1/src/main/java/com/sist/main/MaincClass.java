package com.sist.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
/*
 *    어노테이션
 *    -------- 80% 20% => XML
 *    1) 메모리 할당용 클래스 선정 (클래스 구분) => 찾기/구분
 *       ***@Controller
 *          MVC 에서 View(JSP) 변환하는 컨트롤러
 *          => JSP / ThymeLeaf => 화면 변경
 *       ***@RestController
 *          JSON / XML 데이터를 반환하는 경우 => Rest API 컨트롤러
 *          => 내부적으로 변환이 된다 (자동으로 JSON으로 변환)
 *       @Component
 *          => 메모리 할당 => 일반 클래스
 *          => 모든 클래스의 상속 (상위 클래스)
 *       @Service
 *          => 비즈니스 로직 계층
 *             --------- DAO , Manager 연결
 *          User ==DispatcherServlet == Service == DAO == Oracle
 *                       |
 *                    JSP / HTML
 *       @Repository
 *          => DAO : 데이터베이스 연동
 *       
 *       @Configuration
 *          => XML을 대체 => 순수하게 자바 셋팅
 *          
 *       ***@ControllerAdvice / @RestControllerAdvice
 *          => 공통예외처리 
 * 
 * 
 */
public class MaincClass {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
        ApplicationContext app=new ClassPathXmlApplicationContext("app.xml");
        Member m=(Member)app.getBean("mem"); // key가 없으면 디폴트는 메소드이름 ( 첫글자 소문자로)
        Sawon s=(Sawon)app.getBean("sawon");
        Student sd=(Student)app.getBean("student");
        
        System.out.println(m);
        System.out.println(s);
        System.out.println(sd);
        
        Food f=new Food();
	}

}
