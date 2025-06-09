package cn.smallyoung.webflux.base;


import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author smallyoung
 * @date 2025/6/4
 */

@HttpExchange(url = "/users", accept = "application/json", contentType = "application/json")
public interface BaseUserClient {

    // 方法级别：继承类级别设置，并定义具体路径
    @GetExchange("/") // 等同于 @HttpExchange(url = "/api/users", method = "GET")
    Mono<List<JSONObject>> getAllUsers();

    // 方法级别：覆盖类级别设置，使用不同的contentType
    @PostExchange(url = "/files", contentType = "multipart/form-data")
    Mono<String> uploadFile(@RequestPart("file") Resource file);

}
