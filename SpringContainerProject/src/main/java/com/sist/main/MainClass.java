package com.sist.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

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
	     // 데이터베이스 설정 (driver , url , username , password)
         public static void main(String[] args) {
			// Container에 등록
        	/*ApplicationContext app=new ClassPathXmlApplicationContext("application.xml"); 
        	// 클래스 메모리 할당
        	// 등록된 컨테이너에서 클래스 => 객체 읽기
        	Sawon sa=(Sawon)app.getBean("sa");
            sa.print();
            System.out.println("--------------");        	
        	Sawon sa1=(Sawon)app.getBean("sa1");
        	sa1.print();
            System.out.println("--------------");             
        	Sawon sa2=(Sawon)app.getBean("sa2");
        	sa2.print();
            System.out.println("--------------");
            Sawon sa3=new Sawon();
            sa3.print();*/
        	GenericApplicationContext app=new GenericXmlApplicationContext("app.xml");
            
            Member m1=(Member)app.getBean("mem1"); // getBean은 오브젝트라서 형변환 해줘야 함
            m1.print();
            app.close();
            /* System.out.println("--------------");
            Member m2=(Member)app.getBean("mem2");
            m2.print();
            System.out.println("--------------");
            Member m3=app.getBean("mem3",Member.class);
            m3.print();
            System.out.println("--------------");
            Member m4=app.getBean("mem4",Member.class);
            m4.print();
            System.out.println("--------------");*/
            /*
             *   클래스 관리 영역 : 컨테이너
             *   -------------- XML을 파싱 => 클래스 저장
             *   |클래스 메모리 할당
             *   |Map에 저장
             *    ------------------------- init-method
             *   |활용 =====> 개발자 사용
             *   |소멸
             *    ------------------------- destroy-method
             *   컨테이너 클래스
             *      BeanFactory
             *           |
             *     ApplicationContext
             *           |--- GenericApplicationContext : 소멸기능
             *           |--- AnnotationConfigApplicationContext 일반 자바로 구현
             *     WebApplicationContext : MVC
             *     
             *     
             *     클래스 => 객체 생성
             *           | => 초기값이 필요할 때가 있다
             *                setter DI => setxxx()를 이용해서 값을 저장
             *                contructor DI => 생성자 
             *                method DI => driver 등록 / 오라클 닫기
             *                             크롤링
             *                             init-method : 드라이버 설정
             *                             destroy-method : 드라이버 닫기
             *                -----------------------------------
             *                
             *                스프링을 통해서 필요한 값을 주입
             *       
             *       DI    /     AOP    /   (MVC    /   ORM)
             *       ---- 75%   ----- 5%    나머지 기타
             *                       
             *           
             * 
             * 
             */
		}
}
