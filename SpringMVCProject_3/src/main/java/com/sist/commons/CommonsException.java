package com.sist.commons;

import org.springframework.web.bind.annotation.ControllerAdvice;
// Controller 예외처리 => 공통 예외처리 (모든 Controller 클래스)
import org.springframework.web.bind.annotation.ExceptionHandler;
@ControllerAdvice // RestController 예외처리는 못 잡는다 @RestControllerAdvice 따로 써야 됨
public class CommonsException {
   @ExceptionHandler(Exception.class)
   public void exception(Exception ex)
   {
	   System.out.println("============== 예외 발생(Exception) ==============");
	   ex.printStackTrace();
   }
   
   @ExceptionHandler(Throwable.class)
   public void Throwable(Throwable ex)
   {
	   System.out.println("============== 예외 발생(Throwable) ==============");
	   ex.printStackTrace();
   }
}
