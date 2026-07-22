package com.sist.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sist.dao.DeptDAO;
import com.sist.dao.EmpDAO;
import com.sist.vo.DeptVO;
import com.sist.vo.EmpVO;

// 비즈니스 로직 
// 로그인 / 보안 (암호화,복호화(암호화된 데이터를 원래 상태로 되돌리는 것) / 문자열 결합 .... 외부에서 데이터 읽기
// => 채팅 , 실시간 전송
/*
 *   스테레오타입
 *   ---------> 클래스 메모리 할당 요청
 *              1) @Component : 일반 클래스 => ChatManager,NewsManager...,MainClass 
 *              2) @Repository : DAO 데이터베이스 연동
 *              3) @Service : DAO+DAO => 기타 기능
 *              4) => web @Controller / @RestController
 *                        @ControllerAdvice => 예외처리
 *                        
 *                        
 *                        Service <====> Repository <====> 오라클
 *                           |
 *                       Controller
 *                           |
 *                          JSP / HTML
 * 
 */
@Service("eService")
public class EmpServiceImpl implements EmpService{

	@Autowired  
	private EmpDAO eDao;
	@Autowired // 필드 하나씩만 위에 올릴 수 있음
	private DeptDAO dDao;
	
	@Override
	public List<EmpVO> empListData() {
		// TODO Auto-generated method stub
		return eDao.empListData();
	}

	@Override
	public List<DeptVO> deptListData() {
		// TODO Auto-generated method stub
		return dDao.deptListData();
	}

}
