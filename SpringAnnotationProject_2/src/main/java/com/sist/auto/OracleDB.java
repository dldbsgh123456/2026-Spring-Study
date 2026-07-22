package com.sist.auto;

import org.springframework.stereotype.Repository;

@Repository
public class OracleDB implements Oracle{

	@Override
	public void connection() {
		// TODO Auto-generated method stub
		System.out.println("오라클 연결");
	}

	@Override
	public void disconnection() {
		// TODO Auto-generated method stub
		System.out.println("오라클 연결 해제");
	}
    
}
