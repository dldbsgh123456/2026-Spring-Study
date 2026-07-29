# Spring MVC 핵심 - 의존성 주입 / Controller 분리 / 전역 예외처리

## 생성자 주입 (@RequiredArgsConstructor)

java
@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService {
    private final FoodMapper mapper;   // 필드 하나만 선언하면 롬복이 생성자 자동 생성
    ...
}
@Autowired 필드 주입 대신, final 필드 + @RequiredArgsConstructor(Lombok)로 생성자 주입 처리
생성자를 직접 안 써도 되고, 필드가 늘어나면 생성자도 자동으로 반영됨

예전 방식(@Autowired 생성자 직접 작성)은 주석 처리하고 Lombok 방식으로 대체하는 흐름

## @Controller vs @RestController

'''java
@Controller
@RequiredArgsConstructor
public class FoodController {
    private final FoodService fService;

    @GetMapping("main/main.do")
    public String main_main(String page, Model model) {
        ...
        return "main/main";   // 뷰 이름 반환
    }
}
'''
'''java
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FoodRestController {
    private final FoodService fService;

    @GetMapping("food/list_vue.do")
    public Map food_list_vue(int page) {
        ...
        return map;   // JSON으로 자동 변환되어 반환
    }
}
'''
@Controller: 리턴값이 뷰(View) 이름 → 화면(JSP)으로 이동
@RestController: 리턴값이 객체(Map, List 등) → JSON으로 직렬화되어 응답 본문에 그대로 실림
두 컨트롤러가 같은 Service를 그대로 재사용 → 화면용/외부 API용을 역할만 다르게 분리하는 패턴
@CrossOrigin(origins = "*"): 다른 도메인/포트(Vue 개발 서버 등)에서의 요청을 허용하는 CORS 설정

## 전역 예외 처리 (@ControllerAdvice)

'''java
@ControllerAdvice
public class CommonsException {

    @ExceptionHandler(Exception.class)
    public void exception(Exception ex) {
        ex.printStackTrace();
    }

    @ExceptionHandler(Throwable.class)
    public void throwable(Throwable ex) {
        ex.printStackTrace();
    }
}
'''
@ControllerAdvice가 붙은 클래스는 모든 @Controller에 공통 적용되는 예외 처리기
@ExceptionHandler(Exception.class)처럼 특정 예외 타입을 지정하면, 그 컨트롤러 계층 어디서든 해당 예외가 발생 시 이 메서드가 대신 처리
각 컨트롤러마다 try-catch를 반복하지 않아도 됨

지금은 콘솔에 스택트레이스만 출력하는 상태 → 추후 에러 페이지 반환이나 로깅 프레임워크 연동으로 확장 가능
