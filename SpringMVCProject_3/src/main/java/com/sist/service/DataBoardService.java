package com.sist.service;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import com.sist.vo.DataBoardVO;

public interface DataBoardService {
	  public List<DataBoardVO> databoardListData(int start);	  
	  public int databoardTotalPage();	  
	  public void databoardInsert(DataBoardVO vo); 
}
