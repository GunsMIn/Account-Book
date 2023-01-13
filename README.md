# Account-Book(가계부  RestApi)
## JPA를 이용한 RestApi 구현 / Spring Security  인증,인가 / JWT 토큰 / CI-CD구축 


***

## 구현 완료
**Function** | **완료** | 
:------------ | :-------------| 
**Swagger** | :heavy_check_mark: |  
**Spring Security 인증 / 인가 필터 구현** | :heavy_check_mark: |  
**회원가입 / 로그인 구현 (JWT 토큰 발급)** | :heavy_check_mark: |  
**가계부(AccountBook) 작성, 수정, 삭제, 리스트** | :heavy_check_mark: |  
**가계부 기록(Record) 작성, 수정, 삭제, 복원,리스트** | :heavy_check_mark: |  
**가계부 기록 수정, 삭제, 복원 시 가계부 잔고 정상적인 계산 로직** | :heavy_check_mark: | 
**AWS EC2에 Docker 배포** | :heavy_check_mark: |  
**Gitlab CI & Crontab CD** | :heavy_check_mark: 

# ERD 다이어그램
- **User : 회원 테이블**
- **Account_Book : 가계부 테이블**
- **Record : 가계부 기록 테이블**
<img width="500" alt="다이어그램" src="https://user-images.githubusercontent.com/104709432/211975196-96e9687a-32af-4ba7-bf7b-5f0b63fd9f14.PNG">



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
| DELETE    | /api/v1/**account_books**/{postId}             | 가계부 삭제 |   |✔ | 
| GET | /api/account_books/{bookId}/**records**/{recordId}             | 가계부 기록 조회  |                                           | ✔| 
| GET | /api/account_books/{bookId}/**records**           | 가계부 기록 리스트 보기(최신순,20개) |                                           |✔ | 
| POST | /api/account_books/{bookId}/**records**/          | 가계부 기록 쓰기 |{"money": "string", "memo":"string", "act":"string", "expendType":"string", "day":"string"}                                           |✔ | 
| PATCH |/api/account_books/{bookId}/**records**/{recordId}       | 가계부 기록 수정(with 가계부 잔고 수정) |{"money": "string", "memo":"string", "act":"string", "expendType":"string", "day":"string"}                                           |✔ | 
| DELETE | /api/account_books/{bookId}/**records**/{recordId}            | 가계부 기록 삭제하기(with 가계부 잔고 복원 ) |                                           |✔ | 

## USER(회원)
### 1. 회원가입 (POST) : /api/**users**/join 
> - 이메일 중복 체크하여 회원가입
> - 비밀번호 암호화 진행 후 DB에 저장
> - 엔티티와 DTO의 분리로 API 스펙의 유지
 
 ### 2. 로그인 (POST) : /api/**users**/login 
> - 회원가입 진행 후 email 유무 확인 
> - 비밀번호 일치/불일치 확인 작업
> - 위 2개의 검증이 끝난 후 jwt와 refresh token을 반환
> - 엔티티와 DTO의 분리로 API 스펙의 유지 
 ## Account_Book(가계부)
  ### 3. 가계부 생성 (POST) : /api/**account_books** 
> - 인증된 사용자 인지 확인
> - 로그인하지 않은 고객은 가계부 내역에 대한 접근 제한 처리
> - 가계부 잔고(balance) , 제목(title) , 메모(memo)를 작성하여 가계부 생성
> - 엔티티와 DTO의 분리로 API 스펙의 유지
   ### 4. 가계부 수정 (PATCH) : /api/**account_books**/{id} 
> - 인증된 사용자 인지 확인(가계부 생성한 사람인지 타인인지 검증)
> - 로그인하지 않은 고객은 가계부 수정에 대한 접근 제한 처리
> - id로 해당 가계부 조회 후 Dirty check를 이용하여 가계부 잔고(balance) , 제목(title) , 메모(memo) 수정
> - 엔티티와 DTO의 분리로 API 스펙의 유지

 ### 5. 가계부 삭제 (DELETE) : /api/**account_books**/{id} 
> - 인증된 사용자 인지 확인(가계부 생성한 사람인지 타인인지 검증)
> - 로그인하지 않은 고객은 가계부 삭제에 대한 접근 제한 처리
> - id로 해당 가계부 조회 후 해당 가계부 삭제 처리

   ### 6. 가계부 단건 조회 (GET) : /api/**account_books**/{id} 
> - 인증된 사용자 인지 확인
> - 로그인하지 않은 고객은 가계부 조회에 대한 접근 제한 처리
> - id로 해당 가계부 조회 
> - 엔티티와 DTO의 분리로 API 스펙의 유지

   ### 7. 가계부 전체 조회 (GET) : /api/**account_books**
> - 인증된 사용자 인지 확인
> - 최신순으로 작성한 가계부 페이징 처리 조회(COUNT : 5)
> - 로그인한 사용자 조회 후 가계부 리스트 조회
> - 엔티티와 DTO의 분리로 API 스펙의 유지

## Record(가계부 기록)
  ### 3. 가계부 기록 쓰기 (POST) : /api/account_books/{bookId}/**records** 
> - 인증된 사용자 인지 확인
> - 가계부 기록을 위한 ACT(행위) , ExpendType(지출 종류) , Day(요일) 을 **Enum**으로 제작
- Act
```java
public enum Act {
    SPENDING("지출"), SAVING("저축");
   }
```
- Act("지출","저축")에 해당하지 않는 요청값들어올 시 RecordException(ErrorCode.Act) [406 상태코드 반환]
- ExpendType 
```java
public enum ExpendType {
    FOOD_EXPENSE("식비"),
    LIVING_EXPENSE("생활용품비"),
    TRANSPORT_EXPENSE("교통비"),
    CLOTHING_EXPENSE("의류비"),
    HOSPITAL_EXPENSE("병원비"),
    ENTERTAIN_EXPENSE("유흥비"),
    CHILDCARE_EXPENSE("놀이비"),
    PHONE_EXPENSE("통신비"),
    UTILITY_EXPENSE("공과금"),
    ETC_EXPENSE("기타비용");
    }
```
- ExpendType("식비","생활용품비","교통비","의류비","병원비","유흥비","놀이비","통신비","공과금","기타비용")에 해당하지 않는 요청값들어올 시 RecordException(ErrorCode.EXPENDTYPE_FAULT) [406 상태코드 반환]
```java
public enum Day {
    MONDAY("월"),
    TUESDAY("화"),
    WEDNESDAY("수"),
    THURSDAY("목"),
    FRIDAY("금"),
    SATURDAY("토"),
    SUNDAY("일");
    }
```
- DAY("월","화","수","목","금","토","일")에 해당하지 않는 요청값들어올 시 RecordException(ErrorCode.DAY_FAULT) [406 상태코드 반환]
