## ▶ 1. Spring Security 설정 (`application-security.xml`)

### 요청 흐름
```
user(client) === security 필터 === DispatcherServlet === HandlerMapping = Model = ViewResolver = JSP
```
- `/member/**` → `permitAll` (누구나 접근 가능)
- `/admin/**` → `hasRole('ROLE_ADMIN')`
- `/board/**` → `hasAnyRole('ROLE_ADMIN','ROLE_USER')` (주석상 설계, 현재 XML엔 미등록)
- 권한이 없는 URL 접근 시 → **403 접근 거부**

### 현재 등록된 `<intercept-url>`
| 패턴 | 접근 권한 |
|---|---|
| `/member/**` | permitAll |
| `/admin/**` | hasRole('ROLE_ADMIN') |
| `/**` | permitAll |
| `/member/login.do` | permitAll |

> ⚠️ `/**`을 `/admin/**`보다 **아래**에 둔 것이 순서상 맞습니다 (Security는 위에서부터 매칭). 지금 순서는 올바르게 되어 있습니다.

### 로그인 처리 (`form-login`)
| 속성 | 값 | 의미 |
|---|---|---|
| `login-page` | `/member/login.do` | 로그인 폼 위치 |
| `login-processing-url` | `/member/login.do` | 로그인 처리 URL |
| `username-parameter` | `userid` | id input name |
| `password-parameter` | `userpwd` | pw input name |
| `authentication-success-handler-ref` | `LoginSuccessHandler` | 성공 시 |
| `authentication-failure-handler-ref` | `LoginFailureHandler` | 실패 시 |

### 로그아웃 처리 (`logout`)
- `logout-url="/member/logout.do"`
- `invalidate-session="true"` → 세션 무효화
- `logout-success-url="/main/main.do"`

### 인증 방식 — DB 연동 (`jdbc-user-service`)
```sql
-- 사용자 조회
SELECT userid as username, userpwd as password, enable FROM springMember WHERE userid=?
-- 권한 조회
SELECT userid as username, authority FROM authority WHERE userid=?
```
- `authentication-manager` → `authentication-provider` → `password-encoder`(`BCryptPasswordEncoder`) 연결 구조

> ⚠️ `<csrf disabled="true"/>` — 현재 CSRF 보호가 꺼져 있는 상태입니다. 학습용이면 괜찮지만 실서비스 배포 시엔 켜는 게 원칙입니다.

> 📌 `<remember-me/>` 는 주석 처리되어 있어 **자동로그인 기능 미구현** 상태입니다.

---

## ▶ 2. 로그인 성공/실패 핸들러

### `LoginSuccessHandler` (`AuthenticationSuccessHandler`)
- `onAuthenticationSuccess()` 오버라이드까지만 되어 있고 **내부 로직 미구현**
- 주석상 목표: 로그인 성공 시 `HttpSession`에 사용자 정보 저장

### `LoginFailureHandler` (`AuthenticationFailureHandler`)
- `onAuthenticationFailure()` 안에서 예외 타입별 분기만 되어 있고 **처리 로직 미구현**

| 예외 타입 | 의미 |
|---|---|
| `BadCredentialsException` | 아이디/비밀번호 불일치 |
| `DisabledException` | 휴면(비활성화) 계정 |

> ⚠️ 두 핸들러 모두 골격(스텁)만 있는 상태 — 실제로는 `HttpSession`에 값 저장(성공) / `response.sendRedirect()`나 에러 메시지 세팅(실패) 로직이 채워져야 동작합니다.

---

## ▶ 3. `MainInterceptor` (`HandlerInterceptorAdapter`)

인터셉터 3단계 실행 순서:

```
요청 → preHandle() → Controller 실행 → postHandle() → View 렌더링 → afterCompletion()
```

| 메소드 | 호출 시점 | 현재 구현 |
|---|---|---|
| `preHandle` | Controller 실행 **전** | 로그 출력 + `super` 호출(그대로 진행) |
| `postHandle` | Controller 실행 후, View 렌더링 **전** | 로그 출력만 |
| `afterCompletion` | View 렌더링까지 완료된 **후** | 로그 출력만 |

- 주석에 적힌 활용 목적: `preHandle()`에서 **자동로그인 / ID 저장** 처리 예정
- 현재는 3개 메소드 모두 `System.out.println`으로 호출 여부만 확인하는 단계

---

## ▶ 4. `FooterCommonsAspect` (AOP)

**포인트컷 대상:** `com.sist.web.*Controller.*(..)` — `web` 패키지의 모든 `*Controller` 클래스의 모든 메소드

| Advice | 어노테이션 | 동작 |
|---|---|---|
| `sendData()` | `@After` | Controller 메소드 실행 후, 인기 음식 리스트(`foodHit7Data()`)를 조회해 `request`에 `fList`로 저장 (공통 footer용 데이터) |
| `log()` | `@Around` | 메소드 실행 **전/후**로 로그 출력 (`jp.proceed()` 전후로 요청 시작/완료 로그) |

```
@Around 흐름:
  로그: "사용자 요청: xxx"
     ↓
  jp.proceed()  ← 실제 컨트롤러 메소드 실행
     ↓
  로그: "사용자 요청 완료: xxx"
```

> 📌 `@After`는 정상/예외 상관없이 항상 실행, `@Around`는 `proceed()` 호출 시점을 직접 제어할 수 있어 전/후 로직을 모두 넣을 수 있다는 차이가 있습니다.

---

## ▶ 전체 흐름 요약

```
사용자 로그인 요청 (/member/login.do)
   → Security 필터가 가로챔
   → 성공: LoginSuccessHandler (세션 저장 예정, 미구현)
   → 실패: LoginFailureHandler (에러 처리 예정, 미구현)

일반 페이지 요청 (main.do 등)
   → MainInterceptor.preHandle()
   → Controller 실행 (FooterCommonsAspect가 @Around/@After로 감쌈)
   → MainInterceptor.postHandle()
   → View 렌더링
   → MainInterceptor.afterCompletion()
```

| 구성요소 | 역할 |
|---|---|
| Security XML | 인증/인가 규칙, 로그인/로그아웃 처리 등록 |
| SuccessHandler / FailureHandler | 로그인 결과에 따른 후처리 (현재 스텁) |
| Interceptor | 컨트롤러 앞뒤 공통 처리 (현재 로그만) |
| Aspect (AOP) | 특정 패키지 메소드 전체에 공통 관심사(로그, 데이터 세팅) 적용 |
