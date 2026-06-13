# 주문 시스템을 통한 분산 트랜잭션 실습 및 비교 프로젝트

이 프로젝트는 MSA(Microservice Architecture) 환경에서 다수의 마이크로서비스 간의 데이터 정합성을 보장하기 위한 다양한 **분산 트랜잭션 패턴(Monolithic, TCC, Saga Orchestration, Saga Choreography)** 을 구현하고 테스트해보는 실습용 리포지토리입니다.

---

## 📌 주요 요구사항 정의
- **주문 관리**: 주문 데이터 저장 및 진행 상태 관리
- **재고 관리**: 상품 주문에 따른 재고 차감 및 롤백(보상 트랜잭션)
- **포인트 관리**: 사용자의 포인트 차감 및 롤백(보상 트랜잭션)
- **데이터 정합성**: 분산된 서비스의 데이터 상태가 최종적으로 일치해야 함
- **멱등성 보장**: 네트워크 재시도나 실패 복구 시 보상 트랜잭션이 여러 번 호출되어도 중복 처리되지 않아야 함 (Idempotent Cancel)

---

## 🛠 기술 스택
- **언어 및 프레임워크**: Java 17, Spring Boot 3.x / 4.x
- **데이터베이스**: MySQL (데이터 관리), Redis (분산 락 관리)
- **이벤트/메시징 브로커**: Apache Kafka (Saga Choreography 이벤트 전파)
- **빌드 도구**: Gradle (각 마이크로서비스 개별 Gradle 프로젝트 구성)

---

## 📐 분산 트랜잭션 구현 패턴 소개

본 프로젝트는 총 4가지 아키텍처 방식을 제공하여 비교 실험이 가능합니다.

### 1. Monolithic (`monolithic`)
- 단일 애플리케이션 및 단일 RDBMS(`commerce_example`) 환경.
- 하나의 스프링 트랜잭션(`@Transactional`) 범위 내에서 주문 생성, 재고 차감, 포인트 사용이 원자적(Atomic)으로 처리됩니다.

### 2. TCC (Try-Confirm-Cancel) (`tcc-order`, `tcc-product`, `tcc-point`)
- API 기반의 2단계 확정 분산 트랜잭션 패턴입니다.
- **Try**: 각 서비스에 자원 예약(가선점)을 요청합니다. (예: 재고 가예약, 포인트 가선점)
- **Confirm**: 모든 서비스의 예약이 성공하면 예약을 확정(실제 차감)합니다.
- **Cancel**: 하나라도 실패하면 예약된 자원을 해제(취소)합니다.

### 3. Saga Orchestration (`saga-orchestration-order`, `saga-orchestration-product`, `saga-orchestration-point`)
- 중앙 제어자(Orchestrator)가 전체 비즈니스 워크플로우를 통제하는 방식입니다.
- Order 서비스 내의 Coordinator가 동기식 HTTP API 호출을 순차적으로 수행하며 다음 단계의 트랜잭션을 실행하고, 실패가 감지되면 역순으로 보상 트랜잭션(Cancel API)을 전송합니다.

### 4. Saga Choreography (`saga-choreography-order`, `saga-choreography-product`, `saga-choreography-point`)
- 중앙 제어자 없이, 각 서비스가 메시지 브로커(Kafka)의 이벤트를 발행/구독(Pub/Sub)하며 독립적으로 상호작용하는 이벤트 기반 비동기 방식입니다.
- **정상 흐름 (Success Flow)**:
  `Order Placed` (주문 생성 및 결제 진행) $\rightarrow$ `Quantity Decreased` (재고 차감) $\rightarrow$ `Point Used` (포인트 사용) $\rightarrow$ `Order Completed` (주문 성공)
- **실패 및 보상 트랜잭션 흐름**:
  결제 중 포인트 잔액이 부족하거나 시스템 예외가 발생하면 `Point Use Fail` 이벤트가 발행되며, 이를 구독하는 Product 서비스가 이미 차감했던 재고를 원상 복구(보상 트랜잭션)한 후 `Quantity Decreased Fail` 이벤트를 발행하여 최종적으로 Order 서비스를 실패(`FAILED`) 상태로 바꿉니다.

---

## 🚀 인프라 설정 및 실행 방법

### 1. 인프라 실행 (Docker Compose)
프로젝트 루트 디렉토리에서 Docker Compose를 사용하여 MySQL, Redis, Kafka를 기동합니다.
```bash
docker-compose up -d
```

### 2. MySQL 데이터베이스 생성
MySQL에 접속하여 각 서비스가 사용할 데이터베이스 스키마를 미리 생성해야 합니다. (스프링 부트 기동 시 자동으로 테이블이 생성됩니다.)
```sql
CREATE DATABASE commerce_example;
CREATE DATABASE order_example;
CREATE DATABASE product_example;
CREATE DATABASE point_example;
```

### 3. 마이크로서비스 포트 정보
- **Order Service**: `8080` (모든 패턴의 주문 엔트리포인트)
- **Point Service**: `8081`
- **Product Service**: `8082`

---

## 🧪 시나리오 테스트 방법 (HTTP Client)

`http/` 디렉토리에 각 트랜잭션 패턴을 검증하기 위한 `.http` 파일이 제공됩니다. IntelliJ IDEA의 HTTP Client 등을 통해 편리하게 테스트할 수 있습니다.

### 💡 Saga Choreography 시나리오 요약
1. **[성공 흐름] 정상 주문 및 결제 완료 (Success Flow)**: 총 주문 금액 $\le$ 10,000원(초기 잔액), 수량 $\le$ 100개(초기 재고). 최종 상태 `COMPLETED`.
2. **[실패 흐름 - 재고 부족] 상품 재고 수량 초과 주문**: 150개 대량 주문 시도. Product 서비스의 예외 차감 실패 후 보상 트랜잭션을 거쳐 최종 상태 `FAILED`.
3. **[실패 흐름 - 포인트 부족] 포인트 잔액 초과 주문**: 총합 11,000원 상당 주문. 재고는 선차감되나 포인트 부족 예외로 인해 상품 보상 트랜잭션이 수행되어 재고가 원상복구되고 최종 상태 `FAILED`.
4. **[강제 예외 테스트 - Point 사용 실패 / 보상 성공]**:
   * Product 1을 **`99개`** 주문하면 결제 금액이 9,900원이 되어, `PointService.use()`에서 의도적으로 `RuntimeException`이 발생합니다.
   * 상품 서비스의 보상 트랜잭션(재고 취소)이 멱등하게 작동하여 최종 주문 상태가 `FAILED`로 안전하게 롤백되는지 검증합니다.
5. **[강제 예외 테스트 - Product 취소 실패 / 보상 실패]**:
   * Product 1을 **`88개`** 주문하면 결제 금액이 8,800원이 되어 `PointService.use()` 실패 후 보상 트랜잭션(`ProductService.cancel`)이 시작됩니다.
   * 상품 보상 트랜잭션 도중에도 강제 예외가 터져 **보상 트랜잭션 자체가 실패했을 때** Kafka의 재시도 로그와 주문 상태가 `REQUESTED`에 정체되는 현상을 직접 모니터링할 수 있습니다.