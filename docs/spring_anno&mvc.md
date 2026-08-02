## 1. `@Autowired` 주입 위치 & `@Qualifier`

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

## 2. 계층 구조: Controller → Service → DAO → Mapper (MyBatis)

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

## 3. Spring MVC 설정 파일 구조

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
