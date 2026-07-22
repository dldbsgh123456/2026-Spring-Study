package com.sist.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sist.dao.FoodDAO;
import com.sist.vo.FoodVO;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
// »ý¼ºÀÚ => @Autowired
public class FoodServiceImpl implements FoodService{
    private final FoodDAO dao;
	
	@Override
	public List<FoodVO> foodListData(int start) {
		// TODO Auto-generated method stub
		return dao.foodListData(start);
	}

	@Override
	public int foodTotalPage() {
		// TODO Auto-generated method stub
		return dao.foodTotalPage();
	}

}