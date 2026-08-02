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

```java
@Aspect @Component
public class DAOAspect {
    @Autowired private MyDAO mDAO;

    @Before("execution(* com.sist.main3.MyDAO.db*(..))")
    public void before() { mDAO.getConnection(); }

    @After("execution(* com.sist.main3.MyDAO.db*(..))")
    public void after() { mDAO.getConnection(); }
}
```
```java
@Configuration
@ComponentScan(basePackages = {"com.sist.*"})
@EnableAspectJAutoProxy
public class DAOConfig {}
```
- `@ComponentScan` = XML의 `<context:component-scan base-package="">`
- `execution(* com.sist.main3.MyDAO.db*(..))` → `MyDAO`의 `db`로 시작하는 모든 메소드에 적용
- 적용 전: `dbselect()` 안에 `getConnection()/disConnection()`을 직접 호출 → 적용 후: 핵심 로직만 남고 공통 로직은 Aspect가 자동 실행

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
→ `@Before`/`@After`는 이 프록시 생성·호출 과정을 어노테이션만으로 자동화해주는 것.

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

## 정리

| 방식 | 설정 위치 | 특징 |
|---|---|---|
| XML + 어노테이션 | `app.xml` + `@Component` | 스캔으로 자동 등록 |
| 순수 자바 설정 | `@Configuration` + `@Bean` | Spring Boot 기본 방식 |
| AOP | `@Aspect` + `@Before`/`@After` | 프록시 패턴 기반, 공통/핵심 로직 분리 |
| 컨테이너 직접 구현 | SAX 파싱 + 리플렉션 | 스프링 DI 내부 동작 원리 |
