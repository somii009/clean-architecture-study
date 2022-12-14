# 1. 계층형 아키텍처의 문제는 무엇일까?
<br>

## 계층형 아키텍처는 데이터베이스 주도 설계를 유도한다

![image](https://user-images.githubusercontent.com/57824857/197804869-04a15a6e-f8e1-4efe-9547-41e1b53aeb7c.png)

- 위와 같은 계층형 아키텍처에서 웹 계층 → 도메인계층 → 영속성 계층 에 의존하기 때문에 결국 데이터베이스에 의존하게 된다.
- 따라 모든 것이 영속성 계층을 토대로 만들어진다.

- 전통적인 계층형 아키텍처에서는 의존성의 방향에 따라 자연스럽게 **데이터베이스의 구조를 먼저 생각하고, 이를 토대로 도메인 로직을 구현**한다.
- 하지만 비즈니스 관점에서는 무엇보다도 도메인 로직을 먼저 만들어야 한다.

<br>

**⇒ 도메인 로직이 맞다는 것을 확인한 후, 이를 기반으로 영속성 계층과 웹 계층을 만들어야 한다.**

![image](https://user-images.githubusercontent.com/57824857/197805009-cf39b671-1a35-4ba5-988c-f798758fa06a.png)


- ORM 사용 시, ORM에 의해 관리되는 엔티티들은 일반적으로 영속성 계층에 둔다.
- 도메인 계층에서는 이러한 엔티티에 접근, 사용 가능

→ 이렇게 되면 영속성 계층과 도메인 계층 사이에 강한 결합이 생긴다.

따라서 서비스가 영속성 모델을 비즈니스 모델처럼 사용하게 되고, 이로 인해 도메인 로직 뿐만 아니라 즉시로딩/지연로딩, 데이터베이스 트랜잭션, 캐시 플러시(flush) 등 영속성 계층과 관련된 작업들을 해야만 한다.

- 영속성 코드가 사실상 도메인 코드에 녹아들어가서 둘 중 하나만 바꾸는 것이 어려워진다.

<br>

## 지름길을 택하기 쉬워진다

![image](https://user-images.githubusercontent.com/57824857/197805101-c9cdab24-02ef-484d-b4cf-5101d34428b5.png)

- 계층형 아키텍처에서 적용되는 유일한 규칙은, 특정한 계층에서는 같은 계층에 있는 컴포넌트나 아래에 있는 계층에만 접근 가능하다는 것이다.
    - 따라서 만약 상위 계층에 위치한 컴포넌트에 접근해야한다면 간단히 컴포넌트를 계층 아래로 내리면 된다.
    - 이것을 계속 수행하다보면 위의 그림과 같아진다.
    - 영속성 계층은 컴포넌트를 아래 계층으로 내릴수록 비대해진다.

<br>

## 테스트하기 어려워진다

- 계층형 아키텍처를 사용할 때 일반적으로 나타나는 변화의 형태는 계층을 건너뛰는 것이다.

![image](https://user-images.githubusercontent.com/57824857/197805168-c9045f62-1a2c-4a09-b4dd-6866952916b9.png)

- 위의 그림과 같이 도메인 계층을 건너뛰는 것은 도메인 로직을 코드 여기저기에 흩어지게 만든다.

이렇게 되면 발생하는 문제점들

- 단 하나의 필드를 조작하는 것에 불과하더라도 도메인 로직을 웹 계층에 구현하게 된다.
    - 더 많은 로직을 웹 계층에 추가하면서 핵심 도메인 로직들이 퍼져나갈 확률이 높다.
- 웹 계층 테스트에서 도메인 계층 뿐만 아니라 영속성 계층도 모킹(mocking) 해야 한다.
    - 이렇게 되면 단위테스트의 복잡도가 올라가고, 테스트를 전혀 작성하지 않게 될 확률이 높아진다.
    - 시간이 흘러 웹 컴포넌트의 규모가 커지면 다양한 영속성 컴포넌트에 의존성이 많이 쌓이면서 테스트의 복잡도를 높인다.

<br>

## 유스케이스를 숨긴다

실제 개발자들은 새로운 코드를 짜는데 시간에 쓰기보다는 기존 코드를 바꾸는 데 더 많은 시간을 쓴다.

따라서 기능을 추가하거나 변경할 적절한 위치를 찾는 일이 빈번하기 때문에, 아키텍처는 코드를 빠르게 탐색하는데 도움이 돼야 한다.

- 계층형 아키텍처에서는 도메인 로직이 여러 계층에 흩어지기 쉽다.
- 계층형 아키텍처는 도메인 서비스의 ‘너비’에 관한 규칙을 강제하지 않는다.



- 따라서 시간이 흐르면 위 그림과 같이 여러 개의 유스케이스를 담당하는 넓은 서비스가 만들어지기도 한다.
- 이러한 넓은 서비스는 영속성 계층에 많은 의존성을 갖게 되고, 다시 웹 레이어의 많은 컴포넌트가 이 서비스에 의존하게 된다.
    - 이런 경우 서비스를 테스트하기 어려워지고, 작업해야 할 유스케이스를 책임지는 서비스를 찾기도 어려워진다.

⇒ 좁은 도메인 서비스가 유스케이스 하나씩만 담당하도록 하자!

ex) UserService에서 사용자 등록 유스케이스를 찾는 대신 RegisterUserService를 바로 열어서 작업하도록!

<br>

## 동시 작업이 어려워진다

실제 프로젝트에서는 여러 작업을 동시에 해야하고, 아키텍처가 동시 작업을 지원해야한다.

- 계층형 아키텍처에서는 모든 것이 영속성 계층 위에 만들어지기 때문에 영속성 계층 → 도메인 계층 → 웹 계층 순으로 개발해야한다. 따라서 특정 기능은 동시에 한 명의 개발자맞 작업할 수 있다.
- 코드에 넓은 서비스가 있다면 서로 다른 기능을 동시에 작업하기가 더욱 어렵다.
    - 서로 다른 유스케이스에 대한 작업을 하게 되면 같은 서비스를 동시에 편집하는 상황이 발생하고, 이는 병합 충돌과 이전 코드로 되돌려야하는 문제를 야기하기 때문이다.

<br>

## 유지보수를 가능한 소프트웨어를 만드는 데 어떻게 도움이 될까?

- 올바르게 구축하고 몇 가지 추가적인 규칙들을 적용하면 계층형 아키텍처는 유지보수하기 매우 쉬워지며 코드를 쉽게 변경하거나 추가할 수 있게 된다.
- 지름길을 택하지 않고 유지보수하기에 더 쉬운 솔루션을 만들자!


---
[이미지 참고](https://m.blog.naver.com/fbfbf1/222762059059)

책 : [만들면서 배우는 클린 아키텍처](http://www.yes24.com/Product/Goods/105138479)
