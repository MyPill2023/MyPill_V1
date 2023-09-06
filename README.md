# 💊 MyPill_V1

요구사항 개발기간 :`2023.06.19` ~ `2023.07.14`

리팩토링 개발기간  : `2023.07.24` ~ `2023.09.06`

> 영양제 이커머스 플랫폼</br>
MyPill에서는 설문 기반 맞춤형 영양소를 추천하고, 복약 관리 기능을 더한, 영양제 이커머스 플랫폼 입니다!

- [🔗 MyPill](https://www.mypill.shop/)
- [🔗 시연영상](https://youtu.be/VYYzGUSq1Hk)

## 사용환경(Version)
- java : 17
- Spring boot : 3.0.6
- build : Gradle
- database : MariaDB
- 개발환경 : IntelliJ


## 📌 기술 스택
<details>
<summary>기술 스택</summary>
  
### FrontEnd

- HTML, CSS, JS
- 타임리프
- 제이쿼리
- 테일윈드
- 데이지UI

### BackEnd

- SpringBoot 3.xx
- java 17
- mariaDB
- Spring Oauth 2.0
- 스프링 시큐리티
- 공공데이터 API
- 토스페이먼츠 결제 모듈

### Infra

- NGINX
- 젠킨스
- 도커
- 레디스
- 네이버 클라우드 플랫폼
- 네이버 오브젝트 스토리지
- 네이버 CDN+

</details>

![020](https://github.com/MyPill2023/MyPill/assets/64017307/55ae15a1-f06d-4920-867f-1f5cf3d5b4fb)

## ⚙️ 시스템 아키텍처

<details>
<summary>시스템 아키텍처</summary>

- MariaDB
- 네이버 클라우드 플랫폼
- 네이버 오브젝트 스토리지
- 네이버 CDN+
- Jenkins
- Redis
- 토스페이먼츠 결제 모듈
- Spring Oauth 2.0
    - 네이버
    - 카카오
- API
    - 위치 정보
        - [카카오 Map API](https://apis.map.kakao.com/)
    - 공공데이터
        - [건강기능식품 판매업](https://www.foodsafetykorea.go.kr/api/openApiInfo.do?menu_grp=MENU_GRP31&menu_no=656&show_cnt=10&start_idx=1&svc_no=I1290&svc_type_cd=API_TYPE06)    
        - [공정거래위원회_통신판매사업자 등록현황 제공 조회 서비스](https://www.data.go.kr/data/15112404/openapi.do)
            
- Swagger
- DDD(Domain-Driven-Design)

</details>

![ad5673640218fbc8](https://github.com/MyPill2023/MyPill/assets/64017307/5f4d29f8-89be-4518-9d75-6af04c8b3211)

## 📄 ERD
![Untitled](https://github.com/MyPill2023/.github/assets/64017307/c91eb2d6-b87b-439c-af2d-476cc010c1ee)


## 📂 API 명세서

- [🔗 API명세서](https://www.mypill.shop/swagger-ui/index.html#/)


