package com.sist.aop;

import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import com.sist.vo.DeptVO;
import com.sist.vo.EmpVO;

// 공통으로 사용되는 클래스
@Aspect
@Component
/*
 *    JoinPoint => 시점 (어디서 호출)
 *      @Before
 *      @After
 *      @After-Returning
 *      @Around
 *      @After-Throwing
 *    PointCut => 어떤 메소드에 적용
 *    ----------- Advice 
 *    
 *    
 *    public String display()
 *    {
 *       @Before
 *       try
 *       {
 *          ---------------@Around => before
 *             => setAutoCommit(false)
 *             
 *             => commit()
 *             
 *             => 어떤 메소드 호출 => 시작
 *             
 *             => 종료 ==> log
 *           --------------@Around => after
 *           로그 / 트랜젝션 => AOP
 *       }catch(Exception e)
 *       {
 *          --------------- @After-Throwing : 에러 처리
 *       }
 *       finally
 *       {
 *          ----------@After
 *       }
 *       
 *       return "값" ------> @After-Returning : 리턴값 받기
 *    }
 *    
 *    * com.sist.service.EmpServiceImpl.*(..))
 *    --리턴형                클래스      메소드 매개변수
 *                                           | 모든 매개변수
 *      |모든 리턴형                      |모든 메소드
 * 
 */
public class EmpAOP {
     @Before("execution(* com.sist.service.EmpServiceImpl.*(..))")
     public void before()
     {
    	 System.out.println("메소드 진입전...");
     }
     @After("execution(* com.sist.service.EmpServiceImpl.*(..))")
     public void after()
     {
    	 System.out.println("메소드 종료전...");
     }
     @Around("execution(* com.sist.service.EmpServiceImpl.*(..))")
     // 리팩토링 / 코드리뷰
     public Object around(ProceedingJoinPoint jp) throws Throwable
     {
    	 Object obj=null;
    	 long start=System.currentTimeMillis();
    	 System.out.println("호출된 메소드"+jp.getSignature().getName());
    	 // 메소드 호출 => 수행
    	 obj=jp.proceed();
    	 long end=System.currentTimeMillis();
    	 System.out.println("걸린 시간:"+(end-start)+"MS");
    	 return obj;
     }
     
     @AfterReturning(value="execution(* com.sist.service.EmpServiceImpl.*(..))",returning ="obj")
     public void afterReturn(Object obj) // returning 이랑 값이 같아야 함 
     {
    	 if(obj instanceof List)
    	 {
    		List<?> list=(List<?>)obj;
    		for(Object item:list)
    		{
    			if(item instanceof EmpVO)
    			{
    				EmpVO vo=(EmpVO)item;
    				System.out.println(vo.getEname()+" "+vo.getEname()+" "+vo.getJob()+" "+vo.getDbday());   				
    				
    			}
    			
    			if(item instanceof DeptVO)
    			{
    				DeptVO vo=(DeptVO)item;
    				System.out.println(vo.getDeptno()+" "+vo.getDname()+" "+vo.getLoc());   				
    				
    			}
    		}
    	 }
      
     }
     @AfterThrowing(value="execution(* com.sist.service.EmpServiceImpl.*(..))",throwing="ex")
     public void AfterThrowing(Throwable ex)
     {
    	 ex.printStackTrace();
     }
}
