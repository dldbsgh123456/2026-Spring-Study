package com.sist.main3;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration // xml 대신 자바로 환경 설정 
@ComponentScan(basePackages = {"com.sist.*"}) // <context:component-scan basepackage="">
@EnableAspectJAutoProxy
public class DAOConfig {
       
}
