# Spring WebFlux 中的 @HttpExchange 注解

## 课程目标与概述
本次课程将介绍 Spring WebFlux 中的 `@HttpExchange` 注解，这是一种用于构建声明式 HTTP 客户端的新特性。通过使用 `@HttpExchange`，开发者可以更轻松地定义和调用 HTTP 服务接口，从而减少样板代码并提高开发效率。

### 课程目标
- 理解 `@HttpExchange` 的基本概念和用途
- 掌握如何使用 `@HttpExchange` 创建声明式 HTTP 客户端
- 学习如何配置和使用 `HttpServiceProxyFactory` 来生成客户端代理
- 了解 `@HttpExchange` 与 `Feign` 的区别及其优势
- 实践示例代码以加深理解
- 学习如何在 `@HttpExchange` 方法中处理不同类型的请求参数和响应类型。

## @HttpExchange 注解简介
`@HttpExchange` 是 Spring Framework 6 提供的一个声明式 HTTP 客户端注解，旨在简化 HTTP 服务的调用。它允许开发者通过 Java 接口定义 HTTP 请求，并由框架自动生成实现代码。

### 主要特性
- **声明式编程**：通过注解定义 HTTP 请求，减少样板代码
- **轻量级**：相比 `Feign`，`@HttpExchange` 更加轻巧，依赖更少
- **内置支持**：Spring Boot 3 提供了对 `@HttpExchange` 的内置支持，无需额外引入第三方库

## 使用 @HttpExchange 创建声明式 HTTP 客户端

### 1. 添加依赖

要使用 `@HttpExchange`，需要在项目中添加 `spring-boot-starter-webflux` 依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

---

### 2. 配置 WebClient 和 HttpServiceProxyFactory

为了使用 `@HttpExchange`，需要配置 `WebClient` 和 `HttpServiceProxyFactory`：

```java

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class BaseWebConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://jsonplaceholder.typicode.com")
                .build();
    }

    @Bean
    public BaseUserClient userClient(WebClient webClient) {
        HttpServiceProxyFactory httpServiceProxyFactory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient))
                .build();
        return httpServiceProxyFactory.createClient(BaseUserClient.class);
    }

}
```

---

### 3. 定义 HTTP 服务接口

创建一个 Java 接口，并使用 `@HttpExchange` 注解来定义 HTTP 请求：

```java
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import reactor.core.publisher.Mono;

@HttpExchange(url = "/users", accept = "application/json", contentType = "application/json")
public interface BaseUserClient {

    @GetExchange("/")
    Mono<List<JSONObject>> getAllUsers();

}

```

---


### 4. 调用 HTTP 服务

注入 `BaseUserClient` 并调用定义的方法：

```java
import cn.smallyoung.webflux.base.BaseUserService;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class BaseUserServiceTest {

    @Resource
    private BaseUserService baseUserService;

    @Test
    public void testGetAllUsers() {
        List<JSONObject> users =  baseUserService.getAllUsers().block();
        System.out.println(users);
    }
}
```

## `@HttpExchange` 相关注解和参数说明

`@HttpExchange` 是一个元注解，它本身可以用于定义请求的通用属性（如 URL、内容类型和期望的接受类型），并且它拥有一系列派生注解，用于更具体地指定 HTTP 请求方法。

### 1. `@HttpExchange` 注解详解

**用途：** 用于在类或方法级别定义声明式 HTTP 客户端接口。当用于类时，它为接口中的所有方法提供一个基础 URL 和默认行为；当用于方法时，它定义特定方法的 HTTP 交换细节。

**位置：** 接口或接口中的方法上。

**注解参数：**

- **`value()` / `url()`**: (别名)
  * **类型：** `String`
  * **默认值：** `""` (空字符串)
  * **说明：** 指定请求的 URL。如果用于类级别，它定义了所有方法的基础 URL；如果用于方法级别，它定义了相对于类级别 URL 的路径。支持 URI 模板变量（例如 `/users/{id}`）。
- **`method()`**:
  * **类型：** `String`
  * **默认值：** `""` (空字符串)
  * **说明：** 指定 HTTP 请求方法（例如 "GET", "POST", "PUT", "DELETE"）。
  * **注意：** 通常不直接使用此参数，而是使用它的派生注解，如 `@GetExchange`, `@PostExchange` 等，它们已内置了 `method` 属性。
- **`contentType()`**:
  * **类型：** `String`
  * **默认值：** `""` (空字符串)
  * **说明：** 指定请求的 `Content-Type` 头部。例如 `"application/json"` 或 `"multipart/form-data"`。
- **`accept()`**:
  * **类型：** `String[]`
  * **默认值：** `{}` (空数组)
  * **说明：** 指定请求的 `Accept` 头部，表示客户端希望接收的响应媒体类型。例如 `{"application/json", "text/plain"}`。

--- 

**示例：**

```java
// 类级别：定义基础URL和默认接受JSON
@HttpExchange(url = "/api", accept = "application/json", contentType = "application/json")
public interface MyServiceClient {

    // 方法级别：继承类级别设置，并定义具体路径
    @GetExchange("/users") // 等同于 @HttpExchange(url = "/api/users", method = "GET")
    Mono<List<User>> getAllUsers();

    // 方法级别：覆盖类级别设置，使用不同的contentType
    @PostExchange(url = "/files", contentType = "multipart/form-data")
    Mono<String> uploadFile(@RequestPart("file") Resource file);
}
```

---

### 2. 请求方式派生注解

这些注解是 `@HttpExchange` 的特化版本，用于更简洁地表达常见的 HTTP 请求方法。它们都继承了 `@HttpExchange` 的所有参数。

- **`@GetExchange`**:
  * **用途：** 标识用于 HTTP GET 请求的方法。
  * **继承参数：** `value()` / `url()`, `contentType()`, `accept()`, `method()` (但 `method()` 默认固定为 "GET")
- **`@PostExchange`**:
  * **用途：** 标识用于 HTTP POST 请求的方法。
  * **继承参数：** 同上 (但 `method()` 默认固定为 "POST")
- **`@PutExchange`**:
  * **用途：** 标识用于 HTTP PUT 请求的方法。
  * **继承参数：** 同上 (但 `method()` 默认固定为 "PUT")
- **`@DeleteExchange`**:
  * **用途：** 标识用于 HTTP DELETE 请求的方法。
  * **继承参数：** 同上 (但 `method()` 默认固定为 "DELETE")
- **`@PatchExchange`**:
  * **用途：** 标识用于 HTTP PATCH 请求的方法。
  * **继承参数：** 同上 (但 `method()` 默认固定为 "PATCH")

---

**示例：**

```java
public interface ResourceClient {
    @GetExchange("/resources/{id}") // GET /resources/{id}
    Mono<Resource> getResource(@PathVariable String id);

    @PostExchange("/resources") // POST /resources
    Mono<Resource> createResource(@RequestBody Resource newResource);

    @PutExchange("/resources/{id}") // PUT /resources/{id}
    Mono<Resource> updateResource(@PathVariable String id, @RequestBody Resource updatedResource);

    @DeleteExchange("/resources/{id}") // DELETE /resources/{id}
    Mono<Void> deleteResource(@PathVariable String id);

    @PatchExchange("/resources/{id}") // PATCH /resources/{id}
    Mono<Resource> patchResource(@PathVariable String id, @RequestBody Map<String, Object> updates);
}
```

---

### 3. 请求参数注解详解

这些注解用于将方法参数绑定到请求的不同部分。

- **`@PathVariable`**:
  * **用途：** 将方法参数绑定到 URI 模板变量上。
  * **位置：** 方法参数上。
  * **注解参数：**
    * **`value()`**: (别名)
      * **类型：** `String`
      * **默认值：** 根据参数名推断
      * **说明：** 指定 URI 模板变量的名称。如果为空，则默认为方法参数的名称。
    * **`name()`**: (别名) 同 `value()`。
    * **`required()`**:
      * **类型：** `boolean`
      * **默认值：** `true`
      * **说明：** 表示路径变量是否必须存在。通常路径变量都是必须的，如果设置为 `false`，则意味着该路径变量是可选的，但这不常见于 RESTful API 的路径设计。

- **`@RequestParam`**:
  * **用途：** 将方法参数绑定到请求的查询字符串参数（URL 中的 `?key=value&key2=value2` 部分）。
  * **位置：** 方法参数上。
  * **注解参数：**
    * **`value()`**: (别名)
      * **类型：** `String`
      * **默认值：** 根据参数名推断
      * **说明：** 指定查询参数的名称。如果为空，则默认为方法参数的名称。
    * **`name()`**: (别名) 同 `value()`。
    * **`required()`**:
      * **类型：** `boolean`
      * **默认值：** `true`
      * **说明：** 表示该查询参数是否必须存在。如果 `false`，则该参数可以不提供，此时方法参数会是 `null` 或默认值。
    * **`defaultValue()`**:
      * **类型：** `String`
      * **默认值：** `\n\t\t\n\t\t\n\uE000\uE001\uE002\n\t\t\t\t\n` (Spring内部标记)
      * **说明：** 如果 `required` 为 `false` 且请求中未提供该参数，则使用此默认值。

- **`@RequestBody`**:
  * **用途：** 将请求体的内容绑定到方法参数上。通常用于发送 JSON、XML 等复杂数据结构。
  * **位置：** 方法参数上。
  * **注解参数：**
    * **`required()`**:
      * **类型：** `boolean`
      * **默认值：** `true`
      * **说明：** 表示请求体是否必须存在。如果为 `false`，则请求体可以为空，此时方法参数会是 `null`。

- **`@RequestHeader`**:
  * **用途：** 将请求的 HTTP 头部信息绑定到方法参数上。
  * **位置：** 方法参数上。
  * **注解参数：**
    * **`value()`**: (别名)
      * **类型：** `String`
      * **默认值：** 根据参数名推断
      * **说明：** 指定 HTTP 头部的名称。
    * **`name()`**: (别名) 同 `value()`。
    * **`required()`**:
      * **类型：** `boolean`
      * **默认值：** `true`
      * **说明：** 表示该头部是否必须存在。
    * **`defaultValue()`**:
      * **类型：** `String`
      * **默认值：** `\n\t\t\n\t\t\n\ue000\ue001\ue002\n\t\t\t\t\n` (Spring内部标记)
      * **说明：** 如果 `required` 为 `false` 且请求中未提供该头部，则使用此默认值。

- **`@RequestPart`**:
  * **用途：** 用于处理 `multipart/form-data` 请求中的单个部分，通常用于文件上传或包含文件和其它表单数据的请求。方法参数类型可以是 `FilePart` (推荐用于 WebFlux)、`Resource` 或普通的 Java bean (当部分为JSON等时)。
  * **位置：** 方法参数上。
  * **注解参数：**
    * **`value()`**: (别名)
      * **类型：** `String`
      * **默认值：** 根据参数名推断
      * **说明：** 指定 multipart 部分的名称。
    * **`name()`**: (别名) 同 `value()`。
    * **`required()`**:
      * **类型：** `boolean`
      * **默认值：** `true`
      * **说明：** 表示该部分是否必须存在。
    * **`defaultValue()`**:
      * **类型：** `String`
      * **默认值：** `\n\t\t\n\t\t\n\uE000\uE001\uE002\n\t\t\t\t\n` (Spring内部标记)
      * **说明：** 如果 `required` 为 `false` 且请求中未提供该部分，则使用此默认值。

---

**请求参数注解的应用示例：**

```java
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// 假设我们有一个OrderService，需要支持多种请求和响应
@HttpExchange(url = "/orders", accept = "application/json")
public interface OrderServiceClient {

    // 1. 获取特定订单详情 (路径参数)
    @GetExchange("/{orderId}")
    Mono<Order> getOrderDetails(@PathVariable("orderId") String id);

    // 2. 搜索订单 (查询参数, 接受/返回JSON)
    @GetExchange("/search")
    Mono<List<Order>> searchOrders(
        @RequestParam("customerName") String name,
        @RequestParam(value = "status", required = false, defaultValue = "PENDING") String status,
        @RequestParam(required = false) Integer limit
    );

    // 3. 创建订单 (请求体参数)
    @PostExchange(contentType = "application/json")
    Mono<ResponseEntity<Order>> createOrder(@RequestBody Order creationRequest);

    // 4. 更新订单状态 (路径参数 + 请求头)
    @PutExchange("/{orderId}/status")
    Mono<Void> updateOrderStatus(
        @PathVariable String orderId,
        @RequestParam("newStatus") String newStatus,
        @RequestHeader("X-Operator-Id") String operatorId
    );

    // 5. 上传订单附件 (文件参数 + 额外数据)
    @PostExchange(url = "/{orderId}/attachments", contentType = "multipart/form-data")
    Mono<JSONObject> uploadAttachment(
        @PathVariable String orderId,
        @RequestPart("file") FilePart attachmentFile, // 推荐使用 FilePart for WebFlux
        @RequestPart("description") String fileDescription
    );

    // 6. 获取订单报表 (返回文件流)
    @GetExchange(url = "/reports/{reportName}", accept = "application/pdf")
    Mono<Resource> getOrderReport(@PathVariable String reportName);

    // 7. 处理简单响应 (仅返回状态码或成功/失败)
    @DeleteExchange("/{orderId}")
    Mono<Void> deleteOrder(@PathVariable String orderId);
}
```

---

| 功能点/注解 | Spring MVC (传统 Web MVC) | Spring WebFlux (响应式 Web) | 差异与说明 |
| :---------- | :------------------------- | :-------------------------- | :---------- |
| **核心编程模型** | Servlet API，阻塞式I/O | Project Reactor，非阻塞式I/O | MVC 基于请求-响应模型，一次处理一个请求；WebFlux 基于事件流，可以处理多个并发请求，更适合微服务和高并发场景。 |
| **控制器注解** | `@Controller` | `@Controller` | 都能用于标记类为控制器。 |
|             | `@RestController` | `@RestController` | 两者都等同于 `@Controller` + `@ResponseBody`，表示返回 JSON/XML 等数据而不是视图名。 |
| **请求映射注解** | `@RequestMapping` | `@RequestMapping` | 都可以用于方法和类，映射请求路径。 |
|             | `@GetMapping` | `@GetMapping` | 对应 HTTP GET 请求。 |
|             | `@PostMapping` | `@PostMapping` | 对应 HTTP POST 请求。 |
|             | `@PutMapping` | `@PutMapping` | 对应 HTTP PUT 请求。 |
|             | `@DeleteMapping` | `@DeleteMapping` | 对应 HTTP DELETE 请求。 |
|             | `@PatchMapping` | `@PatchMapping` | 对应 HTTP PATCH 请求。 |
| **请求参数绑定** | `@PathVariable` | `@PathVariable` | 绑定 URL 路径变量。 |
|             | `@RequestParam` | `@RequestParam` | 绑定请求参数（Query Parameters）。 |
|             | `@RequestBody` | `@RequestBody` | 绑定请求体到 Java 对象（通常是 JSON/XML）。 |
|             | `@ModelAttribute` | `@ModelAttribute` | 绑定请求参数到 Java 对象，通常用于表单提交，在 WebFlux 中使用较少。 |
|             | `@RequestHeader` | `@RequestHeader` | 绑定请求头。 |
|             | `@CookieValue` | `@CookieValue` | 绑定 Cookie。 |
|             | `@RequestPart` | `@RequestPart` | 绑定 `multipart/form-data` 请求中的文件或表单部分。在WebFlux中，文件通常绑定到 `FilePart`。 |
| **响应体处理** | `@ResponseBody` | `@ResponseBody` | 标记方法返回值直接作为响应体。 |
| **返回值类型** | `void`, `String` (视图名), `ModelAndView`, POJO (默认通过Converter转为JSON/XML), `ResponseEntity<?>` | `Mono<?>`, `Flux<?>`, POJO (默认通过Converter转为JSON/XML), `ResponseEntity<?>` | WebFlux 主要使用 `Mono` (0或1个元素) 和 `Flux` (0到N个元素) 来表示异步操作的结果流。 |
| **异常处理** | `@ExceptionHandler` (方法级别) | `@ExceptionHandler` (方法级别) | 两者都支持在控制器内部处理异常。 |
|             | `@ControllerAdvice` (全局) | `@ControllerAdvice` (全局) | 两者都支持全局异常处理。 |
| **拦截器/过滤器** | `HandlerInterceptor` (通过 `WebMvcConfigurer`) | `WebFilter` (通过 `WebFluxConfigurer` 或实现 `WebFilter` 接口) | 概念相似，但实现接口和配置方式不同。WebFilter 是一个响应式过滤器链。 |
| **服务器** | Servlet 容器 (Tomcat, Jetty, Undertow) | Reactor Netty, Undertow, Jetty (支持非阻塞) | WebFlux 可以运行在支持非阻塞 I/O 的服务器上。 |
| **数据访问** | Spring Data JPA (阻塞式), JdbcTemplate | Spring Data R2DBC (响应式) | R2DBC 是响应式关系型数据库连接驱动，用于 WebFlux。 |
| **测试** | `@WebMvcTest` | `@WebFluxTest` | 专门用于测试 Spring MVC 或 WebFlux 控制器。 |
|             | `MockMvc` | `WebTestClient` | WebTestClient 是用于测试 WebFlux 应用的非阻塞客户端。 |
| **WebSockets** | Spring WebSocket (STOMP) | Spring WebFlux WebSocket API | WebFlux 提供了更原生的响应式 WebSocket 支持。 |
| **函数式端点** | 不支持（主要通过注解） | `RouterFunction`, `HandlerFunction` | WebFlux 提供了函数式编程风格的端点定义方式，不需要 `@Controller` 注解。 |
| **安全整合** | Spring Security (基于 Servlet) | Spring Security (基于 Reactive) | 两者都可与 Spring Security 集成，但 WebFlux 版本是为响应式栈设计的。 |
| **事件处理** | ApplicationEventPublisher (同步或异步) | ApplicationEventPublisher (WebFlux 中常结合 `Mono`/`Flux` 处理异步事件) | 概念相似，但 WebFlux 更强调异步流式处理。 |


