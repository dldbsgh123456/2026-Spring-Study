package com.sist.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sist.service.FoodService;
import com.sist.vo.FoodVO;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class FoodController {
    private final FoodService fService;
    
    // 목록 출력
    // 매개변수 => 요청값을 받는다
    /*
     *   데이터값 받는 방법
     *      = 모든 데이터값은 String
     *      = 데이터형에 맞게 받을 수 있다
     *      = 커맨드 객체 : VO단위로
     *      = 내장 객체 : request,response,session,model... 
     * 
     */
    @GetMapping("main/main.do")
    public String main_main(String page,Model model) // request가 사라짐  @RequestParam("page")  String page 앞에 생략 가능 원래는 붙여야 했었음
    {
    	if(page==null)
    		page="1";
    	int curpage=Integer.parseInt(page);
    	int start=(curpage*12)-12;
    	List<FoodVO> list=fService.foodListData(start);
    	int totalpage=fService.foodTotalPage();
    	
    	final int BLOCK=10;
    	int startPage=((curpage-1)/BLOCK*BLOCK)+1;
    	int endPage=((curpage-1)/BLOCK*BLOCK)+BLOCK;
    	
    	if(endPage>totalpage)
    	    endPage=totalpage;
    	
    	// 전송
    	model.addAttribute("list",list);
    	model.addAttribute("curpage",curpage);
    	model.addAttribute("totalpage",totalpage);
    	model.addAttribute("startPage",startPage);
    	model.addAttribute("endPage",endPage);
    	// => 보안 중심 : request(ip포함) => 가급적이면 request사용금지 권장
    	// => request / response => Cookie
    	model.addAttribute("main_jsp","../main/home.jsp");
    	return "main/main";
    }
}
