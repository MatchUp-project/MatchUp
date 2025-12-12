# MatchUp 프로젝트 Copilot 지침

## 프로젝트 개요
MatchUp은 스포츠 팀 구성 및 매칭 플랫폼입니다. Spring Boot 3.3.1 + MySQL + Spring Security + Thymeleaf로 구성된 MVC 웹 애플리케이션입니다.

**빌드 시스템**: Gradle (Java 17)  
**주요 스택**: Spring Boot, Spring Data JPA, Spring Security, MySQL, Thymeleaf  
**프로젝트 구조**: 단일 모듈 Gradle 프로젝트 (`MatchUp/` 디렉토리 내 또 다른 설정 있음)

---

## 핵심 아키텍처

### 도메인 구조
프로젝트는 기능별로 도메인 모듈로 분리되어 있습니다:

- **board**: 게시판 (FREE/PLAYER/TEAM 카테고리) + 댓글, 좋아요, 플레이어/팀 모집 게시글
- **team**: 팀 생성, 팀 멤버 관리, 팀 게시판
- **user**: 사용자 인증, 프로필 관리
- **match**: 경기 관리
- **matchrecord**: 경기 기록 및 점수 입력
- **event**: 이벤트 관리
- **notification**: 알림
- **post**: 팀 게시판
- **security**: Spring Security 커스텀 설정
- **common**: `CurrentUserService` 등 공통 유틸

### 계층별 패턴
각 도메인은 다음 계층을 따릅니다:

```
Entity (JPA @Entity, 필드는 일반 getter/setter)
  ↓
Repository (JpaRepository 확장, 커스텀 쿼리 메서드)
  ↓
Service (@Service, @Transactional, 비즈니스 로직)
  ↓
Controller (@Controller, @RestController)
```

### 데이터 접근 패턴
- **Repository**: `JpaRepository` 확장, 메서드 네이밍 컨벤션 사용
  - 예: `findByCategoryAndDeletedFalseOrderByIdDesc()`, `findByDeletedFalseOrderByIdDesc()`
  - 복잡한 쿼리는 `@Query(nativeQuery = true)` 또는 JPQL 사용
  - 예: `BoardRepository.findTopRecentByCategory(@Param("categoryName") String, @Param("limit") int)`
  
- **Soft Delete 패턴**: 많은 엔티티(Board 등)에서 `deleted` 필드(boolean)로 논리적 삭제 구현
  - Repository 메서드는 `deleted = false` 조건을 명시적으로 포함 (쿼리 메서드명에 명시)

### 서비스 계층 특징
- `@Transactional` 클래스 수준 또는 메서드 수준 적용
- 읽기 전용 메서드에는 `@Transactional(readOnly = true)` 사용
- Lombok의 `@RequiredArgsConstructor` 사용으로 생성자 의존성 주입
- 예: `BoardService` → `BoardRepository`, `BoardCommentRepository`, `UserService` 등 다중 Repository 의존

### 컨트롤러 패턴
- **@RestController**: JSON API 응답 (e.g., `BoardController`, `BoardLikeController`, `TeamController`)
- **@Controller**: HTML 뷰 반환 (e.g., `BoardPageController`, `TeamPageController`, `MatchController`)
- 경로: REST API는 `/api/` 접두사 사용 (e.g., `/api/board`)

---

## 개발 워크플로우

### 빌드 및 실행
```bash
# Gradle 래퍼 사용 (Windows)
.\gradlew.bat build           # 빌드
.\gradlew.bat bootRun         # 실행 (기본값: localhost:8080)
```

### 데이터베이스
- **DBMS**: MySQL (로컬 설정됨)
- **설정**: `application.properties`
  ```
  spring.datasource.url=jdbc:mysql://localhost:3306/matchup?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
  spring.datasource.username=chaj
  spring.datasource.password=chaji3949!
  spring.jpa.hibernate.ddl-auto=none  # 자동 스키마 생성 비활성화
  ```
- **주의**: 자동 DDL이 비활성화되어 있으므로 스키마 변경 시 SQL 스크립트 필요

### 테스트
```bash
.\gradlew.bat test            # 테스트 실행 (JUnit 5 기반)
```

---

## 프로젝트 고유 패턴 및 관례

### 1. 현재 사용자 획득
```java
// common/CurrentUserService.java 사용
@Component
public class CurrentUserService {
    public User getCurrentUser() { /* SecurityContext에서 조회 */ }
    public Team getCurrentUserTeam() { /* 현재 사용자의 팀 */ }
}
```
모든 컨트롤러/서비스에서 의존성 주입으로 사용:
```java
@Transactional
public Long create(BoardRequest request) {
    User user = userService.findById(userSecurityService.getCurrentUserId());
    // ...
}
```

### 2. DTO 패턴
- **Request DTO**: 양방향 데이터 바인딩 (Lombok `@Getter` `@Setter`)
  - 예: `BoardRequest` → category, title, content + 카테고리별 추가 필드
  - category는 문자열로 받아서 enum으로 변환
- **Response DTO**: 읽기 전용 (필드 직접 공개)
  - 예: `BoardResponse`

### 3. 비즈니스 로직 집중
`Service` 클래스는 복잡한 다중 Repository 접근 포함:
```java
// BoardService.java 패턴
public Long create(BoardRequest request) {
    Board board = new Board(...);
    boardRepository.save(board);
    
    // 카테고리별 추가 데이터 저장
    if (category == PLAYER) {
        boardPlayerRecruitRepository.save(new BoardPlayerRecruit(...));
    } else if (category == TEAM) {
        boardTeamSearchRepository.save(new BoardTeamSearch(...));
    }
}
```

### 4. 열거형(Enum) 활용
- `BoardCategory` enum: FREE, PLAYER, TEAM
- Repository 메서드에서 enum으로 조회하는 메서드와 문자열로 조회하는 메서드 혼재
  - 예: `findByCategoryAndDeletedFalse(BoardCategory)` vs `findTopRecentByCategory(String categoryName)`

### 5. 네이티브 쿼리 사용
복잡한 조인이나 특정 집계 필요 시:
```java
@Query(value = "SELECT * FROM board WHERE category = :categoryName AND deleted = false ORDER BY id DESC LIMIT :limit", nativeQuery = true)
List<Board> findTopRecentByCategory(@Param("categoryName") String categoryName, @Param("limit") int limit);
```

---

## 주요 의존성 및 버전

| 라이브러리 | 버전 | 용도 |
|-----------|------|------|
| Spring Boot | 3.3.1 | 핵심 프레임워크 |
| Spring Data JPA | (Boot 포함) | ORM |
| Spring Security | (Boot 포함) | 인증/인가 |
| Thymeleaf | (Boot 포함) | 서버 사이드 템플릿 |
| MySQL Connector | 8.0.33 | 데이터베이스 드라이버 |
| Lombok | (최신) | 보일러플레이트 감소 |
| Spring DevTools | (Boot 포함) | 개발 편의성 |

---

## 주의사항 및 베스트 프랙티스

1. **Soft Delete 확인**: Board 같은 엔티티는 물리적 삭제 대신 `deleted` 플래그 사용
   - 새로운 Repository 메서드 추가 시 `DeletedFalse` 조건 포함 필수

2. **@Transactional 명시**: Service 메서드는 클래스 또는 메서드 수준에서 선언
   - 읽기 전용은 `readOnly = true`로 최적화

3. **enum 변환 주의**: Request에서 문자열로 category를 받아 enum으로 변환
   ```java
   BoardCategory category = BoardCategory.valueOf(request.getCategory().toUpperCase());
   ```

4. **다중 도메인 로직**: 한 Service에서 여러 Repository 접근 가능 (예: BoardService)
   - 이 경우 트랜잭션 경계 신경 써야 함

5. **보안**: 모든 사용자 관련 작업은 `CurrentUserService`로 현재 사용자 확인
   - 데이터 소유권 검증 필수 (예: 본인의 게시글만 수정/삭제)

---

## 파일 참고
- 핵심 도메인: `src/main/java/com/team10/matchup/{board,team,user,match}/`
- 엔티티: `{Entity}.java` (JPA `@Entity`)
- Repository: `{Entity}Repository.java`
- Service: `{Entity}Service.java`
- Controller: `{Entity}Controller.java` 또는 `{Entity}PageController.java`
- DTO: `{Entity}Request/Response.java` (in `dto/` 폴더)

---

## **Match 모듈(구체 패턴)**

- **주요 책임**: 매치 게시글 생성/조회(`MatchPost`), 매치 신청(`MatchRequest`), 신청 수락/거절, 매치 상태 전환(OPEN → MATCHED 등).
- **현재 사용자 접근**: `CurrentUserService`로 현재 사용자와 팀을 조회하여 권한/행동 결정 (예: `MatchService.createMatchPost`, `MatchController.matchApplyPage`).
- **상태 관리**: 엔티티는 상태를 일부 `String` 필드로 저장(`MatchPost.status`, `MatchRequest.status`)하고, 코드에서는 `MatchStatus`, `MatchRequestStatus` enum을 함께 사용함 — 새 코드를 추가할 때는 string-대-열거형 혼용을 주의하세요.
  - 예: `MatchPost.status`는 기본값으로 `"OPEN"`을 사용하고, 서비스는 문자열 상수(`"MATCHED"`)로 비교합니다.
- **Repository 패턴**: 간단한 쿼리는 메서드 네이밍으로 작성 (`findAllByOrderByCreatedAtDesc`, `findByTeamAndStatusOrderByMatchDatetimeDesc`) — 복잡한 집계가 필요하면 `@Query` 사용.
- **컨트롤러 흐름 예시**: `MatchController.matchApplyPage`는 서버 측에서 필터(`available`/`unavailable`)를 적용한 뒤 뷰에 `matchPosts`와 `requestStatusMap`을 전달합니다. 뷰는 이 데이터를 바탕으로 신청 버튼 활성화 여부를 결정합니다.
- **비즈니스 규칙 (요약)**:
  - 사용자는 팀이 있어야 매치 신청/생성 가능 (없으면 `noTeam` 뷰 플래그)
  - 동일 사용자의 중복 신청 금지: `MatchService.requestMatch`가 이미 존재하는 신청을 방지
  - 매치 삭제 제약: 작성자만 삭제 가능하고, 상태가 `MATCHED`이면 삭제 불가

참고 파일:
- `src/main/java/com/team10/matchup/match/MatchService.java`
- `src/main/java/com/team10/matchup/match/MatchController.java`
- `src/main/java/com/team10/matchup/match/MatchPost.java`
- `src/main/java/com/team10/matchup/match/MatchRequest.java`
- `src/main/java/com/team10/matchup/match/MatchPostRepository.java`

