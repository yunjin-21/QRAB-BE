# Qrab

<br>

## 👩‍💻 Back-End 개발 맡은 파트

|[이윤진](https://github.com/yunjin-21)|
| :-: |
|  <img src="https://avatars.githubusercontent.com/yunjin-21" width="150"> |
|DB 설계 및 API 명세서 작성,<br>회원가입/로그인 API 개발,<br>Category API 개발,<br>Note API 개발,<br>Friendship API 개발,<br>Profile API 개발,<br>MainPage API 개발,<br>배포 담당|


<br>


## 기술 스택
- Java 17
- Spring Boot
- MySQL
- AWS RDS, EC2, S3, VPC, Route53, Elastic Beanstalk

## API 명세서
---
### 👤 회원가입 & 로그인:  `/users`
| Method | Description | URI |
| ------- | --- | --- |
| POST | 회원가입 |/users/signup |
| POST | 이메일 중복 체크 | /users/check-email?email={email} |
| POST | 닉네임 중복 체크 | /users/check-nickname?nickname={nickname} |
| POST | 로그인 | /users/authenticate |

---
### 🪪 Profile Page:  `/profiles`

| Method | Description | URI |
| ------- | --- | --- |
| GET | 프로필 정보 + 친구 목록 조회 |/profiles |
| PUT | 프로필 정보 수정 | /profiles/update |
| POST | 퀴즈 알림 설정 | /profiles/notifications/time |

---
### 🏠 Main Page: `/qrab`
| Method | Description | URI |
| ------- | --- | --- |
| GET | 메인 페이지 조회 |/qrab |

---
### 📝 Note Page: `/notes`
| Method | Description | URI |
| ------- | --- | --- |
| GET | 노트 저장소 페이지 조회 | /notes?page={page} |
| GET | 특정 노트 요약본 조회 | /notes/{noteId}/summary |
| POST | 티스토리/벨로그 크롤링 + url 크롤링(스크린샷 + Naver OCR API 활용) →  ChatGPT API 이용해 요약본 생성 | /notes/crawl/url |
| POST | PDF 파일 크롤링 /  이미지 파일 크롤링 → ChatGPT API 이용해 요약본 생성 | /notes/crawl/file |
| POST | 접근제한자 설정 | /notes/{noteId} | 

---
### 🔎 Category Page: `/categories` 
| Method | Description | URI |
| ------- | --- | --- |
| POST | 상위 카테고리 추가 | /categories |
| POST | 하위 카테고리 추가 | /categories/child |
| DELETE | 카테고리 삭제 | /categories/{categoryId} |
| PUT | 카테고리 수정 | /categories/update |
| GET | 상위 카테고리 조회 | /categories/parent |
| GET | 상위 + 하위 카테고리 조회 | /categories/parent/child/{parentId} |

---
### 💁 Friend Page: `/firends`
| Method | Description | URI |
| ------- | --- | --- |
| POST | 친구 추가 | /friends |
| DELETE | 친구 삭제 | /friends/{friendshipId} |
| GET | 친구의 노트 조회 | /friends/{friendshipId}/notes?page={page} |
| POST | 친구의 노트 추가 | /friends/{frinedshipId}/notes/{noteId} |





