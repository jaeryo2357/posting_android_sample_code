# Navigation 3 · 로그인 대기 딥 링크 실습

기존 `app`과 독립된 Android 프로젝트입니다. Android Studio에서 **이 폴더**를 열고
`app` 실행 구성을 실행하세요. 루트의 Kotlin 1.9 프로젝트와 다른 빌드를 사용합니다.

## 환경

- JDK 17, Gradle 9.3.1, AGP 9.1.1
- AGP 내장 Kotlin/Compose compiler/serialization plugin 2.2.10
- Navigation 3 runtime/UI **1.2.0**
- compileSdk 37, targetSdk 36, minSdk 24
- SDK Platform 37.0 (`platforms;android-37.0`), Build Tools 36.0.0

`local.properties`의 `sdk.dir`은 Android Studio에서 자신의 SDK 위치로 설정하세요.

## 화면에서 확인

1. 기본값은 개선 구현입니다. ‘상품 42 링크’를 누릅니다.
2. ‘Activity 재생성’ 또는 기기 회전 후 대기 목적지와 경로가 보존되는지 확인합니다.
3. 로그인 전에 ‘상품 99 링크’를 누릅니다.
4. ‘이전 요청 완료 재현’을 눌러 오래된 42의 완료로 이동하지 않는지 확인합니다.
5. ‘로그인 성공’을 눌러 상품 99로 이동합니다. ‘이전 화면’으로 목록에 돌아갑니다.
6. ‘초기화’ 후 링크를 열고 ‘취소’하면 기존 경로를 유지합니다.
7. ‘실패 구현’을 켜면 로그인 성공 후 목적지를 버리고 목록으로 이동하는 문제를 비교할 수 있습니다.

로그인은 mock입니다. 서버, 계정, 인증 토큰은 사용하지 않습니다.
상태 패널에 표시한 값은 현재 상태이며, 사전에 만든 결과 로그가 아닙니다.

## 외부 Intent와 최초 진입

`shop.example.com`은 예시 도메인입니다. 도메인 검증은 구성하지 않았으므로 브라우저 클릭 대신
아래의 명시적 패키지 Intent로 테스트합니다.

```sh
# 처음 실행: 목록 → 상품 42 합성 경로를 대기 상태에 보관
adb shell am force-stop com.example.nav3login
adb shell am start -W -a android.intent.action.VIEW -d 'https://shop.example.com/products/42' -p com.example.nav3login

# 같은 Activity가 맨 위에 열린 상태: onNewIntent, 기존 경로 유지
adb shell am start -W -a android.intent.action.VIEW -d 'https://shop.example.com/products/99' -p com.example.nav3login
```

인앱 버튼은 실행 중 진입 정책입니다. 최초 Intent는 `savedInstanceState == null`일 때만 접수하여
회전/재생성 때 새 UUID를 발급해 다시 접수하지 않습니다. 사용자 동작으로 받은 새 링크에는 새 UUID를 만듭니다.
같은 URL을 나중에 다시 여는 행위를 전역 중복으로 제거하지 않습니다.

## 구현

- `LinkState.kt`: 실제 경로와 대기 요청 분리, 완료/취소 전이, 오래된 요청 ID 거부.
- `LinkViewModel.kt`: matcher와 `withBackStack`, JSON으로 최소 내비게이션 상태를 SavedStateHandle에 저장.
- `MainActivity.kt`: NavDisplay, mock 로그인, 실패/개선 비교, onCreate/onNewIntent.
- `LinkStateTest`: 경로 유지/교체, 취소, 중복·오래된 완료, 직렬화 왕복.
- `LinkUiTest`: Activity 재생성, 링크 교체 후 로그인, 취소 흐름.

현재 대기 요청의 ID 검사와 pending 제거는 중복 완료를 막습니다. 외부 전송 전체의 exactly-once
보장이나 실제 인증 세션 관리가 아닙니다. 서버 인증 결과를 화면 이동 요청에 연결하는 정책은
인증 구조에 맞춰 별도로 구현해야 합니다.

## 빌드·테스트

```sh
./gradlew :app:assembleDebug :app:testDebugUnitTest
# 실행 중인 에뮬레이터/기기가 연결된 상태
./gradlew :app:connectedDebugAndroidTest
# 디버그 APK 직접 설치
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

GitHub Actions의 ‘Nav3 login sample’ 워크플로는 APK 빌드와 JVM 테스트를 수행하고 APK를 artifact로 제공합니다.
UI 테스트는 로컬 기기/에뮬레이터에서 위 명령으로 실행합니다.

## 글에 넣을 실제 화면 수집

로그인 대기(42), 재생성 후 대기(42), 목적지 교체(99), 로그인 완료(99), 실패 구현(목록)을 각각 캡처합니다.

```sh
adb shell screencap -p /sdcard/nav3-login.png
adb pull /sdcard/nav3-login.png .
adb shell screenrecord --time-limit 60 /sdcard/nav3-login.mp4
adb pull /sdcard/nav3-login.mp4 .
```

캡처 파일은 실제 실행 후에만 결과 자료로 사용합니다.

## 검증 기록 (2026-10-11)

Linux, Temurin JDK 17.0.20.1, Gradle 9.3.1에서 디버그 앱 APK를 빌드했고
상태 전이 JVM 테스트 7개가 통과했습니다. UI 테스트 Kotlin 코드도 컴파일했습니다.
기기/에뮬레이터에서의 UI 테스트 실행과 화면 캡처는 수행하지 않았습니다.
