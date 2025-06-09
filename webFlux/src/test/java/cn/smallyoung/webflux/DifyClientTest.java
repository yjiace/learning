package cn.smallyoung.webflux;


import cn.smallyoung.webflux.dify.DifyClient;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.Map;

/**
 * @author smallyoung
 * @date 2025/6/9
 */
@SpringBootTest
public class DifyClientTest {

    @Resource
    private DifyClient difyClient;

    private static final String CHAT_TOKEN = "Bearer app-zhdPkLrFcP9DihiKtQdmneUa";
    private static final String WORKFLOW_TOKEN = "Bearer app-BgQgSRucjwenPEOVDZuNaw63";

    private static final String user = "test";

    /**
     * 发送对话消息
     */
    @Test
    public void chatMessages() {
        difyClient.chatMessages(CHAT_TOKEN,
                JSONObject.of("response_mode", "streaming", "user", user, "query", "你好", "inputs", Map.of()))
                .doOnNext(System.out::println).blockLast(Duration.ofSeconds(30));
    }

    /**
     * 上传文件
     */
    @Test
    public void filesUpload() {
        MultipartFile file = new MockMultipartFile(
                "file", // 参数名称
                "empty.txt", // 原始文件名
                "text/plain", // 文件类型
                new byte[0] // 文件内容（空字节数组）
        );
        difyClient.filesUpload(CHAT_TOKEN,user, file)
                .doOnNext(System.out::println).block(Duration.ofSeconds(30));
    }


    /**
     * 停止响应
     */
    @Test
    public void chatMessagesStop() {
        difyClient.chatMessagesStop(CHAT_TOKEN, JSONObject.of("user", user), "a15ec12b-179a-4fe6-a5c6-a1273e9cd854")
                .doOnNext(System.out::println).block(Duration.ofSeconds(30));
    }


    /**
     * 消息反馈（点赞）
     */
    @Test
    public void messagesFeedbacks() {
        difyClient.messagesFeedbacks(CHAT_TOKEN, JSONObject.of("user", user, "rating", "like"), "1405dd3a-3811-4c9a-be4a-503c0bcce0ea")
                .doOnNext(System.out::println).block(Duration.ofSeconds(30));
    }


    /**
     * 获取下一轮建议问题列表
     */
    @Test
    public void messagesSuggested() {
        difyClient.messagesSuggested(CHAT_TOKEN, user, "996156b5-e20d-49e3-a36c-9b2184e03311")
                .doOnNext(System.out::println).block(Duration.ofSeconds(30));
    }

    /**
     * 获取会话历史消息
     */
    @Test
    public void messages() {
        difyClient.messages(CHAT_TOKEN, "f42ecfc9-a8ad-4858-be00-1dd803d09f3c", user, null, null)
                .doOnNext(System.out::println).block(Duration.ofSeconds(30));
    }


    /**
     * 获取会话列表
     */
    @Test
    public void conversations() {
        difyClient.conversations(CHAT_TOKEN, null, user, null, null)
                .doOnNext(System.out::println).block(Duration.ofSeconds(30));
    }


    /**
     * 删除会话
     */
    @Test
    public void conversationsDelete() {
        difyClient.conversationsDelete(CHAT_TOKEN, JSONObject.of("user", user), "f42ecfc9-a8ad-4858-be00-1dd803d09f3c")
                .doOnNext(System.out::println).block(Duration.ofSeconds(30));
    }

    /**
     * 会话重命名
     */
    @Test
    public void conversationsName() {
        difyClient.conversationsName(CHAT_TOKEN, JSONObject.of("user", user, "auto_generate", true), "e3e21992-ecf8-494c-9ed2-fa102fba064e")
                .doOnNext(System.out::println).block(Duration.ofSeconds(30));
    }

    /**
     * 执行 workflow
     */
    @Test
    public void workflowsStreaming() {
        difyClient.workflowsStreaming(WORKFLOW_TOKEN, JSONObject.of("user", user, "response_mode", "streaming", "inputs", Map.of()))
                .doOnNext(System.out::println).blockLast(Duration.ofSeconds(30));
    }

    /**
     * 根据workflow执行ID获取workflow任务的状态
     */
    @Test
    public void workflowsStatus() {
        difyClient.workflowsStatus(WORKFLOW_TOKEN, "253fef4e-d225-4bff-8c07-14125b10a219")
                .doOnNext(System.out::println).block(Duration.ofSeconds(30));
    }

    /**
     * 停止响应
     */
    @Test
    public void workflowsStop() {
        difyClient.workflowsStop(WORKFLOW_TOKEN, "3a67f959-5019-4cbc-bb11-3e5b55b50f42", JSONObject.of("user", user))
                .doOnNext(System.out::println).block(Duration.ofSeconds(30));
    }


}
