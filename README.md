# Account-Book(가계부  RestApi)
# JPA를 이용한 RestApi 구현 / Spring Security  인증,인가 /JWT 토큰 / CI-CD구축 


***

## 구현 완료
**Function** | **완료** | 
:------------ | :-------------| 
**Swagger** | :heavy_check_mark: |  
**Spring Security 인증 / 인가 필터 구현** | :heavy_check_mark: |  
**회원가입 / 로그인 구현 (JWT 토큰 발급)** | :heavy_check_mark: |  
**가계부(AccountBook) 작성, 수정, 삭제, 리스트** | :heavy_check_mark: |  
**가계부 기록(Record) 작성, 수정, 삭제, 리스트** | :heavy_check_mark: |  
**AWS EC2에 Docker 배포** | :heavy_check_mark: |  
**Gitlab CI & Crontab CD** | :heavy_check_mark: 

# ERD 다이어그램


### 테스트 전용 로그인 회원
**User**
> - email : test@gmail.com
>
> - PW : kk1234
<hr>

## 🔽 RestAPI EndPoint

| METHOD | URI                                | 기능               | RequestBody                                      |인증필요             |
| ------ | ---------------------------------- |---------------------------| ------------------------------------- |----------- |
| POST   | /api/**users**/join                 | 회원가입                      | {"username": "string", "email":"string", "password":"string"} |  | 
| POST   | /api/**users**/login                | 로그인                       | {"email": "string", "password":"string"} | | 
| GET    | /api/**account_books**                     |해당 회원의 가계부 전체 리스트(최신 가계부 5개 페이징 처리)  |                                           | ✔| 
| GET    | /api/**account_books**/{id}                     | 가계부 단건 조회   |                                           |✔ | 
| POST    | /api/**account_books**/{id}             | 가계부 생성             | {"title": "string", "memo":"string", "balance":"string"}                                           |✔ | 
| PATCH   | /api/**account_books**/{id}                      | 가계부 수정  |  {"title": "string", "memo":"string", "balance":"string"}   |✔ | 
| DELETE    | /api/v1/**posts**/{postId}             | 가계부 삭제 |   |✔ | 
| GET | /api/account_books/{bookId}/records/{recordId}             | 가계부 기록 조회  |                                           | ✔| 
| GET | /api/account_books/{bookId}/records           | 가계부 기록 리스트 보기(최신순,20개) |                                           |✔ | 
| POST | /api/account_books/{bookId}/records/          | 가계부 기록 쓰기 |                                           |✔ | 
| PATCH |/api/account_books/{bookId}/records/{recordId}       | 가계부 기록 수정(with 가계부 잔고 수정) |{"money": "string", "memo":"string", "act":"string", "expendType":"string", "day":"string"}                                           |✔ | 
| DELETE | /api/account_books/{bookId}/records/{recordId}            | 가계부 기록 삭제하기(with 가계부 잔고 복원 ) |                                           |✔ | 


