package cn.smallyoung.webflux.dify;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author smallyoung
 * @date 2024/7/30
 */

@HttpExchange(url = "https://dify.aipfuture.com/v1")
public interface DifyClient {

    /**
     * 发送对话消息
     *
     * @param authorization 鉴权的api-key
     * @param request       请求体
     * @return 响应
     */
    @PostExchange("/chat-messages")
    Flux<JSONObject> chatMessages(@RequestHeader(name = "Authorization") String authorization, @RequestBody JSONObject request);

    /**
     * 上传文件
     *
     * @param authorization 鉴权的api-key
     * @param user          用户标识
     * @param file          文件
     * @return 响应
     */
    @PostExchange("files/upload")
    Mono<JSONObject> filesUpload(@RequestHeader(name = "Authorization") String authorization,
                                 @RequestParam(name = "user") String user, @RequestPart(name = "file") MultipartFile file);


    /**
     * 停止响应
     *
     * @param authorization 鉴权的api-key
     * @param request       请求体
     * @param taskId        任务 ID，可在流式返回 Chunk 中获取
     * @return 响应
     */
    @PostExchange("chat-messages/{taskId}/stop")
    Mono<JSONObject> chatMessagesStop(@RequestHeader(name = "Authorization") String authorization,
                                      @RequestBody JSONObject request, @PathVariable String taskId);


    /**
     * 消息反馈（点赞）
     *
     * @param authorization 鉴权的api-key
     * @param request       请求体
     * @param messageId     消息 ID
     * @return 响应
     */
    @PostExchange("messages/{messageId}/feedbacks")
    Mono<JSONObject> messagesFeedbacks(@RequestHeader(name = "Authorization") String authorization,
                                       @RequestBody JSONObject request, @PathVariable String messageId);


    /**
     * 获取下一轮建议问题列表
     *
     * @param authorization 鉴权的api-key
     * @param user          用户标识，由开发者定义规则，需保证用户标识在应用内唯一
     * @param messageId     消息 ID
     * @return 响应
     */
    @GetExchange("messages/{messageId}/suggested")
    Mono<JSONObject> messagesSuggested(@RequestHeader(name = "Authorization") String authorization,
                                       @RequestParam(name = "user") String user, @PathVariable String messageId);

    /**
     * 获取会话历史消息
     *
     * @param authorization  鉴权的api-key
     * @param conversationId 会话 ID
     * @param user           用户标识，由开发者定义规则，需保证用户标识在应用内唯一
     * @param firstId        当前页第一条聊天记录的 ID，默认 null
     * @param limit          一次请求返回多少条聊天记录，默认 20 条。
     * @return 响应
     */
    @GetExchange("messages")
    Mono<JSONObject> messages(@RequestHeader(name = "Authorization") String authorization,
                              @RequestParam(name = "conversation_id") String conversationId,
                              @RequestParam(name = "user") String user,
                              @RequestParam(name = "first_id", required = false) String firstId,
                              @RequestParam(name = "limit", required = false) Integer limit);


    /**
     * 获取会话列表
     *
     * @param authorization 鉴权的api-key
     * @param lastId        当前页最后面一条记录的 ID，默认 null
     * @param user          用户标识，由开发者定义规则，需保证用户标识在应用内唯一
     * @param limit         一次请求返回多少条记录
     * @param pinned        只返回置顶 true，只返回非置顶 false
     * @return 响应
     */
    @GetExchange("conversations")
    Mono<JSONObject> conversations(@RequestHeader(name = "Authorization") String authorization,
                                   @RequestParam(name = "last_id", required = false) String lastId,
                                   @RequestParam(name = "user") String user,
                                   @RequestParam(name = "limit", required = false) Integer limit,
                                   @RequestParam(name = "pinned", required = false) Boolean pinned);


    /**
     * 删除会话
     *
     * @param authorization  鉴权的api-key
     * @param request        请求参数
     * @param conversationId 会话ID
     * @return 响应
     */
    @DeleteExchange("conversations/{conversationId}")
    Mono<JSONObject> conversationsDelete(@RequestHeader(name = "Authorization") String authorization,
                                         @RequestBody JSONObject request,
                                         @PathVariable String conversationId);


    /**
     * 会话重命名
     *
     * @param authorization  鉴权的api-key
     * @param request        请求参数
     * @param conversationId 会话ID
     * @return 响应
     */
    @PostExchange("conversations/{conversationId}/name")
    Mono<JSONObject> conversationsName(@RequestHeader(name = "Authorization") String authorization,
                                       @RequestBody JSONObject request,
                                       @PathVariable String conversationId);


    /**
     * 执行 workflow
     *
     * @param authorization 鉴权的api-key
     * @param request       请求参数
     * @return 响应
     */
    @PostExchange("workflows/run")
    Flux<JSONObject> workflowsStreaming(@RequestHeader(name = "Authorization") String authorization,
                                        @RequestBody JSONObject request);

    /**
     * 根据workflow执行ID获取workflow任务的状态
     *
     * @param authorization 鉴权的api-key
     * @param workflowId    执行 ID，可在流式返回 Chunk 中获取
     * @return 响应
     */
    @GetExchange("workflows/run/{workflowId}")
    Mono<JSONObject> workflowsStatus(@RequestHeader(name = "Authorization") String authorization,
                                     @PathVariable String workflowId);

    /**
     * 停止响应
     *
     * @param authorization 鉴权的api-key
     * @param taskId        任务 ID，可在流式返回 Chunk 中获取
     * @param request       请求参数
     * @return 响应
     */
    @PostExchange("workflows/tasks/{taskId}/stop")
    Mono<JSONObject> workflowsStop(@RequestHeader(name = "Authorization") String authorization,
                                   @PathVariable String taskId, @RequestBody JSONObject request);

}
