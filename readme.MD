# 소셜 로그인 예제 만들어 보기

그 소스 코드의 참조글은 다음 링크 2개 입니다.

https://billcorea.tistory.com/308
https://billcorea.tistory.com/313

네이버, 카카오, 구글 로그인을 통해 정보 수집을 할 수 있도록 지원합니다. 
API 연동을 위해서는 google cloud function 으로 로그인 redirect url 호출을 지원하고 있습니다. 

google cloud function 참고글은 다음 링크를 참고 하세요.
https://billcorea.tistory.com/279

이 소스 코드와 연관된 질문은 nari961450@gmail.com 으로 알려 주시면 아는 범주내에서 회신해 드리겠습니다.

## 이 코드를 실행해 보기 위해서는 clone 하고 나서 
local.properties 파일에

KAKAO_KEY=""
RES_KAKAO_KEY=""
NAVER_CLIENT=""
NAVER_SECERT=""
facebook_app_id=""
fb_login_protocol_scheme=""
facebook_client_token=""

이런 정보를 만들어 주어야 정상적으로 build 해 볼 수 있습니다.
