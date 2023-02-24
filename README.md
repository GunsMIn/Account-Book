# 가계부  RestAPI

## RestApi Swagger 
### [http://ec2-3-39-231-24.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui](http://ec2-3-39-231-24.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui/index.html#/)</br>

## 개발환경

<br>

- **Java 11**
- **Build** : Gradle 7.6
- **Framework** : Springboot 2.7.7
- **CI & CD** : GitLab
- **Server** : AWS EC2
- **Deploy** : Docker
- **IDE** : IntelliJ
<br>

## 구현 완료
**Function** | **완료** | 
:------------ | :-------------| 
**Swagger** | :heavy_check_mark: |  
**Spring Security 인증 / 인가 필터 구현** | :heavy_check_mark: |  
**회원가입 / 로그인 구현 (JWT 토큰 발급, reFresh Token )** | :heavy_check_mark: |  
**회원 등급 변경(USER<=>ADMIN) / 회원 삭제(soft delete)** | :heavy_check_mark: |  
**가계부(AccountBook) 작성, 수정, 삭제, 리스트** | :heavy_check_mark: |  
**가계부 기록(Record) 작성, 수정, 삭제, 복원,리스트** | :heavy_check_mark: |  
**가계부 기록 수정, 삭제, 복원 시 가계부 잔고 정상적인 계산 로직** | :heavy_check_mark: | 
**AWS EC2 서버 Docker 배포** | :heavy_check_mark: |  
**Gitlab CI & Crontab CD** | :heavy_check_mark: 

# ERD 다이어그램
<br>

![가계부 (2)](https://user-images.githubusercontent.com/104709432/212717278-c8bf145c-95c7-47a4-b4a2-d568a36bc3e6.png)

</br>

- **user : 회원 테이블**
  - user_id : Primary key
  - email : 이메일
  - name : 회원 이름
  - password: 비밀번호
  - role : userRole(USER,ADMIN)
- **account_book : 가계부 테이블**
  - account_book_id : Primary key
  - title : 가계부 제목
  - memo : 가계부 머릿글 메모
  - balance : 잔고 
- **record : 가계부 기록 테이블**
  - record_id : Primary key 
  - money : 돈 (지출 할 돈/ 저축 할 돈)
  - memo : 가계부 기록 시 메모 
  - act : 기록 행위(지출 / 저축)
  - expend_type : 지출 / 저축 종류
  - day : 요일
- **공통 컬럼**
  - registerd_at : 생성 시간
  - updated_at : 수정 시간
  - deleted_at : 삭제 시간

## 🔽 RestAPI EndPoint

| METHOD | URI                                | 기능               | RequestBody                                      |인증필요             |
| ------ | ---------------------------------- |---------------------------------------------------| ------------------------------------- |----------- |
| POST   | /api/**users**/join                 | 회원가입                      | {"username": "string", "email":"string", "password":"string"} |  | 
| POST   | /api/**users**/login                | 로그인                       | {"email": "string", "password":"string"} | | 
| POST   | /api/**users**/{id}/role_change               | 회원 등급 변경(ADMIN 등급만 가능)                     | {"role": "string"} | ✔| 
| DELETE   | /api/**users**/{id}e               | 회원 삭제(Soft Delete)                     |  |✔ | 
| GET    | /api/**account_books**                     |해당 회원의 가계부 전체 리스트(최신 가계부 5개 페이징 처리)  |                                           | ✔| 
| GET    | /api/**account_books**/{id}                     | 가계부 단건 조회   |                                           |✔ | 
| POST    | /api/**account_books**/{id}             | 가계부 생성             | {"title": "string", "memo":"string", "balance":"string"}                                           |✔ | 
| PATCH   | /api/**account_books**/{id}                      | 가계부 수정  |  {"title": "string", "memo":"string", "balance":"string"}   |✔ | 
| DELETE    | /api/v1/**account_books**/{postId}             | 가계부 삭제(Soft Delete)   |   |✔ | 
| GET | /api/account_books/{bookId}/**records**/{recordId}             | 가계부 기록 조회  |                                           | ✔| 
| GET | /api/account_books/{bookId}/**records**           | 가계부 기록 리스트 보기(최신순,20개) |                                           |✔ | 
| POST | /api/account_books/{bookId}/**records**/          | 가계부 기록 쓰기 |{"money": "string", "memo":"string", "act":"string", "expendType":"string", "day":"string"}                                           |✔ | 
| PATCH |/api/account_books/{bookId}/**records**/{recordId}       | 가계부 기록 수정(with 가계부 잔고 수정) |{"money": "string", "memo":"string", "act":"string", "expendType":"string", "day":"string"}                                           |✔ | 
| DELETE | /api/account_books/{bookId}/**records**/{recordId}            | 가계부 기록 삭제하기(with 가계부 잔고 복원 ) |                                           |✔ |
| POST | /api/account_books/{bookId}/**records**/{recordId}/resave            | 가계부 삭제된 기록 복원하기 (with 가계부 잔고 복원 ) |                                           |✔ |

## 설계
**가계부 기능에 관련된 API의 요구사항을 보면서 우선 회원테이블 , 가계부 테이블 그리고 가계부에 대한 글 작성을 할 수 있는 가계부 기록테이블을 생각하게되었다. 회원(user)과 가계부(account_book)은 1 대 다 관계로 가계부(account_book)테이블이 회원의 PK(user_id)를 참조키로 갖게 설계하였고 또한 회원과  가계부 기록(record) 도 1 대 다 관계이므로 기록(record)테이블도 회원의 PK(user_id)를 참조키로 갖게 설계하였다. 가계부(account_book)와 가계부 기록(record)도 1 대 다 관계로 설계하여 기록(record)테이블에서 가계부의 PK(account_book_id)를 참조키로 갖게 설계하였다. 삭제에서는 논리 삭제를 구현하기위해 soft-delete 방법을 채택하였다. 따라서 각 테이블에 deleted_at이라는 컬럼을 추가하여 삭제 시에는 이 컬럼에 현재시간의 값이 들어갈 수 있게 설계하였다.
또한 가계부 기록(record)에서 기록의 행위(Act) , 지출/저축의 종류(expend_type) , 요일(Day)을 Enum으로 만들어주어서  허용 가능한 값을 제한 할 수 있게 그리고 리팩토링 시 변경 범위가 최소화되게 제작해주었다. 또한 CustomError(자체 에러 클래스)를 만들어주어서 직관적으로 예외처리(exception handling)를 각 상황에 맞게 처리해주었다. 또한 ValidateService라는 별도의 비지니스 검증 로직들이 있는 서비스를 제작해주어서 코드의 중복을 최소화하려고 했다. 하지만 코드를 너무 많이 메서드 추출화 하면 오히려 가독성에 해칠 수 있기 때문에 직관적으로 보기 편할 수 있는 부분은 메서드 추출하지 않았다. 가계부 기록(record)과 가계부(account_book)의 dirty check 수정 메서드는 도메인 주도 설계 방식을 채택하였고 가계부 기록(record)의 money의 저축/지출 , 수정 , 삭제시 가계부(account_book)의 잔고(balance)의 변경 또한 도메인 주도 설계로 가져갔다. 엔티티와 DTO의 철저하게 분리하여 API 스펙을 유지하면서 관심사를 분리하였다. 그리고 엔티티에서 setter 사용을 금지하며 생성자와 builder를 채택하여 무분별한 수정을 방지하도록 설계를 가져갔다.**


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

 ### 3. 회원 등급 변경  (POST) : /api/**users**/{id}/role_change 
> - 인증된 사용자 인지 확인
> - ADMIN 회원만이 일반 회원을 ADMIN으로 승격시키는 API(일반회원 등급업 기능 불가)
> - 해당 회원이 ADMIN인지 검사(userRole check)

### 4. 회원 삭제  (DELETE) : /api/**users**/{id}
> - 인증된 사용자 인지 확인
> - 로그인 한 회원이 삭제하려는 회원인지 검사
> - Soft Delete 논리삭제 구현
> - 가계부와 양방향 매핑을 가져가면서 회원삭제 시 가계부(AccountBook), 가계부 기록(Record) 삭제
 ## Account_Book(가계부)
  ### 5. 가계부 생성 (POST) : /api/**account_books** 
> - 인증된 사용자 인지 확인
> - 로그인하지 않은 고객은 가계부 내역에 대한 접근 제한 처리
> - 가계부 잔고(balance) , 제목(title) , 메모(memo)를 작성하여 가계부 생성
> - 엔티티와 DTO의 분리로 API 스펙의 유지
   ### 6. 가계부 수정 (PATCH) : /api/**account_books**/{id} 
> - 인증된 사용자 인지 확인(가계부 생성한 사람인지 타인인지 검증)
> - 로그인하지 않은 고객은 가계부 수정에 대한 접근 제한 처리
> - id로 해당 가계부 조회 후 Dirty check를 이용하여 가계부 잔고(balance) , 제목(title) , 메모(memo) 수정
> - 엔티티와 DTO의 분리로 API 스펙의 유지

 ### 7. 가계부 삭제 (DELETE) : /api/**account_books**/{id} 
> - 인증된 사용자 인지 확인(가계부 생성한 사람인지 타인인지 검증)
> - 로그인하지 않은 고객은 가계부 삭제에 대한 접근 제한 처리
> - id로 해당 가계부 조회 후 해당 가계부 삭제 처리

   ### 8. 가계부 단건 조회 (GET) : /api/**account_books**/{id} 
> - 인증된 사용자 인지 확인
> - 로그인하지 않은 고객은 가계부 조회에 대한 접근 제한 처리
> - id로 해당 가계부 조회 
> - 엔티티와 DTO의 분리로 API 스펙의 유지

   ### 9. 가계부 전체 조회 (GET) : /api/**account_books**
> - 인증된 사용자 인지 확인
> - 최신순으로 작성한 가계부 페이징 처리 조회(COUNT : 5)
> - 로그인한 사용자 조회 후 가계부 리스트 조회
> - 엔티티와 DTO의 분리로 API 스펙의 유지

## Record(가계부 기록)
  ### 10. 가계부 기록 쓰기 (POST) : /api/account_books/{bookId}/**records** 
> - 인증된 사용자 인지 확인
> - 가계부 기록을 위한 **ACT(행위) , ExpendType(지출 종류) , Day(요일) 을 Enum으로 제작
 가계부 기록 Request 제한**
***
#### Act(기록 행위)
```java
public enum Act {
    SPENDING("지출"), SAVING("저축");
   }
```
- Act("지출","저축")에 해당하지 않는 요청 값 들어올 시 RecordException(ErrorCode.Act) [406 상태코드 반환]
***
#### ExpendType(지출,저축 종류)
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
    ETC_EXPENSE("기타비용"),
    SAVE("저축");
    }
```
- ExpendType("식비","생활용품비","교통비","의류비","병원비","유흥비","놀이비","통신비","공과금","기타비용")에 해당하지 않는 요청 값 들어올 시 RecordException(ErrorCode.EXPENDTYPE_FAULT) [406 상태코드 반환]
- 저축시에는 SAVE("저축") 입력
***
#### Day
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
- DAY("월","화","수","목","금","토","일")에 해당하지 않는 요청 값 들어올 시 RecordException(ErrorCode.DAY_FAULT) [406 상태코드 반환]
***

  ### 11. 가계부 기록 수정 (PATCH) : /api/account_books/{bookId}/**records**/{recordId}
> - 인증된 사용자 인지 확인
> - 가계부 기록을 위한 ACT(행위) , ExpendType(지출 종류) , Day(요일) 을 **Enum**으로 제작
> - 영속성 컨텍스트의 스냅샷을 이용한 Dirty Check(변경감지)를 이용한 수정
> - 가계부 기록(Record)의 money가 수정되면 가계부(account_book)의 잔고(balance) 또한 수정
> - 가계부 수정을 위한 **ACT(행위) , ExpendType(지출 종류) , Day(요일) 을 Enum으로 제작
 가계부 수정 Request 제한**
***
#### Act(기록 행위)
```java
public enum Act {
    SPENDING("지출"), SAVING("저축");
   }
```
- Act("지출","저축")에 해당하지 않는 요청 값 들어올 시 RecordException(ErrorCode.Act) [406 상태코드 반환]
***
#### ExpendType(지출,저축 종류)
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
    ETC_EXPENSE("기타비용"),
    SAVE("저축");
    }
```
- ExpendType("식비","생활용품비","교통비","의류비","병원비","유흥비","놀이비","통신비","공과금","기타비용")에 해당하지 않는 요청 값 들어올 시 RecordException(ErrorCode.EXPENDTYPE_FAULT) [406 상태코드 반환]
- 저축시에는 SAVE("저축") 입력
***
#### Day
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
- DAY("월","화","수","목","금","토","일")에 해당하지 않는 요청 값 들어올 시 RecordException(ErrorCode.DAY_FAULT) [406 상태코드 반환]
***
  ### 12. 가계부 기록 삭제 (DELETE) : /api/account_books/{bookId}/**records**/{recordId} 
> - 인증된 사용자 인지 확인
> - 가계부 기록 삭제한 후에 복원할 수 있는 **Soft Delete 방식 채택**
> - 엔티티에 deleted_at 필드를 추가하고 **@SQLDelete(sql = "UPDATE Record SET deleted_at = now() WHERE id = ?")를 사용하여 삭제 시 해당 시간으로 삭제시간 값 들어감**
> - 삭제시간이 존재할 시 -> 삭제된 기록 / **삭제시간이 Null일 시 존재하는 가계부 기록(Record)**
> > - 조회 시 @Where(clause = "deleted_at is null")가 조회 시 자동으로 조건으로 붙어서 존재하는 기록만 조회 가능
> - 가계부 기록(Record)을 삭제 시 가계부(Account_Book)의 잔고(balance) 또한 잔고 맞춤 기능  

  ### 13. 가계부 기록 복원 (POST) : /api/account_books/{bookId}/**records**/{recordId}/restore 
> - 인증된 사용자 인지 확인
> - 가계부 기록 삭제한 후에 복원할 수 있는 Soft Delete 방식 채택
> - @PathVaraible 로 들어온 id로 해당 삭제된 가계부 기록(Record)를 조회 후 삭제시간을 다시 Null로 만들어줌 => 가계부 기록 복원
> - 가계부 기록(Record)을 복원 시 가계부(Account_Book)의 잔고(balance) 또한 복원 기능

  ### 14. 가계부 기록 단건 조회 (GET) : /api/account_books/{bookId}/**records**/{recordId}
> - 인증된 사용자 인지 확인
> - @PathVaraible 로 들어온 id로 해당 가계부 기록 단건 조회

  ### 15. 가계부 기록 페이징 조회 (GET) : /api/account_books/{bookId}/**records**
> - 인증된 사용자 인지 확인
> - 인증된 사용자의 가계부 기록 20개 최신순으로 페이징 조회
  
<br>

## Error Info

| Status Code | Error Message        | When                                               |
|-------------|----------------------|----------------------------------------------------|
| 409         | DUPLICATED_EMAIL | 회원 가입 시 중복일 때 발생                                   |
| 404         | EMAIL_NOT_FOUND   | DB에 저장된 Email이 없는 경우 발생                              |
| 404         | ACCOUNTBOOK_NOT_FOUND       | 상세 조회, 삭제, 수정 요청 시, 요청한 account_book_id 해당하는 가계부가 없는 경우 발생 |
| 404         | RECORD_NOT_FOUND    | 요청한 record_id에 해당하는 가계부 기록(Record)가 없는 경우 발생   |
| 406         | ACT_FAULT      | 가계부 기록 등록시 Act를 지출/저축 외의 요청 시 발생          |
| 406         | EXPEND_FAULT      | 가계부 기록 등록시 ExpendType(지출 종류)가 지정 된 이외의 요청 시 발생          |
| 406         | DAY_FAULT      | 가계부 기록 등록시 Dau(요일)이 지정 된 이외의 요청 시 발생          |
| 401         | INVALID_PASSWORD     | 로그인 시 패스워드 잘못 입력한 경우 발생                            |
| 401         | INVALID_TOKEN        | jwt 토큰이 아니거나 유효하지 않은 토큰으로 요청할 시 발생                 |
| 401         | INVALID_PERMISSION       | 요청자와 소유권자가 다른 경우 발생               |
| 406         | USER_ROLE_NOT_FOUND    | ADMIN의 권한 변경시 요청값이 USER,ADMIN 외의 값이 들어올 때 발생        |
| 500         | DATABASE_ERROR       | DB 연결이 끊어질 경우 발생                                   |

<br>

에러 발생 시, 예시

```json
{
  "resultCode": "ERROR",
  "result": {
    "errorCode": "RECORD_NOT_FOUND",
    "message": "해당 가계부의 기록을 찾을 수 없습니다"
  }
}
```

<br>

---

## Jacoco TestReport✅

<img width="845" alt="JOCOCOTEST" src="https://user-images.githubusercontent.com/104709432/212541689-78a3dbb8-d2cc-4c74-9527-1399b78a434b.PNG">


