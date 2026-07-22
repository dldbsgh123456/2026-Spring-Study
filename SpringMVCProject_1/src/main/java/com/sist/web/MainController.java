package com.sist.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sist.vo.BoardVO;

@Controller
/*
 *   Rest API
 *     = @GetMapping : SELECT
 *     = @PostMapping : INSERT
 *     ----------------- 웹에서는 주로 사용
 *     = @PutMapping : UPDATE
 *     = @DeleteMapping : DELETE
 *     ------------------------------------ 통합 @RequestMapping
 *                    스프링 6 이상 => @RequestMapping 제거 
 * 
 */
@RequestMapping("board/") // 공통 URL 주소 설정 
public class MainController {
     /*@RequestMapping("insert.do")
     public String board_insert(HttpServletRequest request,HttpServletResponse response)
     {
    	 return "board/insert"; //  == viewResolver ->  /board/insert.jsp    			 
     }*/
     
     @GetMapping("insert.do") // 입력폼 / 상세보기
     public String board_insert()
     {
    	 return "board/insert";
     }
     
     @PostMapping("insert_ok.do")
     public String board_insert_ok(HttpServletRequest request,HttpServletResponse response)
     {
    	 String name=request.getParameter("name");
    	 String subject=request.getParameter("subject");
    	 String content=request.getParameter("content");
    	 String pwd=request.getParameter("pwd");
    	 
    	 BoardVO vo=new BoardVO();
    	 vo.setName(name);
    	 vo.setSubject(subject);
    	 vo.setContent(content);
    	 vo.setPwd(pwd);
    	 
    	 request.setAttribute("vo",vo);
    	 return "board/list";
     }
     
     @PostMapping("insert_ok2.do")
     public String board_insert_ok2(String name,String subject,String content,String pwd,Model model)
     {
    	
    	 
    	 BoardVO vo=new BoardVO();
    	 vo.setName(name);
    	 vo.setSubject(subject);
    	 vo.setContent(content);
    	 vo.setPwd(pwd);
    	 
    	 model.addAttribute("vo",vo);
    	 return "board/list";
     }
     
     @PostMapping("insert_ok3.do")
     public String board_insert_ok3(BoardVO vo,Model model)
     {    	   	    	 
    	 model.addAttribute("vo",vo);
    	 return "board/list";
     }
}
