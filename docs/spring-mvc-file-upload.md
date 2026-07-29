# Spring MVC - 파일 업로드 처리 & 화면 포함(include) 패턴

**▶ Command 객체에 `List<MultipartFile>` 바로 바인딩**

```java
@PostMapping("databoard/insert_ok.do")
public String databoardinsert_ok(DataBoardVO vo) {
    List<MultipartFile> list = vo.getFiles();   // VO 안에 files 필드로 자동 바인딩
    ...
}
```

- `enctype="multipart/form-data"` 폼에서 넘어온 여러 개의 파일(`files[0]`, `files[1]`...)이 VO의 `files` 필드(`List<MultipartFile>`)로 자동 매핑됨
- 파라미터 하나(Command 객체)만 받아도 텍스트 필드 + 파일 목록이 한 번에 바인딩되는 구조

```jsp
<form method="post" action="../databoard/insert_ok.do" enctype="multipart/form-data">
  ...
  <input type=file :name="'files['+index+']'">
</form>
```

- Vue로 파일 입력 칸을 동적으로 추가/삭제(`v-for`)한 뒤, `name="files[인덱스]"` 형태로 넘겨서 리스트 바인딩이 되게 만드는 방식

---

**▶ 업로드 시 중복 파일명 처리**

```java
String name = oname.substring(0, oname.lastIndexOf("."));
String ext  = oname.substring(oname.lastIndexOf("."));
int count = 1;
while (file.exists()) {
    String newName = name + "(" + count + ")" + ext;
    file = new File(path + "\\" + newName);
    count++;
}
mf.transferTo(file);
```

- 같은 이름의 파일이 이미 서버에 있으면 `파일명(1).ext`, `파일명(2).ext` 식으로 번호를 붙여가며 중복을 피함
- 파일명/사이즈는 쉼표(`,`)로 이어붙여 하나의 문자열 컬럼에 저장 (`filename`, `filesize`), 개수는 `filecount`로 별도 저장

---

**▶ 시퀀스를 이용한 INSERT**

```java
@Insert("INSERT INTO springDataBoard(no,name,subject,content,pwd,"
      + "filename,filesize,filecount) "
      + "VALUES(sd_no_seq.nextval,#{name},"
      + "#{subject},#{content},#{pwd},#{filename},#{filesize},#{filecount})")
public void databoardInsert(DataBoardVO vo);
```

- PK(`no`)는 자바에서 넘기지 않고 Oracle 시퀀스(`sd_no_seq.nextval`)로 채번
- `#{필드명}`은 전달받은 VO의 getter와 자동 매칭

---

**▶ 처리 후 목록으로 리다이렉트**

```java
dService.databoardInsert(vo);
return "redirect:list.do";
```

- 등록 로직 끝난 뒤 `forward`가 아니라 `redirect:`로 응답 → 브라우저가 다시 GET 요청을 보내게 해서 새로고침해도 중복 등록되지 않음

---

**▶ 화면 include 패턴 (`main_jsp` 공통 변수)**

```java
@GetMapping("databoard/insert.do")
public String databoard_insert(Model model) {
    model.addAttribute("main_jsp", "../databoard/insert.jsp");
    return "main/main";
}
```

- 모든 화면 요청이 `"main/main"`이라는 같은 뷰로 가고, 실제 내용은 `main_jsp` 속성에 담긴 경로를 `main.jsp`에서 `<jsp:include>`로 불러오는 구조로 추정됨
- 페이지마다 컨트롤러는 다르지만 공통 레이아웃(헤더/푸터 등)을 하나의 `main.jsp`가 담당하고, 본문만 갈아 끼우는 패턴
