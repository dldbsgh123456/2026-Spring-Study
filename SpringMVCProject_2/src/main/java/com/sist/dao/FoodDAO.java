package com.sist.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.sist.mapper.*;
import com.sist.vo.*;
@Repository
public class FoodDAO {
    // Mapper => 스프링에서 구현 => 구현된 클래스의 주소값을 가지고 온다
	@Autowired
	private FoodMapper mapper;
	
	public List<FoodVO> foodListData(int start)
	{
		return mapper.foodListData(start);
	}
	
	public int foodTotalPage()
	{
		return mapper.foodTotalPage();
	}
}