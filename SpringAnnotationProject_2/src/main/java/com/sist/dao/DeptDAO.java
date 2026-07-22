package com.sist.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sist.mapper.DeptMapper;
import com.sist.vo.DeptVO;

import java.util.*;
/*
 *      public class A
 *      {
 *         @Autowired ==> FIELD
 *         private B b;
 *      
 *         @Autowired ==> CONSTRUCTOR
 *         public A(){}
 *         
 *         @Autowired ==> METHOD
 *         public void display(@Autowired B b){}
 *                                | PARAMETER         
 *         @Autowired
 *         @Qualifier ANNOTATION_TYPE
 *      }
 * 
 * 
 * 
 */
@Repository // 데이터베이스 연결  
public class DeptDAO {
	@Autowired
    private DeptMapper mapper;
    
    // 사용은 객체주소 
    
    public List<DeptVO> deptListData()
    {
    	return mapper.deptListData();
    }
}
