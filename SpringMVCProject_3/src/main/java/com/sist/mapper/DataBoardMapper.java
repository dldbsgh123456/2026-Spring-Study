package com.sist.mapper;
import java.util.*;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import com.sist.vo.DataBoardVO;


public interface DataBoardMapper {
  @Select("SELECT no,subject,name,TO_CHAR(regdate,'yyyy-mm-dd') as dbday,hit "
		 +"FROM springDataBoard "
		 +"ORDER BY no DESC "
		 +"OFFSET #{start} ROWS FETCH NEXT 10 ROWS ONLY")
  public List<DataBoardVO> databoardListData(int start);
  
  @Select("SELECT CEIL(COUNT(*)/10.0) FROM springDataBoard")
  public int databoardTotalPage();
  
  @Insert("INSERT INTO springDataBoard(no,name,subject,content,pwd,"
		 +"filename,filesize,filecount) "
		 +"VALUES(sd_no_seq.nextval,#{name},"
		 +"#{subject},#{content},#{pwd},#{filename},#{filesize},#{filecount})")
  public void databoardInsert(DataBoardVO vo); 
}
