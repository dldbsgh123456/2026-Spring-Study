package com.sist.controller;
import java.io.File;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import com.sist.service.DataBoardService;
import com.sist.vo.DataBoardVO;
import com.sist.vo.FoodVO;

import lombok.RequiredArgsConstructor;

@Controller // Router => 화면 변경 역할
// 메소드 승격 => 연산자 , 메소드 (어노테이션) => 클래스
// malloc => new
@RequiredArgsConstructor
public class DataBoardController {
    private final DataBoardService dService;
    /*@Autowired
    public DataBoardController(DataBoardService dService)
    {
    	this.dService=dService;
    }*/
    @GetMapping("databoard/insert.do")
    public String databoard_insert(Model model)
    {
    	model.addAttribute("main_jsp","../databoard/insert.jsp");
    	return "main/main";
    }
    
    @GetMapping("databoard/list.do")
    public String databoard_list(String page,Model model)
    {
       if(page==null)
    	  page="1";
       int curpage=Integer.parseInt(page);
       int start=(curpage*10)-10;
  	   List<DataBoardVO> list=dService.databoardListData(start);
  	   int totalpage=dService.databoardTotalPage();
  	   
  	   model.addAttribute("list",list);
       model.addAttribute("curpage",curpage);
       model.addAttribute("totalpage",totalpage);
       model.addAttribute("main_jsp","../databoard/list.jsp");
       return "main/main";
    }
    
    @PostMapping("databoard/insert_ok.do")
    public String databoardinsert_ok(DataBoardVO vo)
    {   	
    	// => Command 객체 => DataBoardVO vo  (vo 그대로 가져옴 )
//    	for(MultipartFile mf:vo.getFiles())
//    	{
//    		System.out.println(mf.getOriginalFilename());
//    	}
    	String path="c:\\upload";
    	List<MultipartFile> list=vo.getFiles();
    	if(list==null) // upload가 안 된 상태
    	{
    	   vo.setFilename("");
    	   vo.setFilesize("");
    	   vo.setFilecount(0);
    	}
    	else // upload가 있는 상태 
    	{
    	   try
    	   {
    		   String filename="";
    		   String filesize="";
    		   for(MultipartFile mf:list)
    		   {
    			   String oname=mf.getOriginalFilename();
    			   File file=new File(path+"\\"+oname);
    			   if(file.exists())
    			   {
    				   // aaa.java
    				   // name="aaa"
    				   // ext=".java"
    				   String name=oname.substring(0,oname.lastIndexOf("."));
    				   String ext=oname.substring(oname.lastIndexOf("."));
    				   int count=1;
    				   while(file.exists())
    				   {
    					   String newName=name+"("+count+")"+ext;
    					   file=new File(path+"\\"+newName);
    					   count++;
    				   }
    			   }
    			   mf.transferTo(file); //업로드
    			   filename+=file.getName()+",";
    			   filesize+=file.length()+",";
    		   }
    		   filename=filename.substring(0,filename.lastIndexOf(","));
    		   filesize=filesize.substring(0,filesize.lastIndexOf(","));
    		   vo.setFilename(filename);
    		   vo.setFilesize(filesize);
    		   vo.setFilecount(list.size());
    	   }catch(Exception ex) {}
    	}
    	dService.databoardInsert(vo);
    	return "redirect:list.do";
    }
}
