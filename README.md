# waggle-was

# Commit convention
■ Commit Type
- feat: 기능 추가
- fix: 버그 수정 
- refactor: 리팩토링 
- docs: 문서 수정 
- test: 테스트 또는 테스트 코드 추가
- style: 코드 포맷팅, 세미콜론 누락, 코드 의미에 영향 없는 변경
- build: 빌드 시스템 수정, 외부 종속 라이브러리 수정(gradle, npm 등) 
- rename: 파일명 또는 폴더명 수정 
- remove: 코드 또는 파일 삭제 
- chore: 그 외 자잘한 수정

# Commit message 7가지 규칙
 
- 제목과 본문을 빈 행으로 구문한다
- 제목을 50글자 내로 제한
- 제목 첫 글자는 소문자로 작성
- 제목 끝에 마침표 넣지 않기
- 제목은 명령문으로 사용하며 과거형을 사용하지 않는다
- 본문의 각 행은 72글자 내로 제한
- 어떻게 보다는 무엇과 왜를 설명한다


# 예시
```
feat: Add user login API

Implement user login feature with JWT authentication
- Add login controller
- Add authentication service
- Add JWT token provider

This enables users to log in securely with email and password.
```
---
# Code convention
[네이버 자바 컨벤션](https://naver.github.io/hackday-conventions-java/#newline-after-annotation)
