# 회원가입 API 구현 변경 내역

## 수정된 파일

### `build.gradle`
- `spring-boot-starter-security` 주석 해제
- **이유**: `BCryptPasswordEncoder`가 Spring Security 모듈에 포함되어 있어 비밀번호 해싱에 필수. Security 자체 인증 기능은 `SecurityConfig`에서 전부 `permitAll`로 비활성화했으므로 기존 API 동작에 영향 없음.

---

### `user/entity/User.java`
기존 엔티티를 확정된 DDL 스키마에 맞게 전면 재작성.

| 변경 항목 | 변경 전 | 변경 후 | 이유 |
|---|---|---|---|
| 비밀번호 필드 | `password` | `passwordHash` + `@Column(name="password_hash")` | DDL 컬럼명이 `password_hash`이고 평문이 아닌 BCrypt 해시값 저장 명시 |
| 필드 추가 | 없음 | `totalSubmissionCount` | DDL에 `total_submission_count` 컬럼 존재 |
| 타임스탬프 | `createdAt` (수동 설정) | `createdAt` + `updatedAt` (JPA Auditing) | DDL에 `updated_at` 컬럼 존재, `@CreatedDate`/`@LastModifiedDate`로 자동 관리 |
| `@Column` 부재 | 없음 | 전 필드에 `@Column` 명시 | snake_case↔camelCase 매핑, unique 제약, length 제약 반영 |
| 타입 | `int` | `Integer` (래퍼 타입) | JPA가 null 여부를 정확히 처리하도록 래퍼 타입 사용 |
| `@EntityListeners` | 없음 | `AuditingEntityListener.class` 추가 | JPA Auditing 작동에 필요 |

기존 메서드(`addExp`, `decreaseExp`, `levelUp`)는 `ExpService`, `LevelEngine`에서 그대로 사용하므로 유지.

---

### `user/repository/UserRepository.java`
- `existsByNickname(String nickname)` 추가: 닉네임 중복 체크용. DDL에 `UNIQUE` 제약이 있으므로 저장 전 서비스 레이어에서 사전 검증 필요.
- `Optional<User> findByEmail(String email)` 추가: 로그인 구현 시 이메일로 사용자 조회에 사용 예정.

---

### `user/controller/UserController.java`
- `POST /api/users/signup` 엔드포인트 제거
- **이유**: 회원가입 책임이 `AuthController`(`/api/auth/signup`)로 이전됨. BCrypt 해싱, 중복 검증 등 인증 관련 로직은 `auth` 패키지에서 관리. `GET /api/users/{userId}`는 그대로 유지.

---

### `user/service/UserService.java`
- `signup()` 메서드 제거
- **이유**: 회원가입 로직이 `AuthService`로 이전. 기존 `signup()`은 BCrypt 해싱 없이 평문을 저장하는 미완성 구현이었음. `getUser()` 메서드는 유지.

---

### `src/main/resources/application.properties`
- `spring.jpa.hibernate.ddl-auto=update` → `none`
- **이유**: DDL이 이미 확정되어 있으므로 JPA가 스키마를 임의로 변경하면 안 됨. `update`는 컬럼 추가는 하지만 컬럼 삭제/수정은 하지 않아 스키마 불일치가 생길 수 있음. 실제 DB에는 `schema.sql`을 수동으로 적용.

---

## 신규 생성된 파일

### `auth/dto/SignupRequest.java`
- Jakarta Validation 어노테이션 포함 (`@NotBlank`, `@Email`, `@Size`)
- **이유**: `user/dto/SignupRequest.java`는 validation 없는 미완성 버전. 인증 관련 DTO는 `auth` 패키지로 분리해 관심사 구분. `record` 타입은 기존 프로젝트 DTO 컨벤션 그대로 따름.

### `auth/dto/SignupResponse.java`
- `userId` 필드 하나만 포함하는 record
- **이유**: 회원가입 성공 응답에는 생성된 유저 ID만 반환하면 충분 (API 명세 기준)

### `auth/service/AuthService.java`
- 이메일/닉네임 중복 체크 → BCrypt 해싱 → 저장 순서로 처리
- **이유**: 중복 체크를 저장 전에 명시적으로 수행해 DB 유니크 제약 위반 에러 대신 명확한 커스텀 예외를 던짐. `PasswordEncoder`는 `SecurityConfig`에 등록된 Bean을 주입받아 사용.

### `auth/controller/AuthController.java`
- `POST /api/auth/signup` 엔드포인트
- `@Valid`로 DTO 검증, `ResponseEntity<ApiResponse<SignupResponse>>`로 공통 포맷 반환
- **이유**: 기존 프로젝트는 plain record 반환이었으나 API 명세상 `{status, message, data}` 래퍼 포맷이 요구됨. 회원가입 응답에만 적용하고 기존 API는 건드리지 않음.

### `common/response/ApiResponse.java`
- `status`(HTTP 상태명), `message`, `data<T>` 세 필드 포함
- static factory 메서드: `success(httpStatus, message, data)`, `error(httpStatus, message)`
- **이유**: 기존 코드에 공통 응답 클래스 없음. 모든 응답을 동일한 포맷으로 직렬화하기 위해 신규 생성. Lombok 미사용은 기존 프로젝트 Entity/DTO 컨벤션 준수.

### `common/exception/DuplicateEmailException.java`
- `RuntimeException` 상속, 메시지 "이미 사용 중인 이메일입니다." 고정
- **이유**: `IllegalArgumentException`으로 던지면 `GlobalExceptionHandler`에서 HTTP 상태 코드를 구분할 수 없음. 이메일/닉네임 중복을 각각 다른 예외 타입으로 분리해 409 응답을 명확하게 처리.

### `common/exception/DuplicateNicknameException.java`
- `DuplicateEmailException`과 동일한 이유로 별도 타입으로 분리

### `common/exception/GlobalExceptionHandler.java`
- `@RestControllerAdvice`로 전역 예외 처리
- `MethodArgumentNotValidException` → 400, 첫 번째 필드 에러 메시지 반환
- `DuplicateEmailException` / `DuplicateNicknameException` → 409
- **이유**: 기존 코드에 예외 핸들러 없어 예외가 500이나 Spring 기본 에러 포맷으로 반환됨. 공통 응답 포맷(`ApiResponse`)에 맞춰 에러 응답을 통일.

### `config/SecurityConfig.java`
- `SecurityFilterChain`: CSRF 비활성화, 모든 요청 `permitAll`
- `PasswordEncoder` Bean: `BCryptPasswordEncoder` 등록
- **이유**: Spring Security를 활성화하면 기본적으로 모든 요청이 차단됨. 로그인 구현 전까지는 전체 허용으로 설정. `PasswordEncoder`를 Bean으로 등록해야 `AuthService`에서 주입받아 사용 가능.

### `config/JpaAuditingConfig.java`
- `@EnableJpaAuditing` 적용
- **이유**: `@EnableJpaAuditing`을 메인 클래스(`@SpringBootApplication`)에 직접 붙이면 `@DataJpaTest` 슬라이스 테스트 시 충돌 가능. 별도 `@Configuration` 클래스로 분리하는 것이 권장 패턴.

---

## 최종 패키지 구조 (변경분)

```
com.catchcbnu.ospp_project
├── auth/                          ← 신규
│   ├── controller/AuthController
│   ├── service/AuthService
│   └── dto/
│       ├── SignupRequest          (validation 포함)
│       └── SignupResponse
├── common/
│   ├── exception/                 ← 신규
│   │   ├── DuplicateEmailException
│   │   ├── DuplicateNicknameException
│   │   └── GlobalExceptionHandler
│   ├── response/                  ← 신규
│   │   └── ApiResponse<T>
│   └── HealthController           (기존 유지)
├── config/                        ← 신규
│   ├── SecurityConfig
│   └── JpaAuditingConfig
└── user/
    ├── entity/User                (DDL 맞게 재작성)
    ├── repository/UserRepository  (메서드 추가)
    ├── controller/UserController  (signup 제거)
    ├── service/UserService        (signup 제거)
    └── dto/ ...                   (기존 유지)
```
