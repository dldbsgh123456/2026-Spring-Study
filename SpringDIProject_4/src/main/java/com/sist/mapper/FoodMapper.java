package com.sist.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.sist.vo.FoodVO;


public interface FoodMapper {
	 @Select("SELECT no,name,address,phone "
			   +"FROM food "
			   +"ORDER BY no ASC "
			   +"OFFSET #{start} ROWS FETCH NEXT 10 ROWS ONLY")
	   public List<FoodVO> foodListData(int start);
}
