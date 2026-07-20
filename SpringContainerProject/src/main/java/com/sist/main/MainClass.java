package com.sist.main;
/*
 *    SpringFrameWork 
 *      자바기반 (코틀린) => 애플리케이션 개발을 위한 기본 틀 제공
 *                                         -------
 *      
 *      핵심 : 결합성이 낮은 프로그램 개발 (유지 보수)
 *            클래스 수정 => 다른 클래스에 영향을 미침
 *            ------------------------------- 방지
 *            
 *            객체 생성 / 관리 / 객체 소멸까지 담당
 *            
 *      => DI  (객체 연결)
 *      => AOP (공통 기반의 기능을 모아서 자동 처리)
 *      => MVC (웹 요청 처리)
 *      => ORM (데이터베이스 연동) : MyBatis / JPA
 *      => Security (인증 / 인가)
 *      --------------------------------------
 *      
 *      
 *      1. Spring Container
 *         ----------------
 *         1) 객체 생성 ~ 객체 소멸 (객체의 생명 주기 관리)
 *            A a=new A();
 *            B b=new B();
 *            -------------- 스프링에서 생성
 *            @Autowired (자동 주입)
 *            A a; => 싱글톤으로 생성
 *         2) bean
 *            등록
 *            XML이용 : <bean id="" class="" scope="singleton">
 *            @Bean  : 어노테이션 이용
 *         3) Container의 종류
 *            = BeanFactory
 *              => 기본 / DI / Core정도 지원
 *            = ApplicationContext
 *              => 기본 / DI / Core정도 지원 + AOP
 *              
 *         BeanFactory
 *             |
 *      ApplicationContext
 *             |
 *    ----------------------------
 *    |                          |
 *                           WebApplicationContext       
 *  AnnotaionConfigApplicationContext (자바기반)
 *  
 *  등록된 클래스를 모아서 관리
 *  ------------------------
 *    1) 클래스 찾기 (DL)
 *       메소드 / 어노테이션 이용
 *                 | @autowired
 *        |getBean(id)
 *    2) 주입 : 변수의 초기화 / 메소드 호출
 *                          method DI => init-method / destroy-method
 *       DI     setter / 생성자
 *        
 * 
 */
public class MainClass {

}
