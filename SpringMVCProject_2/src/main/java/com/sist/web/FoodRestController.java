package com.sist.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sist.service.FoodService;

import lombok.RequiredArgsConstructor;
import java.util.*;
import com.sist.vo.*;
@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor

public class FoodRestController {
   
   private final FoodService service;
   @GetMapping("food/list.do")
   public Map food_list(String page)
   {
	   Map map=new HashMap();
	   if(page==null)
		   page="1";
	   int curpage=Integer.parseInt(page);
	   final int ROWSIZE=12;
	   int start=(curpage*ROWSIZE)-ROWSIZE;
	   List<FoodVO> list=service.foodListData(start);
	   int totalpage=service.foodTotalPage();
	   
	   map.put("list", list);
	   map.put("curpage", curpage);
	   map.put("totalpage", totalpage);
	   
	   return map;
   }
}