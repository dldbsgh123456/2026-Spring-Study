package com.sist.dao;
import java.util.*;
import com.sist.mapper.*;
import com.sist.vo.*;
public class EmpDAO {
    private EmpMapper mapper; // 스프링에서 구현 
    //                   구현된 클래스를 setter를 이용해서 주입 
	public void setMapper(EmpMapper mapper) {
		this.mapper = mapper;
	}
	
	public List<EmpVO> empListData()
	{
		return mapper.empListData();
	}
   
	
	public List<EmpVO> empAllData()
	{
		return mapper.empAllData();
	}
}
