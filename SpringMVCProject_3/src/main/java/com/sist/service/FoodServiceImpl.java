package com.sist.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import com.sist.mapper.*;
import com.sist.vo.*;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService{
    private final FoodMapper mapper;
   /*@Autowired
   public FoodServiceImpl(FoodMapper mapper)
   {
	   this.mapper=mapper;
   }*/

	@Override
	public List<FoodVO> foodListData(int start) {
		// TODO Auto-generated method stub
		return mapper.foodListData(start);
	}
	
	@Override
	public int foodTotalPage() {
		// TODO Auto-generated method stub
		return mapper.foodTotalPage();
	}
	
	@Override
	public List<FoodVO> foodFindListData(Map map) {
		// TODO Auto-generated method stub
		return mapper.foodFindListData(map);
	}
	
	@Override
	public int foodFindTotalPage(Map map) {
		// TODO Auto-generated method stub
		return mapper.foodFindTotalPage(map);
	}

	@Override
	public FoodVO foodDetailData(int no) {
		// TODO Auto-generated method stub
		return mapper.foodDetailData(no);
	}
}