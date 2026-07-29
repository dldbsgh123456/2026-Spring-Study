package com.sist.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sist.mapper.DataBoardMapper;
import com.sist.vo.DataBoardVO;

import lombok.RequiredArgsConstructor;
// vo => Mapper => Service => ServiceImpl => Controller
@Service
@RequiredArgsConstructor
public class DataBoardServiceImpl implements DataBoardService {
       private final DataBoardMapper mapper;

	@Override
	public List<DataBoardVO> databoardListData(int start) {
		// TODO Auto-generated method stub
		return mapper.databoardListData(start);
	}
    
	@Override
	public int databoardTotalPage() {
		// TODO Auto-generated method stub
		return mapper.databoardTotalPage();
	}

	@Override
	public void databoardInsert(DataBoardVO vo) {
		// TODO Auto-generated method stub
		mapper.databoardInsert(vo);
	}
}
