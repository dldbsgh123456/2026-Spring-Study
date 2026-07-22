package com.sist.mapper;
import java.util.*;

import org.apache.ibatis.annotations.Select;

import com.sist.vo.EmpVO;
public interface EmpMapper {
      @Select("SELECT empno,ename,job,TO_CHAR(hiredate,'yyyy-mm-dd') "
    		 +"as dbday,sal "
    		 +"FROM emp "
    		 +"ORDER BY empno ASC")
      public List<EmpVO> empListData(); // 자동 구현 
}
