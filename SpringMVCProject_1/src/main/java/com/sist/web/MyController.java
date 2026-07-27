package com.sist.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;
@RestController
public class MyController {
   @GetMapping("board/update.do")
   public Map board_update()
   {
	   Map map=new HashMap();
	   map.put("a", "홍길동");
	   map.put("b", "홍길동");
	   map.put("c", "홍길동");
	   return map;
   }
}
