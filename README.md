# 대학별 일정 서포트 앱 Campus_Linker
### 앱 개발 배경
최근 COVID-19로 인한 학생 간 교류의 기회가 감소하고, 이에 따라 학생들의 교류를 지원할 수 있는 앱에 대한 수요가 높아지고 있다.  
본 논문에서는 기존 앱 사례들을 분석하여, 필수 기능들을 포함하는 일정 모집 서포트 앱 Campus-Linker를 개발한다.  
기존의 사례들의 제한점을 극복하기 위해 Rest API, Socket, FCM 등을 이용한 서버와 Android Studio를 이용하여, 앱을 이용하여 대학별 사용자의 일정 모집에 도움을 주는 메신저, 게시판 통합 앱을 구현하고자 하였다.

+ 본 프로젝트에서 작성자는 안드로이드 UI 구현과 프론트 엔드 구현을 담당하였으며 본 코드는 코틀린으로 작성되었다.
***

### 핵심 구현기능
+ 로그인, 회원가입 기능(JWT토큰 방식)
+ 이메일 및 학교 위치 기반 본인 인증
+ 게시판 기능
+ 스케쥴 매칭 기능
+ 웹소켓 기반 채팅 기능
+ 알림
***

### 사용된 툴 및 기술
+ Android studio
+ Koitlin
+ REST API
+ Web Socket
+ FCM(Firebase Cloud Messaging)
***

### 여담
이 프로젝트는 아무래도 졸업작품인지라 지금까지 해봤던 다른 프로젝트들에 비해 정성이 많이 들어갔다.  
한번도 사용해본적 없는 코틀린과 처음 구현해보는 기능들로 험한 꼴도 많이 당하고, 그로 인해 생각했던 것보다 시간이 많이 소요되기도 했다.  
이 글을 읽고 왜 미련하게 졸업작품에 처음 사용해보는 언어를 썼냐고 하는 사람들도 있을 것이다.  
그러나 나는 실패를 무서워하는 것이 새로운 것에 도전하지 못할 이유는 안된다고 생각한다.  
그렇게 도전했고, 결국, 모든 기능들의 구현과 UI의 세부정리까지 성공적으로 마무리 하는 것에 성공했다.  
비록, 코드의 정리와 병행하며 개발하기엔 시간이 부족하다 판단해 코드의 가독성은 떨어져 스파게티 코드처럼 되어버렸지만 이정도면 처음 써본 것 치고는 괜찮았다고 생각한다.  
여러분도 처음 해보는 것에 망설이지말고 도전하고 성공하여 원하는 것을 쟁취했으면 좋겠다.
