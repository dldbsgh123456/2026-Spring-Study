package com.sist.main;
import java.util.*;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sist.service.*;
import com.sist.vo.*;
/*
 *    1. DI : 클래스와 클래스의 연관 관계 설정 (클래스 관리를 위한 정의)
 *       = XML => 예전 버전 ( 2.5 ---> 4.0 )
 *       = Annotation => (4.0 이상) => 7버전
 *         => 5버전 => Boot => 6.0
 *       = XML + Annotation  => 현재 실무
 *         공통으로 적용 : 라이브러리  => XML
 *         사용자 정의 (개발자) => Annotation 
 *    2. AOP : 스프링에 등록된 모든 클래스에 적용이 필요한 경우
 *             자동 호출
 *             PointCut : 어떤 메소드 (적용될 메소드)
 *             JoinPoint : 메소드 어느 위치에서 호출할지 결정
 *               Before / After / Around 
 *    3. MVC => Security , Transaction , WebSocket
 *    
 *    4. 인터셉트 => AOP와 겹치는 부분이 있다
 *         => main.do
 *         => 자동 로그인  
 *    5. 공통예외처리 : controllerAdvice
 *    6. 보안 : security
 *    7/ 실시간 : task 
 * 
 * 
 */
public class MainClass2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
        ApplicationContext app=new ClassPathXmlApplicationContext("app.xml");
        EmpService service=(EmpService)app.getBean("eService");
        List<EmpVO> eList=service.empListData();
        List<DeptVO> dList=service.deptListData();
        
        /*for(EmpVO vo:eList)
        {
        	System.out.println(vo.getEmpno()+" "+vo.getEname()+" "+vo.getJob()+" "+vo.getDbday());
        }*/
        System.out.println("====================================================================");
        /*
        for(DeptVO vo:dList)
        {
        	System.out.println(vo.getDeptno()+" "+vo.getDname()+" "+vo.getLoc());
        }*/
	}
}
