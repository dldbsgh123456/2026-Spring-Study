package com.sist.dao;
import java.util.*;
import com.sist.mapper.*;
import com.sist.vo.*;
public class SeoulDAO {
      private SeoulMapper mapper;

	public void setMapper(SeoulMapper mapper) {
		this.mapper = mapper;
	}
      
	public List<SeoulVO> seoulListData(int start)
	{
		return mapper.seoulListData(start);
	}
    
}
