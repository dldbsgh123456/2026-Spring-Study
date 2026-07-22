package com.sist.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.sist.mapper.EmpMapper;
import com.sist.vo.EmpVO;

@Repository // component 도 데이터베이스 연동은 가능함 최상위 오브젝트임
public class EmpDAO {
    // Mapper 읽기
	@Autowired // 스프링에서 자동화 => 구현된 클래스 주소를 넘겨준다
	// 단점 => OOP가 깨진다 => 리플렉션에서 많이 깨짐
	private EmpMapper mapper;
	
	public List<EmpVO> empListData()
	{
		return mapper.empListData();
	}
	
}
