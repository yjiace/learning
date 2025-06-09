package cn.smallyoung.webflux;


import cn.smallyoung.webflux.base.BaseUserService;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author smallyoung
 * @date 2025/6/9
 */
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
