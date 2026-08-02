# Spring MVC — Mapper / Service / View 정리 (2/2)

*(1/2 Controller 계층 정리에 이어서)*

## 1. Mapper — 어노테이션 방식 vs XML 방식

단순 SQL은 `@Select`/`@Insert`로 인터페이스에 바로 작성하지만, **조건이 동적으로 바뀌는 SQL**은 XML 매퍼로 분리한다.

```java
public interface FoodMapper {
    @Select("SELECT no,name,poster,address FROM food ORDER BY no ASC OFFSET #{start} ROWS FETCH NEXT 12 ROWS ONLY")
    public List<FoodVO> foodListData(int start);

    @Select("SELECT no,...,content,theme,price FROM food WHERE no=#{no}")
    public FoodVO foodDetailData(int no);

    // 동적 검색 조건 → XML로 분리 (인터페이스엔 선언만)
    public List<FoodVO> foodFindListData(Map map);
    public int foodFindTotalPage(Map map);
}
```

```xml
<!-- FoodMapper.xml -->
<mapper namespace="com.sist.mapper.FoodMapper">
  <select id="foodFindListData" resultType="FoodVO" parameterType="hashmap">
    SELECT no,name,poster,address FROM food
    WHERE ${column} LIKE '%'||#{fd}||'%'
    ORDER BY no ASC OFFSET #{start} ROWS FETCH NEXT 12 ROWS ONLY
  </select>
  <select id="foodFindTotalPage" resultType="int" parameterType="hashmap">
    SELECT CEIL(COUNT(*)/12.0) FROM food WHERE ${column} LIKE '%'||#{fd}||'%'
  </select>
</mapper>
```
- `#{}` : PreparedStatement 바인딩(값), SQL 인젝션에 안전
- `${}` : 문자열 그대로 치환(컬럼명처럼 동적으로 바뀌어야 하는 부분에 사용) — 값에는 쓰면 안 됨
- `parameterType="hashmap"` : 컨트롤러에서 넘긴 `Map`(`column`,`fd`,`start`)을 그대로 받음

**파일 업로드가 있는 게시판 Mapper (`@Insert`)**
```java
@Insert("INSERT INTO springDataBoard(no,name,subject,content,pwd,filename,filesize,filecount) "
       +"VALUES(sd_no_seq.nextval,#{name},#{subject},#{content},#{pwd},#{filename},#{filesize},#{filecount})")
public void databoardInsert(DataBoardVO vo);
```
- 시퀀스(`sd_no_seq.nextval`)로 PK 자동 채번, VO 필드명과 `#{}` 이름을 맞추면 커맨드 객체 전체를 그대로 바인딩 가능

---

## 2. Service 계층

DAO 없이 **Mapper를 Service가 직접 주입받는 구조**도 실무에서 흔히 쓰인다 (DAO를 생략하고 Service ↔ Mapper 직결).

```java
public interface DataBoardService {
    public List<DataBoardVO> databoardListData(int start);
    public int databoardTotalPage();
    public void databoardInsert(DataBoardVO vo);
}

@Service
@RequiredArgsConstructor
public class DataBoardServiceImpl implements DataBoardService {
    private final DataBoardMapper mapper;   // Mapper 직접 주입 (DAO 계층 생략)
    @Override public List<DataBoardVO> databoardListData(int start) { return mapper.databoardListData(start); }
    @Override public int databoardTotalPage() { return mapper.databoardTotalPage(); }
    @Override public void databoardInsert(DataBoardVO vo) { mapper.databoardInsert(vo); }
}
```
> 흐름: `VO → Mapper → Service(Impl) → Controller`. DAO를 따로 두느냐(Food 예제) 생략하느냐(DataBoard 예제)는 프로젝트 컨벤션 차이일 뿐, 둘 다 실무에서 사용됨.

---

## 3. VO 설계 포인트

```java
@Data
public class DataBoardVO {
    private int no, hit, filecount;
    private String name, subject, content, pwd, filename, filesize, dbday;
    private Date regdate;
    private List<MultipartFile> files;   // 업로드 폼과 바인딩되는 필드
}
```
- 화면(폼)의 `name` 속성과 VO 필드명을 맞추면 커맨드 객체로 자동 수집됨
- 파일 업로드가 필요한 VO는 `List<MultipartFile>` 필드를 추가

---

## 4. JSP 뷰 구성

### (1) 공통 레이아웃 — `jsp:include`
모든 화면이 공통 헤더/푸터를 공유하고, 본문만 동적으로 바뀌는 구조.
```jsp
<jsp:include page="header.jsp"/>
<jsp:include page="${main_jsp}"/>   <!-- 컨트롤러가 model에 담아준 경로 -->
<jsp:include page="footer.jsp"/>
```
- 컨트롤러에서 `model.addAttribute("main_jsp", "../food/detail.jsp")` 식으로 본문 페이지 경로만 넘겨주면, 공통 틀(`main.jsp`)이 include로 갈아 끼우는 방식

### (2) JSTL로 목록 + 페이징 출력
```jsp
<c:forEach var="vo" items="${list}">
  <tr>
    <td>${vo.no}</td><td>${vo.subject}</td><td>${vo.name}</td>
  </tr>
</c:forEach>

<c:if test="${startPage>1}"><a href="?page=${startPage-1}">&laquo;</a></c:if>
<c:forEach var="i" begin="${startPage}" end="${endPage}">
  <a href="?page=${i}" class="${i==curpage?'active':''}">${i}</a>
</c:forEach>
<c:if test="${endPage<totalpage}"><a href="?page=${endPage+1}">&raquo;</a></c:if>
```
- 컨트롤러가 계산해 넘긴 `curpage/totalpage/startPage/endPage`를 그대로 반복문에 사용

### (3) Vue.js로 동적 입력 폼 (파일 여러 개 추가/삭제)
순수 JSP만으로는 "파일 추가" 버튼 클릭 시 input을 동적으로 늘리기 어려워, 부분적으로 Vue를 얹어 처리.
```html
<tr v-for="(file,index) in files" :key="index">
  <td><input type="file" :name="'files['+index+']'"></td>
</tr>
```
```js
const app = Vue.createApp({
  data() { return { files: [] } },
  methods: {
    addFile() { this.files.push({}) },
    removeFile() { if (this.files.length > 0) this.files.pop() }
  }
}).mount("#app")
```
- `:name="'files['+index+']'"` → 서버에서 `List<MultipartFile> files`로 그대로 수집되도록 배열 형태 name을 동적으로 생성
- JSP(서버 렌더링) + Vue(부분 상호작용) 혼합 방식 — 전체를 SPA로 만들지 않고 필요한 부분만 컴포넌트화

---

## 정리

| 계층 | 핵심 |
|---|---|
| Controller | 매개변수 자동 바인딩, GET/POST/REST 구분, 페이징 계산, 파일 업로드 |
| `@ControllerAdvice` | 전역 예외 처리 |
| `@RestController` | Vue/React용 JSON 응답, `@CrossOrigin` 필수 |
| Mapper | `#{}`(값 바인딩) vs `${}`(문자열 치환), 동적 조건은 XML로 분리 |
| Service | DAO를 두거나 생략하고 Mapper 직결, 둘 다 실무 사용 |
| View(JSP) | `jsp:include` 공통 레이아웃, JSTL 반복/조건, 필요한 부분만 Vue로 보강 |
