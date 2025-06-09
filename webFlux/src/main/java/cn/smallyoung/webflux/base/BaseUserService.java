package cn.smallyoung.webflux.base;


import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author smallyoung
 * @date 2025/6/4
 */
@Service
public class BaseUserService {

    @Resource
    private BaseUserClient baseUserClient;

    public Mono<List<JSONObject>> getAllUsers() {
        return baseUserClient.getAllUsers();
    }

}
