# 3. 코드 구성하기

## 계층으로 구성하기

```markdown
buckapl
|--- domain
|    |----- Account
|    |----- Activity
|    |----- AccountRepository
|    |----- AccountService
|--- persistence
|    |----- AccountRepositoryImpl
|--- web
|    |----- AccountController
```

- 웹 계층, 도메인 계층, 영속성 계층 각각에 대해 전용 패키지인 web, domain, persistence를 뒀다.
- 의존성 역전 원칙을 적용해 의존성이 domain 패키지에 있는 도메인 코드만을 향하도록 한다.
    - domain 패키지에 AccountRepository 인터페이스를 추가
    - persistence 패키지에 AccountRepositoryImpl 구현체를 두어서 의존성을 역전시킴

위 패키지 구조는 최적의 구조가 아니다.

- 애플리케이션의 기능 조각이나 특성을 구분짓는 패키지 경계가 없다.
- 애플리케이션이 어떤 유스케이스들을 제공하는지 파악할 수 없다
- 패키지 구조를 통해서는 우리가 목표로 하는 아키텍처를 파악할 수 없다.

## 기능으로 구성하기

```markdown
buckapl
|--- account
|    |----- Account
|    |----- Activity
|    |----- AccountRepository
|    |----- SendMoneyService
|    |----- AccountRepositoryImpl
|    |----- AccountController
```

- 위 패키지는 계좌와 관련된 모든 코드를 최상위의 account 패키지에 넣고, 계층 패키지들을 없앴다.
- 패키지 외부에서 접근하면 안되는 클래스들에 대해 package-private 접근 수준을 이용해 패키지 간의 경계를 강화할 수 있다.
    - 패키지 경계를 package-private 접근 수준과 결합해 각 기능 사이의 불필요한 의존성을 방지
- AccountService의 책임을 좁히기 위해 SendMoneyService로 클래스명 변경

단점

- 계층에 의한 패키지 방식보다 아키텍처의 가시성을 떨어뜨린다.
- 어댑터를 나타내는 패키지명이 없고, 인커밍 포트, 아웃고잉 포트를 확인할 수 없다.
- 도메인 코드와 영속성 코드 간의 의존성을 역전시켰음에도 package-private 접근 수준을 이용해 도메인 코드가 실수로 영속성 코드에 의존하는 것을 막을 수 없다.

## 아키텍처적으로 표현력 있는 패키지 구조

```markdown
buckapl
|--- account
|    |----- adapter
|    |      |----- in
|    |      |      |---- web
|    |      |      |     |---- AccountController
|    |      |----- out
|    |      |      |---- persistence
|    |      |      |     |---- AccountPersistenceAdapter
|    |      |      |     |---- SpringDataAccountRepository
|    |---- domain
|    |     |----- Account
|    |     |----- Activity
|    |---- application
|    |     |----- SendMoneyService
|    |     |----- port
|    |     |     |---- in
|    |     |     |     |---- SendMoneyUseCase
|    |     |     |---- out
|    |     |     |     |---- LoadAccountPort
|    |     |     |     |---- UpdateAccountStatePort
```

헥사고날 아키텍처에서 구조적으로 핵심적인 요소는 아래와 같다.

- 엔티티
- 유스케이스
- 인커밍/아웃고잉 포트
- 인커밍/아웃고잉 어댑터

구조의 각 요소들은 패키지 하나씩에 직접 매핑된다.

- 최상위에는 Account와 관련된 유스케이스를 구현한 모듈임을 나타내는 account 패키지
- 그 다음 도메인 모델이 속한 domian 패키지
- 도메인 모델을 둘러싼 서비스계층인 application 패키지
    - SendMoneyService : 인커밍 포트 인터페이스인 SendMoneyUseCase 구현, LoadAccountPort와 UpdateAccountStatePort 사용
- adapter 패키지는 애플리케이션 계층의 인커밍 어댑터와 아웃고잉 어댑터를 포함

- 어댑터 패키지에 들어있는 모든 클래스들은 application 패키지 내에 있는 포트 인터페이스를 통하지 않고는 바깥에서 호출되지 않기 때문에 package-private 접근 수준으로 둬도 된다.
- 의도적으로 어댑터에서 접근 가능해야 하는 포트들은 public이어야 한다.
- 이 패키지 구조는 DDD 개념에 직접적으로 대응시킬 수 있다!

## 의존성 주입의 역할

- 클린 아키텍처의 가장 본질적인 요건은 애플리케이션 계층이 인커밍/아웃고잉 어댑터에 의존성을 갖지 않는 것이다.
- 모든 계층에 의존성을 가진 중립적인 컴포넌트를 하나 도입
    - 이 컴포넌트는 아키텍처를 구성하는 대부분의 클래스를 초기화하는 역할을 한다.
