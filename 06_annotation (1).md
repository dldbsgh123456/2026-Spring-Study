# Spring 어노테이션 정리

## 1. 클래스 역할 구분 어노테이션

| 어노테이션 | 역할 |
|---|---|
| `@Component` | 모든 어노테이션의 상위 개념. 계층 구분 없는 일반 클래스 |
| `@Controller` | View(JSP 등) 화면 전환 컨트롤러 |
| `@RestController` | JSON/XML 반환 REST API 컨트롤러 (자동 JSON 변환) |
| `@Service` | 비즈니스 로직 계층 (Controller ↔ DAO 연결) |
| `@Repository` | DAO 계층, DB 연동 |
| `@Configuration` | XML 대체, 순수 자바로 빈 설정 |
| `@ControllerAdvice` / `@RestControllerAdvice` | 전역 예외 처리 |

흐름: `User ↔ DispatcherServlet ↔ Service ↔ DAO ↔ Oracle` (화면: JSP/HTML)

이름을 지정하지 않은 `@Component`의 기본 빈 이름은 **클래스명 첫 글자를 소문자로** 바꾼 것 (`Sawon` → `sawon`).

---

## 2. 설정 방식 3가지

### (1) XML + 어노테이션 — `ClassPathXmlApplicationContext`
클래스에 `@Component("id")`를 붙이고 XML의 `<context:component-scan>`으로 스캔.
```java
@Data @Component("mem")
public class Member { private int mno; private String name, address, phone; }
```
```java
ApplicationContext app = new ClassPathXmlApplicationContext("app.xml");
Member m = (Member) app.getBean("mem");
```

### (2) 완전 자바 설정 — `AnnotationConfigApplicationContext` + `@Configuration`
**Spring Boot의 핵심 방식.** XML 없이 `@Bean` 메소드가 빈을 생성.
```java
@Configuration
public class SawonConfig {
    @Bean("sa")
    public Sawon sawon() {
        Sawon s = new Sawon();
        s.setSabun(1); s.setName("심청이"); s.setDept("개발부"); s.setLoc("부산");
        return s;
    }
}
```
```java
AnnotationConfigApplicationContext app = new AnnotationConfigApplicationContext(SawonConfig.class);
Sawon s = (Sawon) app.getBean("sa");
```

### 컨테이너 종류
| 컨테이너 | 용도 |
|---|---|
| `ApplicationContext` | 일반 애플리케이션 |
| `WebApplicationContext` | 웹 애플리케이션 |
| `AnnotationConfigApplicationContext` | 어노테이션 기반 |

> 스프링은 웹 전용이 아니라 애플리케이션 전반(클래스가 많고 복잡한 프로그램)에 쓰이는 프레임워크.

---

## 3. AOP (Aspect-Oriented Programming)

핵심 로직(비즈니스 기능)과 공통 로직(연결/해제, 로깅 등)을 분리하는 기법. 내부적으로 **프록시 패턴**으로 동작한다.

**프록시로 직접 구현하면:**
```java
public class Proxy {
    private MyDAO m;
    public Proxy(MyDAO m) { this.m = m; }
    public void select() {
        System.out.println("오라클 연결...");   // getConnection
        m.select();                             // 실제 대상 호출 (Weaving)
        System.out.println("오라클 연결 해제..."); // disConnection
    }
}
```
→ AOP의 각 Advice는 이 프록시 생성·호출 과정을 어노테이션만으로 자동화해주는 것.

`@EnableAspectJAutoProxy`(또는 XML `<aop:aspectj-autoproxy/>`)로 AOP 프록시 자동 생성을 켜야 동작한다.

### AOP Advice 5종

| Advice | 시점 |
|---|---|
| `@Before` | 메소드 진입 전 |
| `@After` | 메소드 종료 후 (finally, 성공/예외 무관) |
| `@Around` | 전/후 모두 감쌈. `ProceedingJoinPoint.proceed()`로 직접 대상 메소드 호출 → 실행시간 측정 등에 사용 |
| `@AfterReturning` | 정상 종료 후 리턴값을 받음 (`returning` 속성명 = 메소드 매개변수명 일치 필요) |
| `@AfterThrowing` | 예외 발생 시 (`throwing` 속성명 = 매개변수명 일치 필요) |

```java
@Aspect @Component
public class EmpAOP {
    @Before("execution(* com.sist.service.EmpServiceImpl.*(..))")
    public void before() { System.out.println("메소드 진입전..."); }

    @After("execution(* com.sist.service.EmpServiceImpl.*(..))")
    public void after() { System.out.println("메소드 종료전..."); }

    @Around("execution(* com.sist.service.EmpServiceImpl.*(..))")
    public Object around(ProceedingJoinPoint jp) throws Throwable {
        long start = System.currentTimeMillis();
        Object obj = jp.proceed();               // 실제 메소드 호출
        System.out.println("걸린 시간:" + (System.currentTimeMillis() - start) + "MS");
        return obj;
    }

    @AfterReturning(value = "execution(* com.sist.service.EmpServiceImpl.*(..))", returning = "obj")
    public void afterReturn(Object obj) {
        if (obj instanceof List) { /* 리턴된 리스트를 순회하며 로그 등 처리 */ }
    }

    @AfterThrowing(value = "execution(* com.sist.service.EmpServiceImpl.*(..))", throwing = "ex")
    public void afterThrowing(Throwable ex) { ex.printStackTrace(); }
}
```
`execution(* com.sist.service.EmpServiceImpl.*(..))` → `리턴형(*=모든 타입) 클래스.메소드(*=모든 메소드, ..=모든 매개변수)`

---

## 4. 스프링 컨테이너 직접 구현해보기 (SAX + 리플렉션)

실제 스프링이 XML(`<bean>`)을 읽어 객체를 만들고 값을 주입하는 과정을 직접 구현한 예제.

```java
public interface ApplicationContext {
    public Object getbean(String key);
}

public class ClassPathXmlApplicationContext implements ApplicationContext {
    private Map clsMap = new HashedMap();
    public ClassPathXmlApplicationContext(String path) {
        try {
            SAXParser sp = SAXParserFactory.newInstance().newSAXParser();
            XMLParse xp = new XMLParse();
            sp.parse(new File(path), xp);
            clsMap = xp.getMap();
        } catch (Exception ex) {}
    }
    @Override
    public Object getbean(String key) { return clsMap.get(key); }
}
```

XML 파싱은 DOM/SAX 두 방식이 있고, 스프링·마이바티스는 **SAX**(한 줄씩 읽으며 필요한 데이터만 추출)를 사용한다.

```java
public class XMLParse extends DefaultHandler {
    private Map map = new HashMap();
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        try {
            if (qName.equals("bean")) {
                String id = attributes.getValue("id");
                String cls = attributes.getValue("class");
                String[] aName = { attributes.getValue("p:sabun"), attributes.getValue("p:name"),
                                    attributes.getValue("p:dept"), attributes.getValue("p:job") };
                String[] ss = { attributes.getQName(2), attributes.getQName(3),
                                 attributes.getQName(4), attributes.getQName(5) };

                Object obj = Class.forName(cls).getDeclaredConstructor().newInstance(); // 리플렉션 객체 생성
                for (Method m : obj.getClass().getDeclaredMethods()) {
                    for (int i = 0; i < ss.length; i++) {
                        if (m.getName().equalsIgnoreCase("set" + ss[i].substring(ss[i].indexOf(":") + 1))) {
                            if (i == 0) m.invoke(obj, Integer.parseInt(aName[i])); // setSabun(int)
                            else m.invoke(obj, aName[i]);
                        }
                    }
                }
                map.put(id, obj);
            }
        } catch (Exception ex) {}
    }
    public Map getMap() { return map; }
}
```

**DI 동작 순서**
1. `<bean>` 태그에서 `id`, `class`, `p:속성` 추출
2. `Class.forName(cls)` + `newInstance()`로 객체 생성 (메모리 할당)
3. `p:이름` 속성과 이름이 일치하는 `setXXX` 메소드를 찾아 `invoke()`로 값 주입
4. 완성된 객체를 `id`를 key로 `Map`에 저장 → `getbean(id)`로 조회

→ 사용법은 실제 스프링(`app.getBean("id")`)과 동일. 스프링 내부도 이와 비슷하게 SAX 파싱 + 리플렉션으로 빈을 생성·관리한다.

**`p:xxx-ref`**: 값이 아니라 **다른 빈의 참조**를 주입 (예: `p:mapper-ref="fMapper"` — MyBatis Mapper 빈을 DAO에 주입할 때 사용).

---

## 5. `@Autowired` 주입 위치 & `@Qualifier`

`@Autowired`는 필드, 생성자, 메소드, 매개변수 어디든 붙을 수 있다 — 스프링이 타입에 맞는 구현체의 주소를 자동으로 넣어준다.

```java
public class A {
    @Autowired private B b;              // 필드
    @Autowired public A() {}              // 생성자
    @Autowired public void display(@Autowired B b) {} // 메소드/파라미터
}
```
- 단점: 리플렉션으로 강제 주입하다 보니 OOP의 캡슐화가 깨지기 쉬움
- 인터페이스 구현체가 여러 개(`OracleDB`, `MySQLDB`)일 때는 `@Qualifier`로 어떤 빈을 쓸지 지정:
```java
@Autowired
@Qualifier("oracleDB")
private Oracle ora;
```
- Lombok `@RequiredArgsConstructor` + `private final DAO dao;` 조합은 생성자 주입을 자동 생성해주는 대안 (필드 `@Autowired`보다 권장되는 방식)

---

## 6. 계층 구조: Controller → Service → DAO → Mapper (MyBatis)

실무 기본 구조. `Food` 예제로 정리 (Emp/Dept도 동일 패턴 반복).

```java
// Mapper: SQL 담당 (단순 SQL은 @Select, 복잡한 SQL은 XML)
public interface FoodMapper {
    @Select("SELECT no,name,poster,address FROM food ORDER BY no OFFSET #{start} ROWS FETCH NEXT 12 ROWS ONLY")
    public List<FoodVO> foodListData(int start);
    @Select("SELECT CEIL(COUNT(*)/12.0) FROM food")
    public int foodTotalPage();
}

// DAO: Mapper를 주입받아 그대로 호출
@Repository
public class FoodDAO {
    @Autowired
    private FoodMapper mapper;
    public List<FoodVO> foodListData(int start) { return mapper.foodListData(start); }
    public int foodTotalPage() { return mapper.foodTotalPage(); }
}

// Service: DAO를 조합해 비즈니스 로직 제공
@Service
@RequiredArgsConstructor // 생성자 주입 자동 생성
public class FoodServiceImpl implements FoodService {
    private final FoodDAO dao;
    public List<FoodVO> foodListData(int start) { return dao.foodListData(start); }
    public int foodTotalPage() { return dao.foodTotalPage(); }
}

// Controller: 요청을 받아 Service 호출, JSON 반환
@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FoodRestController {
    private final FoodService service;
    @GetMapping("food/list.do")
    public Map food_list(String page) {
        int curpage = (page == null) ? 1 : Integer.parseInt(page);
        int start = (curpage * 12) - 12;
        Map map = new HashMap();
        map.put("list", service.foodListData(start));
        map.put("curpage", curpage);
        map.put("totalpage", service.foodTotalPage());
        return map;
    }
}
```

**계층 흐름**: `Controller ↔ Service ↔ DAO(=Repository) ↔ Mapper ↔ DB`, 화면 응답은 `Controller`가 JSP 또는 JSON으로 처리.

### MVC 매핑 어노테이션
| 어노테이션 | HTTP / 용도 |
|---|---|
| `@GetMapping` | 조회 (SELECT) |
| `@PostMapping` | 등록 (INSERT) |
| `@PutMapping` | 수정 (UPDATE) |
| `@DeleteMapping` | 삭제 (DELETE) |

- `@Controller` + 메소드가 문자열(뷰 이름) 리턴 → `viewResolver`가 JSP로 변환
- `@RestController` → 리턴값(Map, List 등)이 자동으로 JSON 변환
- 파라미터 받는 방법 3가지: `HttpServletRequest.getParameter()` / 메소드 파라미터로 직접 (`String name, ...`) / VO로 한번에 (`BoardVO vo`) — VO 방식이 가장 간결

---

## 7. Spring MVC 설정 파일 구조

| 파일 | 역할 |
|---|---|
| `web.xml` | `DispatcherServlet`을 `*.do`에 매핑, 인코딩 필터 등록 |
| `servlet-context.xml` | `<context:component-scan>`, `<aop:aspectj-autoproxy/>`, `viewResolver`(prefix/suffix) 등 웹 계층 설정 |
| `root-context.xml` | DB 커넥션풀(`BasicDataSource`), MyBatis `SqlSessionFactory`, `<mybatis-spring:scan>` 등 데이터 계층 설정 |

```xml
<!-- servlet-context.xml 핵심 -->
<aop:aspectj-autoproxy/>
<mvc:annotation-driven/>
<context:component-scan base-package="com.sist.*"/>
<bean id="viewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver"
      p:prefix="/" p:suffix=".jsp" />
```
```xml
<!-- root-context.xml 핵심 -->
<util:properties id="db" location="/WEB-INF/config/db.properties"/>
<bean id="ds" class="org.apache.commons.dbcp.BasicDataSource"
      p:driverClassName="#{db['driver']}" p:url="#{db['url']}"
      p:username="#{db['username']}" p:password="#{db['password']}" />
<bean id="ssf" class="org.mybatis.spring.SqlSessionFactoryBean" p:dataSource-ref="dataSource"/>
<mybatis-spring:scan base-package="com.sist.mapper" factory-ref="ssf"/>
```
- `web.xml`은 진입점만 지정하고, 실제 설정은 `contextConfigLocation`에 지정된 `application-*.xml`(= servlet-context + root-context)들이 담당

---

## 정리

| 방식 | 설정 위치 | 특징 |
|---|---|---|
| XML + 어노테이션 | `app.xml` + `@Component` | 스캔으로 자동 등록 |
| 순수 자바 설정 | `@Configuration` + `@Bean` | Spring Boot 기본 방식 |
| AOP | `@Aspect` + Before/After/Around/AfterReturning/AfterThrowing | 프록시 패턴 기반, 공통/핵심 로직 분리 |
| 컨테이너 직접 구현 | SAX 파싱 + 리플렉션 | 스프링 DI 내부 동작 원리 |
| 계층 구조 | Controller-Service-DAO-Mapper | 실무 기본 패턴, MyBatis 연동 |
| MVC 설정 | web.xml + servlet-context + root-context | 웹 계층 / 데이터 계층 분리 |
