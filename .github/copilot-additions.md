# 추가 가이드: Security · Templates · Notifications · 민감정보

## Security 모듈 (실전 규칙)

- 핵심 파일: `security/SecurityConfig.java`, `security/UserSecurityService.java`, `security/CustomUserDetails.java`, `security/LoginFailureHandler.java`.
- 인증 사용자 정보: `CustomUserDetails`는 `id`와 `teamId`를 제공하므로 서비스/컨트롤러에서 현재 사용자의 팀을 확인할 때 `CurrentUserService`와 함께 `teamId`를 사용합니다.
  - 예: `CurrentUserService.getCurrentUserTeamOrNull()`는 로그인 유저의 팀 여부를 검사 — 매치/팀 흐름에서 필수 체크입니다.
- 권한/보안 체크: 리소스의 소유권 검증(예: 게시글/매치 삭제)은 서비스에서 수동으로 검증합니다. 패턴 예: `post.getCreatedBy().getId().equals(current.getId())`.

---

## 템플릿 / 뷰 계약 (Thymeleaf)

- 템플릿 위치: `src/main/resources/templates/` (예: `board/`, `team_*.html`, `match_apply.html`).
- 데이터 계약: 컨트롤러가 뷰에 넘기는 모델 속성 이름을 변경하면 템플릿이 깨집니다. 자주 쓰이는 속성 예:
  - `match_apply` 뷰: `matchPosts`, `requestStatusMap`, `team`, `currentUser`, `filter`, `matchCreateForm`
  - `board_detail` 뷰: `board`, `comments`, `likeCount`, `currentUser`
- 뷰-컨트롤러 협약: 서버에서 버튼/상태 활성화를 결정하는 방식(예: `requestStatusMap`)을 따르세요 — 뷰는 단순 렌더링 역할입니다.

---

## Notification 흐름 (알림)

- 핵심 파일: `notification/NotificationService.java`, `notification/NotificationController.java`, `notification/NotificationAdvice.java`, `notification/NotificationType.java`, `notification/NotificationRepository.java`.
- 패턴: 비즈니스 이벤트(매치 신청/수락/거절 등) 발생 시 `NotificationService`가 알림을 생성하고 저장합니다. `NotificationAdvice`는 공통 모델 속성으로 알림 목록을 주입해 헤더에서 알림을 표시합니다.
- 유의점: 알림을 외부 전송(이메일/푸시)으로 확장할 때는 중복 생성 방지, 수신자 권한 검증, 정렬(생성 시간) 규칙을 먼저 정의하세요.

---

## 민감정보 · DB 마이그레이션 주의

- `src/main/resources/application.properties`에 DB 비밀번호가 하드코딩되어 있습니다. 공개 푸시 전에는 비밀번호를 반드시 제거하거나 마스킹하세요. 예: `spring.datasource.password=PASSWORD_REMOVED`.

PowerShell로 간단히 치환하는 예:
```powershell
# application.properties에서 비밀번호 마스킹
(Get-Content .\src\main\resources\application.properties) -replace 'spring.datasource.password=.*','spring.datasource.password=PASSWORD_REMOVED' | Set-Content .\src\main\resources\application.properties
```

- DDL 자동화 비활성화: `spring.jpa.hibernate.ddl-auto=none`으로 설정되어 있으므로 스키마 변경은 수동 SQL 또는 마이그레이션 도구(Flyway 등)를 사용해야 합니다. 개발/테스트에서는 별도의 `application-test.properties`로 In-memory DB(H2) 설정을 권장합니다.

---

## 자주 발생하는 실수 / 주의 포인트

- Enum vs String 혼용: `MatchPost.status` 같은 필드가 DB에는 `String`으로 저장되고 코드에는 `MatchStatus` enum이 존재합니다. 새로운 상태를 추가하거나 비교할 때 저장 타입과 코드를 일치시키세요.
- Soft Delete 필터 누락: 게시판 엔티티는 `deleted` 플래그를 사용합니다. 쿼리에 `DeletedFalse` 조건을 빼먹지 마세요.
- 트랜잭션 경계: 여러 Repository 호출을 포함하는 비즈니스 로직은 `@Transactional`을 명확히 지정하세요. 읽기 쪽은 `readOnly = true`를 사용하면 성능에 도움이 됩니다.

---

# 끝
