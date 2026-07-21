package com.sist.main3;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DAOAspect {
	@Autowired
    private MyDAO mDAO;
	
	@Before("execution(* com.sist.main3.MyDAO.db*(..))")
	public void before()
	{
		mDAO.getConnection();
	}
	
	@After("execution(* com.sist.main3.MyDAO.db*(..))")
	public void after()
	{
		mDAO.getConnection();
	}
}
