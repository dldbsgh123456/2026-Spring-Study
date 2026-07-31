# Spring MVC - `@Transactional`로 답변형 게시판 처리하기

**▶ 왜 트랜잭션이 필요한가**

답변 등록 하나에 SQL이 여러 번 실행됨:

```java
@Override
@Transactional   // => AOP 적용
public void boardReplyInsert(int pno, BoardVO vo) {
    BoardVO pvo = mapper.boardParentInfoData(pno);              // 1. 상위글 정보 조회
    mapper.boardStepIncrement(pvo.getGroup_id(), pvo.getGroup_step()); // 2. 같은 그룹의 뒷번호들 밀기
    vo.setGroup_id(pvo.getGroup_id());
    vo.setGroup_step(pvo.getGroup_step() + 1);
    vo.setGroup_tab(pvo.getGroup_tab() + 1);
    vo.setRoot(pno);
    vo.setDepth(0);
    mapper.boardReplyInsert(vo);                                 // 3. 답변 INSERT
    mapper.boardDepthIncrement(pno);                              // 4. 원글 depth 증가
}
```

- 4단계(조회 → UPDATE → INSERT → UPDATE) 중 하나라도 실패하면 데이터가 꼬임 (예: step만 밀리고 INSERT는 실패)
- `@Transactional`을 메서드에 붙이면 이 네 개의 SQL이 **하나의 단위**로 묶여서, 중간에 예외가 터지면 전부 롤백됨

---

**▶ `@Transactional`이 내부적으로 하는 일 (AOP 개념)**

메서드 코드에 없는 부분을 스프링이 가로채서(AOP) 앞뒤로 끼워 넣는 구조:

```
@Before   → 커넥션 얻고 conn.setAutoCommit(false)
try {
    (실제 메서드 로직 실행)
    conn.commit()
} catch(Exception e) {
    conn.rollback()   → @AfterThrowing
} finally {
    conn.setAutoCommit(true)  → @After
}
```

- 개발자는 try-catch-rollback 코드를 직접 안 써도 되고, 어노테이션 하나로 "이 메서드는 트랜잭션으로 묶어줘"라고 선언만 하면 됨
- 인터셉트(가로채기) 방식이라 자동 로그인이나 알림 같은 다른 공통 기능도 같은 방식(AOP)으로 확장 가능

---

**▶ 답변형 게시판 데이터 구조 (`group_id` / `group_step` / `group_tab`)**

```
AAAA      group_id=1  group_step=0  depth=0
 └ DDDD   group_id=1  group_step=1  depth=1
    └ BBBB group_id=1  group_step=2  depth=2
       └ CCCC group_id=1  group_step=3 depth=3
FFFF      group_id=2  group_step=0  depth=0
```

- `group_id`: 같은 글타래(원글+답변들)를 묶는 그룹 번호 — 정렬 시 `ORDER BY group_id DESC, group_step ASC`로 그룹은 최신순, 그룹 안에서는 등록순
- `group_step`: 그룹 안에서 화면에 출력되는 순서
- `group_tab`: 답변 들여쓰기 간격
- `root`: 이 글이 어느 원글에 달린 답변인지
- `depth`: 원글에 달린 답변이 몇 개인지 (원글 삭제 가능 여부 판단 등에 사용)

INSERT 시 `group_id`는 새 글마다 증가시키는 게 아니라, 원글에서는 `NVL(MAX(group_id)+1,1)`로 새로 채번하고, 답변에서는 부모의 `group_id`를 그대로 물려받음:

```java
// 원글 INSERT - 새 group_id 채번
@Insert("... (SELECT NVL(MAX(group_id)+1,1) FROM springReplyBoard))")
public void boardInsert(BoardVO vo);

// 답변 INSERT - 부모의 group_id를 그대로 사용
vo.setGroup_id(pvo.getGroup_id());
```

---

**▶ `@Transactional`이 동작하려면 XML 설정이 먼저 필요**

`@Transactional` 어노테이션 하나만 붙인다고 동작하는 게 아니라, 설정 파일(`application-datasource.xml`)에 트랜잭션 매니저를 등록하고 어노테이션 방식을 켜줘야 함:

```xml
<!-- DB 커넥션 정보 -->
<bean id="ds" class="org.apache.commons.dbcp.BasicDataSource"
   p:driverClassName="#{db['driver']}"
   p:url="#{db['url']}"
   p:username="#{db['username']}"
   p:password="#{db['password']}"
/>

<!-- 트랜잭션 매니저 - ds(DataSource)를 기준으로 commit/rollback 관리 -->
<bean id="transactionManager"
   class="org.springframework.jdbc.datasource.DataSourceTransactionManager"
   p:dataSource-ref="ds"
/>

<!-- 메소드 위에 @Transactional 붙이는 방식을 활성화 -->
<tx:annotation-driven/>
```

- `ds`(DataSource) → `transactionManager`가 이 DataSource 기준으로 커넥션의 `setAutoCommit` / `commit` / `rollback`을 관리
- `<tx:annotation-driven/>`을 선언해야 `@Transactional`이 실제로 AOP 프록시로 감싸짐 — 이 설정이 없으면 어노테이션만 붙여도 아무 효과 없음
- `#{db['driver']}` 같은 SpEL 표현식은 `<util:properties id="db" location="/WEB-INF/datasource/db.properties"/>`로 읽어들인 프로퍼티 파일 값을 참조

```xml
<!-- MyBatis가 같은 DataSource(ds)를 공유해서 세션 팩토리 생성 -->
<bean id="ssf" class="org.mybatis.spring.SqlSessionFactoryBean"
  p:dataSource-ref="ds"
/>
<!-- @Mapper 없이도 인터페이스만으로 구현체를 자동 생성 -->
<mybatis-spring:scan base-package="com.sist.mapper" factory-ref="ssf"/>
```

- MyBatis도 같은 `ds`를 참조하기 때문에, `@Transactional`로 시작된 트랜잭션 안에서 Mapper가 실행하는 SQL들이 같은 커넥션/같은 트랜잭션으로 묶임 (그래서 `boardReplyInsert`의 UPDATE/INSERT 네 번이 한 단위로 커밋·롤백될 수 있는 것)

---

**▶ 상세보기에서의 순차 처리 (조회수 증가 + 조회)**

```java
@Override
public BoardVO boardDetailData(int no) {
    mapper.boardHitIncrement(no);   // UPDATE 먼저
    return mapper.boardDetailData(no); // 그 다음 SELECT
}
```

- 조회수를 먼저 올린 뒤 최신 값을 조회하도록 순서를 맞춤 (이 메서드 자체는 `@Transactional`이 안 붙어 있음 — 실패해도 크게 문제되지 않는 단순 UPDATE+SELECT라 트랜잭션으로 묶지 않은 것으로 보임, 답변 등록처럼 여러 테이블/여러 단계가 얽힌 경우에만 `@Transactional`을 붙이는 판단 기준으로 참고할 만함)
