package com.sist.service;

import java.util.List;
import java.util.Map;
import com.sist.vo.FoodVO;

public interface FoodService {
	   public List<FoodVO> foodListData(int start);
	   public int foodTotalPage();
	   public List<FoodVO> foodFindListData(Map map);
	   public int foodFindTotalPage(Map map);
	   public FoodVO foodDetailData(int no);
}