
## 1. IntervuStella
   
- Open AI를 활용한 자기소개서 기반 면접 질문 생성 및 면접 평가 시뮬레이터

- 시연 영상: https://www.youtube.com/watch?v=HmWrYAVafBA

- 중앙대학교 공학학술제 소프트웨어대학장상 수상

![Group 17 3](https://github.com/JMS1208/IntervuStella/assets/90887876/86b406c7-81e7-4af0-9a70-d8d0363195ba)

## 2. 프로젝트 시기 & 담당

- 2023.01 ~ 2023.06 (약 6개월)
- 안드로이드 & UI/UX 일임 (팀 3명)

## 3. 기존 서비스 비교 및 수요 

- 기존 서비스
기존에 제공되는 면접 시뮬레이터들은 면접 기출 은행 방식의 질문만 제공한다는 문제점이 있었습니다.
그래서 면접자의 자기소개서를 바탕으로 Open AI의 API를 활용하여 생성한 면접 질문지를 제공하여 사용자에게 
특화된 면접 연습 환경을 조성할 수 있도록 하였습니다.

- 수요
취준생들이 면접과 관련하여 현직자들에게 궁금한 것 중 61%는 자기소개서를 보고 예상되는 면접 질문이었습니다. (출처: 코멘토) 
이외에도 자기소개서를 바탕으로 면접에서 어떤 질문이 나올지에 대한 분석 자료를 채용사이트들이 제공하는만큼 수요는 충분하다고 판단하였습니다.

## 4. 주요 요구사항

a. 로그인 및 회원가입

b. 기출 면접 질문 제공

c. 출석 기능

d. 면접 질문 공유하는 사용자 커뮤니티

e. 자기소개서 작성

f. 모의 면접

g. 마이페이지

## 5. 사용 스킬

- Jetpack Compose / TensorFlowLite / Google ML Kit / Android Speech Recognizer / Coroutine / Lifecycle / Room Database / Dagger-Hilt / Retrofit2 / OkHttp3

## 6. 프로젝트를 통해 배운 스킬 

- 안드로이드 Jetpack Compose, CameraX, 안드로이드 생명주기 처리, 실시간 포즈, 표정 분석, 실시간 STT 처리

## 7. 어려웠던 점과 해결 방법

![Group 168](https://github.com/JMS1208/IntervuStella/assets/90887876/116503d1-89e9-4b41-b338-2f28d6655e8c)

Open AI API 비용문제 - 프롬프트를 한글로 입력시 영어보다 토큰이 약 7배 많이 필요하여 비용이 많이 든다는 
문제가 있었습니다. 이 문제를 해결하기 위해, 같은 자기소개서에 대해서는 데이터베이스에 캐싱처리하여 사용자의 
요청에 의해서만 새로운 질문을 생성하게 하였습니다. 그 외에는 기존에 만들어진 면접 질문을 사용할 수 있도록 하고, 
번역과정을 추가하여 프롬프트를 영어로 작성함으로써 토큰 수를 줄인 결과 API 사용료를 낮출 수 있었습니다.

![Group 169](https://github.com/JMS1208/IntervuStella/assets/90887876/df8223b7-36d6-411b-a9fa-6ee8454b8cb0)

이미지 분석이 1초에 120번씩 무의미한 분석 결과를 UI에 갱신하는 문제가 발생하였고, 락 매커니즘으로 3초에 1번만  분석한 이미지 결과를 UI에 갱신하는 작업이 필요했습니다. 하나의 공유자원을 사용한다는 특징과 하나의 스레드만  공유자원을 점유하는 특징이 있었기 때문에, Kotlin의 뮤텍스를 활용하여 이를 해결하였습니다.

모바일 앱 특성상 사용자가 면접 진행 도중 백그라운드로 이동할 수 있는 문제가 발생할 수 있었습니다. Composable
Lifecycle을 사용하여 앱의 생명주기에 따른 상태데이터 처리로 면접 진행 도중 백그라운드로 이동시 면접이 자동으로 
중단되고, 다시 포그라운드로 돌아왔을 때 다시 면접이 진행되게 처리하여 사용자 경험을 향상시켰습니다.

## 8. 주요화면

![Group 170](https://github.com/JMS1208/IntervuStella/assets/90887876/5934cb01-268c-493d-9741-1379fa753ebb)

![Group 171](https://github.com/JMS1208/IntervuStella/assets/90887876/03af0162-f55f-4322-b6d4-a7e9f5ddac05)

![Group 172](https://github.com/JMS1208/IntervuStella/assets/90887876/9c477234-88f1-4454-8a37-851aae0f2f5b)

![Group 173](https://github.com/JMS1208/IntervuStella/assets/90887876/2f52d2dd-044f-4336-86f6-e0afaf51b8f8)

![Group 174](https://github.com/JMS1208/IntervuStella/assets/90887876/a8a4a380-29d1-4817-9aa3-ebc17132d517)

![Group 175](https://github.com/JMS1208/IntervuStella/assets/90887876/94171f60-1d6d-4480-88db-b42f196b7db9)

![Group 176](https://github.com/JMS1208/IntervuStella/assets/90887876/4292acac-61cb-43c0-ae73-dd4cd8df0ebb)

## 9. 도메인 모델링

![다이어그램2 1](https://github.com/JMS1208/IntervuStella/assets/90887876/250b76ed-6151-484b-b1ab-ffc8df76be71)

![다이어그램3 1](https://github.com/JMS1208/IntervuStella/assets/90887876/e788a545-10a5-468e-92ee-6613c6a02640)

![다이어그램4 1](https://github.com/JMS1208/IntervuStella/assets/90887876/519e4cf8-293f-47ca-9b03-026d20795966)

![다이어그램1 1](https://github.com/JMS1208/IntervuStella/assets/90887876/f1a3a850-f990-4eff-b5c4-d0968a9b5de5)
