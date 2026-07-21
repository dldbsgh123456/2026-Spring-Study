package com.sist.mapper;
import java.util.*;

import org.apache.ibatis.annotations.Select;

import com.sist.vo.SeoulVO;

public interface SeoulMapper {
   @Select("SELECT no,title,msg,address "
		   +"FROM seoul_location "
		   +"ORDER BY no ASC "
		   +"OFFSET #{start} ROWS FETCH NEXT 10 ROWS ONLY")
   public List<SeoulVO> seoulListData(int start);
}
