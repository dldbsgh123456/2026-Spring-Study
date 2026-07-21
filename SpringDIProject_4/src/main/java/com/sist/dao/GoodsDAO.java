package com.sist.dao;

import java.util.List;

import com.sist.mapper.GoodsMapper;
import com.sist.vo.GoodsVO;

public class GoodsDAO {
     private GoodsMapper mapper;

	public void setMapper(GoodsMapper mapper) {
		this.mapper = mapper;
	}
	public List<GoodsVO> goodsListData(int start)
	{
		return mapper.goodsListData(start);
	}
     
}
