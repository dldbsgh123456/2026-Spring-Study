# Spring MVC — Controller 계층 정리 (1/2)

## 1. `@Controller` 매개변수로 값 받기

Spring MVC는 `request.getParameter()` 대신 **매개변수 이름 = 요청 키(key)**로 자동 바인딩한다. 순서는 상관없고, 이름만 일치하면 됨.

```java
@GetMapping("main/main.do")
public String main_main(String page, Model model) { ... }

@GetMapping("food/detail.do") // detail.do?no=1
public String food_detail(int no, Model model) { ... }
```

**값 받는 방법 정리**
| 방법 | 예시 |
|---|---|
| 일반 데이터형 | `String page`, `int no` |
| 내장 객체 | `Model model`, `HttpSession session` (요청 관련 `HttpServletRequest`는 보안상 지양) |
| 배열/리스트로 묶어 받기 | `String[] hobby`, `List list` (`name="list[0]"`, `list[1]`...) |
| 커맨드 객체(VO) | `DataBoardVO vo` — 폼 필드명과 VO 필드명이 같으면 자동으로 채워짐 |
| 리다이렉트 시 값 전달 | `RedirectAttributes` → `a.setAttribute("no",1)` 후 `return "redirect:...";` |

반환형: **String**이면 화면(JSP) 이동, **void**면 파일 다운로드.

---

## 2. GET / POST / REST 매핑 구분

| 상황 | 매핑 |
|---|---|
| 화면 폼 요청, 단순 조회 | `@GetMapping` |
| 데이터 전송(등록/검색 처리) | `@PostMapping` |
| 여러 방식 모두 허용 | `@RequestMapping` |
| 다른 프로그램(React/Vue) 연동 REST API | `@GetMapping`(SELECT) / `@PostMapping`(INSERT) / `@PutMapping`(UPDATE) / `@DeleteMapping`(DELETE) |

- `<form>`, `<a>` 태그, `location.href` → GET/POST 조합
- `axios.get() / post() / put() / delete()` → Rest API 매핑과 1:1 대응

---

## 3. 목록 + 페이지네이션(블록 방식) 공식

게시판/목록 화면에서 반복되는 표준 페이징 계산.

```java
int curpage = (page == null) ? 1 : Integer.parseInt(page);
int start = (curpage * 12) - 12;              // OFFSET 계산 (한 페이지 12건)
List<FoodVO> list = fService.foodListData(start);
int totalpage = fService.foodTotalPage();

final int BLOCK = 10;                          // 페이지 번호 10개씩 블록 단위
int startPage = ((curpage - 1) / BLOCK * BLOCK) + 1;
int endPage   = ((curpage - 1) / BLOCK * BLOCK) + BLOCK;
if (endPage > totalpage) endPage = totalpage;

model.addAttribute("list", list);
model.addAttribute("curpage", curpage);
model.addAttribute("totalpage", totalpage);
model.addAttribute("startPage", startPage);
model.addAttribute("endPage", endPage);
```
- `curpage`: 현재 페이지, `totalpage`: 전체 페이지 수, `startPage~endPage`: 하단 페이지 번호 블록의 시작/끝

---

## 4. 검색 처리 (동적 조건)

검색 컬럼과 검색어를 `Map`으로 묶어 Mapper에 전달 → 페이징 로직과 조합.

```java
@RequestMapping("food/find.do")
public String food_find(String page, String column, String fd, Model model) {
    if (column == null) column = "address";
    if (fd == null) fd = "마포";
    ...
    Map map = new HashMap();
    map.put("column", column);
    map.put("fd", fd);
    map.put("start", start);
    List<FoodVO> list = fService.foodFindListData(map);
    ...
}
```

---

## 5. 파일 업로드 처리 (`MultipartFile`)

커맨드 객체(VO)에 `List<MultipartFile> files` 필드를 두면 업로드 파일들이 자동으로 바인딩된다.

```java
@PostMapping("databoard/insert_ok.do")
public String databoardinsert_ok(DataBoardVO vo) {
    String path = "c:\\upload";
    List<MultipartFile> list = vo.getFiles();

    if (list == null) {                    // 업로드 안 한 경우
        vo.setFilename(""); vo.setFilesize(""); vo.setFilecount(0);
    } else {
        String filename = "", filesize = "";
        for (MultipartFile mf : list) {
            String oname = mf.getOriginalFilename();
            File file = new File(path + "\\" + oname);
            if (file.exists()) {            // 동명 파일 존재 시 (1),(2)... 붙여 이름 변경
                String name = oname.substring(0, oname.lastIndexOf("."));
                String ext = oname.substring(oname.lastIndexOf("."));
                int count = 1;
                while (file.exists()) {
                    file = new File(path + "\\" + name + "(" + count + ")" + ext);
                    count++;
                }
            }
            mf.transferTo(file);            // 실제 저장
            filename += file.getName() + ",";
            filesize += file.length() + ",";
        }
        vo.setFilename(filename.substring(0, filename.lastIndexOf(",")));
        vo.setFilesize(filesize.substring(0, filesize.lastIndexOf(",")));
        vo.setFilecount(list.size());
    }
    dService.databoardInsert(vo);
    return "redirect:list.do";
}
```
- 파일이 여러 개일 수 있으므로 이름/크기를 `,`로 이어붙여 한 컬럼에 저장하는 방식
- 동명 파일 충돌 시 `(1)`, `(2)` 형태로 자동 리네이밍

---

## 6. 공통 예외 처리 — `@ControllerAdvice`

모든 `@Controller`에 걸쳐 발생하는 예외를 한 곳에서 처리.

```java
@ControllerAdvice
public class CommonsException {
    @ExceptionHandler(Exception.class)
    public void exception(Exception ex) { ex.printStackTrace(); }

    @ExceptionHandler(Throwable.class)
    public void throwable(Throwable ex) { ex.printStackTrace(); }
}
```
- `@ExceptionHandler(타입.class)`로 예외 종류별 처리 메소드를 등록
- 각 컨트롤러마다 try-catch를 반복하지 않아도 됨

---

## 7. `@RestController` — Vue/React 연동용

화면(JSP) 없이 **데이터만** 전송할 때 사용. `Map`이나 VO를 리턴하면 자동으로 JSON 변환.

```java
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")   // 다른 도메인(Vue 개발서버 등)에서의 요청 허용
public class FoodRestController {
    private final FoodService fService;

    @GetMapping("food/list_vue.do")
    public Map food_list_vue(int page) {
        Map map = new HashMap();
        // ...같은 페이징 로직...
        map.put("list", fService.foodListData(start));
        return map;
    }

    @GetMapping("food/detail_vue.do")
    public FoodVO food_detail(int no) { return fService.foodDetailData(no); }
}
```
- 같은 데이터를 다루더라도 **JSP용 `@Controller`**와 **Vue용 `@RestController`**를 별도 클래스로 분리해서 공존시키는 것이 일반적 (URL도 `_vue.do` 등으로 구분)
- `@CrossOrigin`이 없으면 다른 포트/도메인의 Vue 개발서버에서 CORS 에러 발생

---

*(2/2에서 Mapper 동적 SQL, Service 계층, JSP 뷰 구성(Vue 업로드 폼, JSTL 페이징, 공통 레이아웃)을 이어서 정리)*
