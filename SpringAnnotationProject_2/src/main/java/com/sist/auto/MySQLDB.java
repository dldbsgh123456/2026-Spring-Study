package com.sist.auto;

import org.springframework.stereotype.Repository;

@Repository
public class MySQLDB implements Oracle{

	@Override
	public void connection() {
		// TODO Auto-generated method stub
		System.out.println("MySQL 연결");
	}

	@Override
	public void disconnection() {
		// TODO Auto-generated method stub
		System.out.println("MySQL 연결 해제");
	}

}
