# 💊 MyPill
`2023.06.19` ~ `2023.07.14`
> 영양제 이커머스 플랫폼</br>
MyPill에서는 설문 기반 맞춤형 영양소를 추천하고, 복약 관리 기능을 더한, 영양제 이커머스 플랫폼 입니다!
- [🔗 MyPill](https://www.mypill.shop)
- [🔗 시연영상](https://youtu.be/VYYzGUSq1Hk)
</br>

## 🦁 팀원 소개 
| [![](https://avatars.githubusercontent.com/u/94813918?v=4)](https://github.com/jny0) | [![](https://avatars.githubusercontent.com/u/99067128?v=4)](https://github.com/hojunking96)  | [![](https://avatars.githubusercontent.com/u/64017307?v=4)](https://github.com/leemimi) |  
|:---------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------------------------:|
|                            [박진영](https://github.com/jny0)                             |                            [송호준](https://github.com/hojunking96)                             |                                      [이미정](https://github.com/leemimi)  

</br>

## 💡 서비스 기획 의도

- 사회적으로 개인 건강 및 면역에 대한 관심이 높아짐에 따른 **영양제 소비 열풍**
- 시중에 영양제가 많아 **자신에게 맞는 영양제를 찾기 어려움**
- 사용자의 현재 건강상태를 바탕으로 **필요한 영양제들을 추천**해주고, 바로 관련 상품을 구매할 수 있도록 연결해주는 플랫폼
- **판매자가 직접 영양제 제품을 등록하고 판매**할 수 있어, 판매자와 구매자 모두 이용할 수 있는 플랫폼
- **복약관리 기능**까지 더해 기존 서비스들과 차별화

</br>

## 📢 프로젝트 소개

### 🔎 프로젝트 상세 설명

![008](https://github.com/MyPill2023/MyPill/assets/64017307/99b11419-98d4-45b0-bb44-849c2d908528)

### 💫 주요 기능
![010](https://github.com/MyPill2023/MyPill/assets/64017307/1838c57f-68d9-4b49-9c36-d1e5630ade67)
![011](https://github.com/MyPill2023/MyPill/assets/64017307/a8a65f94-160b-4c82-a3b6-f03a0f8d3b2b)
![012](https://github.com/MyPill2023/MyPill/assets/64017307/53a8c7c5-1986-4f66-9640-74a4657658c2)
![013](https://github.com/MyPill2023/MyPill/assets/64017307/bc80ae00-cab1-4b88-adcc-626fdd08c45b)
![014](https://github.com/MyPill2023/MyPill/assets/64017307/2263a6bb-1b59-4ab3-9716-86077b86113e)
![015](https://github.com/MyPill2023/MyPill/assets/64017307/8ae7bf83-1d38-467d-af89-4b7424258308)
![016](https://github.com/MyPill2023/MyPill/assets/64017307/5fcba384-7f75-43b2-9ea5-03e1ce98d566)

## 📌 기술 스택
![020](https://github.com/MyPill2023/MyPill/assets/64017307/55ae15a1-f06d-4920-867f-1f5cf3d5b4fb)

## ⚙️ 시스템 아키텍처
![ad5673640218fbc8](https://github.com/MyPill2023/MyPill/assets/64017307/5f4d29f8-89be-4518-9d75-6af04c8b3211)

## 📄 ERD

## 📂 개발 자료

- [🔗 API명세서]([https://youtu.be/VYYzGUSq1Hk](https://www.mypill.shop/swagger-ui/index.html#/))

<details>
<summary>요구사항 명세서</summary>

![요구사항명세서](https://github.com/MyPill2023/MyPill/assets/64017307/61beece2-6c77-473d-9534-c5bc8456cc73)

</details>

<details>
<summary>그라운드 룰</summary>

### **그라운드 룰**

🍎 정규 회의는 매일 13시 → 이전까지 구현 상태 체크 및 오늘 진행상황 공유

🗣 불참해야 할 일 생길 때, 하루 전에는 말을 해주기

⚠️ 공지 확인 시 12시간 내에 답장 해주기 + 공지 확인 후에 ✅ 체크 필수

📝 공부하다 모르는 내용 생기면 공유하고 서로 같이 고민하기

🗣 PR 승인 전에 코드 점검하고 확인 후 승인하기

</details>

<details>
<summary>코드 컨벤션</summary>

## ☑️ 코드 컨벤션


🐫 **함수명, 변수명은 소문자 카멜케이스로 작성**

🐫 **클래스, 생성자명은 대문자 카멜케이스로 작성**

©️상수명은 CONSTANT_CASE로 작성

  
1.메서드명은 동사, 혹은 동사구로 작성
    ex) sendMessage(O) mesasage(x)

2. 다른 변수와 상수들은 명사, 혹은 명사구로 작성
    ex) makeResult(X) taskResult(O)

- 객체 이름을 함수 이름에 중복해서 넣지 않기 (상위 이름을 하위 이름에 중복시키지 않기)
- 하나의 메소드와 클래스는 하나의 목적만 수행하게 만드는 것을 권장

**메소드 순서**

- public 먼저 다 적고 private 적기
- Controller의 메소드 호출 순서대로 Service 작성
- Controller: 조회→생성→수정→삭제

**메소드 컨벤션**

- 리소스 생성 `create()`
- 리소스 조회 `getXXX()`
- 리소스 목록 조회 `getList()`
- 리소스 수정 `update()`
- 리소스 삭제 `delete()`


**주석은 설명하려는 구문에 맞춰 들여쓰기**

```java
// Good
void someFunction() {
  ...

  // statement에 관한 주석
  statements
}
```

**이항** **연산자 사이에는 공백을 추가**

```java
a+b+c+d // bad
a + b + c + d // good
```

☝ 콤마 다음에 값이 올 경우 공백을 추가

```java
int[] arr = [1,2,3,4]; //bad
int[] arr = [1, 2, 3, 4]; //good
```

⚠️  1. @Override 어노테이션은 꼭 붙이자

1. try-catch문에서 어지간한 경우에는 catch문을 비워두지 말자. 아무것도 적지 않는 것이 확실히 맞다면 주석을 넣어둔다.

- 모든 예외 케이스에 대한 고려

</details>

<details>
<summary>Git Commit Message Convention</summary>

### 커밋 유형

- 대문자로 작성

| 커밋 유형 | 의미 |
| --- | --- |
| Feat | 새로운 기능 추가 |
| Fix | 버그 수정 |
| Docs | 문서 수정 |
| Style | 코드 formatting, 세미콜론 누락, 코드 자체의 변경이 없는 경우 |
| Refactor | 코드 리팩토링 |
| Test | 테스트 코드, 리팩토링 테스트 코드 추가 |
| Chore | 패키지 매니저 수정, 그 외 기타 수정 ex) .gitignore |
| Design | CSS 등 사용자 UI 디자인 변경 |
| Comment | 필요한 주석 추가 및 변경 |
| Rename | 파일 또는 폴더 명을 수정하거나 옮기는 작업만인 경우 |
| Remove | 파일을 삭제하는 작업만 수행한 경우 |
| !BREAKING CHANGE | 커다란 API 변경의 경우 |
| !HOTFIX | 급하게 치명적인 버그를 고쳐야 하는 경우 |
| Deploy | 배포 관련 |
- 한 커밋에는 한 가지 문제만 작성
- 제목과 본문 빈 행으로 분리
- 제목 첫 글자는 대문자로, 끝에는 `.` 금지
- 제목은 50자 이내로 할 것
- 가독성 높이기
- 어떻게, 무엇을, 왜에 맞추어 작성
- merge는 squash로

### PR

- 추가사항
- 변경사항
- 특이사항

</details>


  
