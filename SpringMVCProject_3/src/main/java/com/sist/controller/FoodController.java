package com.sist.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sist.service.FoodService;
import com.sist.vo.FoodVO;

import lombok.RequiredArgsConstructor;
import java.util.*;
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
   public String main_main(String page,Model model)
   {
	   if(page==null)
		   page="1";
	   int curpage=Integer.parseInt(page);
	   //int curpage=page;
	   int start=(curpage*12)-12;
	   List<FoodVO> list=fService.foodListData(start);
	   int totalpage=fService.foodTotalPage();
	   
	   final int BLOCK=10;
	   int startPage=((curpage-1)/BLOCK*BLOCK)+1;
	   int endPage=((curpage-1)/BLOCK*BLOCK)+BLOCK;
	   
	   if(endPage>totalpage)
		   endPage=totalpage;
	   
	   // 전송
	   model.addAttribute("list", list);
	   model.addAttribute("curpage", curpage);
	   model.addAttribute("totalpage", totalpage);
	   model.addAttribute("startPage", startPage);
	   model.addAttribute("endPage", endPage);
	// => 보안 중심 : request(ip포함) => 가급적이면 request사용금지 권장
   	// => request / response => Cookie
	   model.addAttribute("main_jsp", "../main/home.jsp");
	   return "main/main";
   }
   /*
    *     PostMapping / GetMapping
    *         |              |
    *      <form>           <a> , sendRedirect() , localhost.href          
    *       ajax({ 
    *           type:'post'
    *       })
    *      axios.post()
    *      
    *      문제
    *        Get => 화면 폼
    *        Post => 데이터 전송
    *        검색 => GET
    *        검색 요청 => POST
    *        ----------------- + RequestMapping
    *        
    *      PutMapping / DeleteMapping ==> Rest Api
    *        => 다른 프로그램과 연동
    *        => JavaScript : React / Vue
    *        
    *      GetMapping  : Select axios.get()
    *      PostMapping : Insert axios.post()
    *      PutMapping  : Update axios.put()
    *      DeleteMapping : Delete axios.delete() 
    *      
    *      => request를 사용하지 않는다 -> 스프링 오면서 request가 없어져가지고 그럼
    *         ==> 매개변수를 이용해서 => DispatcherServlet으로부터 값을 받는다
    *         ==> 순서는 상관없다 / 키명과 동일한 변수
    *         ==> 애매한 데이터 / null이 있을 경우
    *             (String으로 받는다)
    *         ==> 데이터 전송시에는 Model을 이용한다
    *                          ------ 데이터 전송 객체
    *      => @Controller : Router(화면 이동)
    *         -----------
    *         return형
    *         | = String => 화면 이동 (JSP지정)
    *         | = void => 파일다운로드
    *      => 매개변수
    *         일반 데이터형 , 내장 객체 , 데이터를 모아서 받을 수 있다
    *         --------------------------------------------
    *         display(HttpSession session)
    *         display(String[] hobby)
    *         display(List list)
    *           => <input type="text" name="list[0]">
    *              <input type="text" name="list[1]">
    *              <input type="text" name="list[2]">
    *              <input type="text" name="list[3]">
    *      => (RedirectAttributes a)
    *         a.setAttribute("no",1)
    *         return "redirect:../food/detail.do"
    *         
    *      최근 유행
    *         1. SpringBoot = Vue
    *         2. SpringBoot = React
    *         3. FastApi = Vue , React
    *         4. NodeJs = Vue , React
    *         
    *         JSP (X) => AI 출력이 불가능
    *         ------- HTML (ThymeLeaf)
    *      
    *      요청 = Controller = Mapper = Repository = Service = Controller = JSP
    *         
    */
   @GetMapping("food/detail.do") // detail.do?no=1
   public String food_detail(int no,Model model) //데이터를 전송하는게 있으면 Model이 필요함 
   {
	   FoodVO vo=fService.foodDetailData(no);
	   model.addAttribute("vo",vo);
	   model.addAttribute("main_jsp","../food/detail.jsp");
	   return "main/main";   
   }
   
   @RequestMapping("food/find.do")
   public String food_find(String page,String column,String fd,Model model)
   {
	   // String column,String fd 사용자 요청값 => 요청 처리
	   // => Model을 이용해서 JSP로 전송 => 화면 출력
	   System.out.println("fd :: " + fd );
	   
	   if(column==null)
		   column="address";
	   if(fd==null)
		   fd="마포";
	   if(page==null)
		   page="1";
	   
	   int curpage=Integer.parseInt(page);
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
	   
	   // 전송
	   model.addAttribute("list", list);
	   model.addAttribute("curpage", curpage);
	   model.addAttribute("totalpage", totalpage);
	   model.addAttribute("startPage", startPage);
	   model.addAttribute("endPage", endPage);
	   model.addAttribute("column",column);
	   model.addAttribute("fd",fd);
	   model.addAttribute("main_jsp", "../food/find.jsp");
	   return "main/main";
   }
}