package com.sist.vo;

import lombok.Data;
// 사용자 정의 데이터형 => 스프링에서 관리하지 않는다
@Data
public class DeptVO {
    private int deptno;
    private String dname,loc;
}
