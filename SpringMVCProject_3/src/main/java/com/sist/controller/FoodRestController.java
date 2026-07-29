package com.sist.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sist.service.FoodService;
import com.sist.vo.FoodVO;

import lombok.RequiredArgsConstructor;
// "데이터만 전송 => Vue"
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FoodRestController {
   private final FoodService fService;
   
   @GetMapping("food/list_vue.do")
   public Map food_list_vue(int page) {
	   Map map=new HashMap();
	   int start=(page*12)-12;
	   List<FoodVO> list=fService.foodListData(start);
	   int totalpage=fService.foodTotalPage();
	   
	   final int BLOCK=10;
	   int startPage=((page-1)/BLOCK*BLOCK)+1;
	   int endPage=((page-1)/BLOCK*BLOCK)+BLOCK;
	   
	   if(endPage>totalpage)
		   endPage=totalpage;
	   
	   map.put("curpage", page);
	   map.put("totalpage", totalpage);
	   map.put("startPage", startPage);
	   map.put("endPage", endPage);
	   map.put("list", list);
	   return map;
   }
   
   @GetMapping("food/detail_vue.do")
   public FoodVO food_detail(int no)
   {
	   FoodVO vo=fService.foodDetailData(no);
	   return vo;
   }
   
   @RequestMapping("food/find_vue.do")
   public Map food_find(int page,String column,String fd){
	   
	   int curpage=page;
	   int start=(curpage*12)-12;
	   
	   Map map=new HashMap();
	   map.put("column",column);
	   map.put("fd",fd);
	   map.put("start",start);
	   
	   List<FoodVO> list=fService.foodFindListData(map);
	   int totalpage=fService.foodFindTotalPage(map);
	   
	   final int BLOCK=10;
	   int startPage=((curpage-1)/BLOCK*BLOCK)+1;
	   int endPage=((curpage-1)/BLOCK*BLOCK)+BLOCK;
	   
	   if(endPage>totalpage)
		   endPage=totalpage;
	   
	   map=new HashMap();
	   
	   map.put("curpage", page);
	   map.put("totalpage", totalpage);
	   map.put("startPage", startPage);
	   map.put("endPage", endPage);
	   map.put("list", list);
	   return map;
	   
	   
   }
}