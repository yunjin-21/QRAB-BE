# 💡 졸업 캡스톤 프로젝트 QRAB(큐랩)
## 대학생을 위한 AI 기반 퀴즈 생성을 통한 학습 보조 서비스

<br>

## ⚙️ 시스템 구조도
<img src="https://github.com/user-attachments/assets/85ff44ef-94a3-427b-9310-d0b1c9643cff" width="500" alt="image">


## 👩‍💻 Back-End 개발 담당한 파트


|[이윤진](https://github.com/yunjin-21)|
| :-: |
|  <img src="https://avatars.githubusercontent.com/yunjin-21" width="150"> |
|DB 설계 및 API 명세서 작성,<br> 회원가입/로그인 API 개발,<br>Category API 개발,<br>Note API 개발,<br>Friendship API 개발,<br>Profile API 개발,<br>MainPage API 개발,<br> 배포 환경 세팅 및 구축|


<br>


## 📊 기술 스택
- Java 17
- Spring Boot
- MySQL
- AWS RDS, EC2, S3, Route53, ACM, Elastic Beanstalk, SES
- JSoup, Selenium Web Driver, Apache PDFBox, Naver Clover OCR API, ChatGPT-4o mini API

## 📊 ERD 설계

![ER 다이어그램 이미지](https://drive.google.com/uc?id=1ey8cE0OzjvX_C4eHsDdJUubtrPsBgp8R) 

## 💻 API 명세서
---
### 👤 회원가입 & 로그인:  `/users`
| Method | Description | URI |
| ------- | --- | --- |
| POST | 회원가입 |/users/signup |
| POST | 이메일 중복 체크 | /users/check-email?email={email} |
| POST | 닉네임 중복 체크 | /users/check-nickname?nickname={nickname} |
| POST | 로그인 | /users/authenticate |


---
### 📝 Note Page: `/notes`
| Method | Description | URI |
| ------- | --- | --- |
| GET | 전체 노트 저장소 조회 | /notes?page={page} |
| GET | 특정 노트 요약본 조회 | /notes/{noteId}/summary |
| POST | 티스토리/벨로그 크롤링(JSoup) + 타웹사이트 크롤링(Selenium Web Driver + Naver Clover OCR API) → ChatGPT-4o mini API로 요약본 생성 | /notes/crawl/url |
| POST | PDF 파일 크롤링 (Apache PDFBox) / 이미지 파일 크롤링(Naver Clover OCR API) → ChatGPT-4o mini API로 요약본 생성 | /notes/crawl/file |
| POST | 카테고리별 노트 조회 | /notes/{categoryId}?page={page} | 
| POST | 접근제한자 토글 추가 (기본 public) | /notes/{noteId}/view | 


---
### 🔎 Category Page: `/categories` 
| Method | Description | URI |
| ------- | --- | --- |
| POST | 상위 카테고리 추가 | /categories |
| POST | 하위 카테고리 추가 | /categories/child |
| DELETE | 카테고리 삭제 | /categories |
| PUT | 카테고리 수정 | /categories/update |
| GET | 상위 카테고리 조회 | /categories/parent |
| GET | 상위 카테고리 click → 선택된 해당 상위 카테고리의 하위 카테고리들 조회 | /categories/parent/{parentId}/child |


---
### 💁 Friend Page: `/firends`
| Method | Description | URI |
| ------- | --- | --- |
| POST | 친구 추가 | /friends |
| DELETE | 친구 삭제 | /friends/{friendshipId} |
| GET | 친구의 노트 조회 | /friends/{friendshipId}/notes?page={page} |
| POST | 친구의 노트 추가 | /friends/notes/{noteId} |
| GET | 카테고리별 친구의 노트들 조회 | /friends/{friendshipId}/notes/{categoryId}?page={page} |


---
### 📕 Profile Page: `/profiles`
| Method | Description | URI |
| ------- | --- | --- |
| POST | 프로필 + 친구 목록 조회 | /profiles |
| PATCH | 프로필 정보(이미지, 닉네임, 패스워드, 폰번호) 편집 | /profiles/updateProfile |
| PUT | 사용자 학과 편집  | /profiles/updateMajor |
| POST | 메일 알림 설정 여부 (기본값 : 설정x) | /profiles/notifications |
| POST | 메일 정보 등록 | /profiles/emails |


---
### 🏠 Main Page: `/qrab`
| Method | Description | URI |
| ------- | --- | --- |
| GET | 연속 학습일수 + 이번달 학습 일수 + 스코어 랭킹 보드 + 별자리 개수 + 최근 노트 + 최근 퀴즈 세트 + 최근 틀린 퀴즈 + 이번달 학습기록 + 이번달 별자리 기록 조회 | /qrab |

