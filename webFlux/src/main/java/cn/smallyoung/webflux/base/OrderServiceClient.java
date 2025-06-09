package cn.smallyoung.webflux.base;


import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 本接口描述了订单服务，提供的模拟服务，不可请求，并未配置Client注解
 * 假设我们有一个OrderService，需要支持多种请求和响应
 *
 * @author smallyoung
 * @date 2025/6/9
 */
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
