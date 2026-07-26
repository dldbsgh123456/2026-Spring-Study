# IoC / DI 개념

## IoC (Inversion of Control)란?
객체의 생성과 생명주기 관리를 개발자가 아니라 **스프링 컨테이너**가 담당하는 것

## DI (Dependency Injection)란?
객체가 필요한 의존 객체를 직접 생성하지 않고, **외부(컨테이너)에서 주입**받는 방식

### 1. 클래스 등록

우선, XML없이 직접 코드로 객체를 만든다면
```java
public class MainClass {
    public static void main(String[] args) {
        Member mem = new Member(1, "홍길동", "서울", "1111-1111");
        mem.print();
    }
}
```
가능하지만
값이 바뀔 때마다 코드를 고치고 다시 컴파일해야 함
new Member(2, "심청이", ...) 코드수정 -> 다시 컴파일

but XML 파일에 클래스 등록을 해놓는다면 설정파일만 수정하면 됨

class는 꼭 네임으로 적어야 함, scope는 안 적으면 싱글이 디폴트 객체 여러개 쓰고 싶으면 prototype
Lombok(@DATA)의 setter 이용해서 값 설정
```java
<bean id="mem" class="com.sist.main.Member" scope="singleton">  -> 네임스페이스 p태그 방법 3가지중 첫 번째
  <property name="sabun" value="1"></property>
  <property name="name" value="홍길동"></property>
  <property name="loc" value="서울"></property>
  <property name="phone" value="1111-1111"></property>
</bean>
```
```java
<bean id="mem" class="com.sist.main.Member" scope="singleton">  -> 네임스페이스 p태그 방법 3가지중 두 번째
  <property name="sabun"><value>1</value></property>
  <property name="name"><value>홍길동</value></property>
  <property name="loc"><value>서울</value></property>
  <property name="phone"><value>1111-1111</value></property>
</bean>
```
```java
<bean id="mem" class="com.sist.main.Member" scope="singleton"  -> 네임스페이스 p태그 방법 3가지중 세 번째 (가장 많이 쓰임)
  p:sabun="1"
  p:name="홍길동"
  p:loc="서울"
  p:phone="1111-1111"
  />
```
***네임스페이스탭에서 p태그가 체크가 되어 있어야 할 수 있음***
--------------------------------------------------------------------------------------------------------------------------
@AllArgsConstructor 이용해서 값 설정
```java
<bean id="mem" class="com.sist.main.Member" scope="singleton"> 네임스페이스 c태그 방법 4가지중 첫 번째
<constructor-args value="1" index="0"/>
<constructor-args value="홍길동" index="1"/>
<constructor-args value="서울" index="2"/>
<constructor-args value="1111-1111" index="3"/>
```
```java
<bean id="mem" class="com.sist.main.Member" scope="singleton"> 네임스페이스 c태그 방법 4가지중 두 번째
<constructor-arg><value>2</value></constructor-arg>
<constructor-arg><value>심청이</value></constructor-arg>
<constructor-arg><value>경기</value></constructor-arg>
<constructor-arg><value>2222-2222</value></constructor-arg>
</bean>
```
```java
<bean id="mem" class="com.sist.main.Member" scope="singleton" -> 네임스페이스 c태그 방법 4가지중 세 번째 (가장 많이 쓰임)
  c:mno="1"
  c:name="심청이"
  c:loc="경기"
  c:phone="2222-2222"
/>
```
```java
<bean id="mem" class="com.sist.main.Member" scope="singleton" -> 네임스페이스 c태그 방법 4가지중 네 번째
  c:_00="1"
  c:_01="심청이"
  c:_02="경기"
  c:_03="2222-2222"
/>
```
***네임스페이스탭에서 c태그가 체크가 되어 있어야 할 수 있음***
