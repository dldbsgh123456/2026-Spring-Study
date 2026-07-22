package com.sist.service;

import java.util.List;

import com.sist.vo.DeptVO;
import com.sist.vo.EmpVO;
// 기능들만 모아줌 
public interface EmpService {
	 public List<EmpVO> empListData();
	 public List<DeptVO> deptListData();
}
