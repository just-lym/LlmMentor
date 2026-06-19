package com.llmmentor.springai.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.llmmentor.springai.model.Book;
import com.llmmentor.springai.tools.WeatherTool;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Resource
    private DashScopeChatModel chatModel;

    private ChatClient chatClient;
    @Resource
    private WeatherTool weatherTool;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        ChatMemory chatMemory = MessageWindowChatMemory.builder().chatMemoryRepository(new InMemoryChatMemoryRepository()).maxMessages(10).build();
        MessageChatMemoryAdvisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
        chatClient = ChatClient.builder(chatModel)
                .defaultSystem("你是一个怼天怼地的毒舌，路过的狗都要被你骂一遍")
                .defaultAdvisors(memoryAdvisor,new SimpleLoggerAdvisor())
                .defaultOptions(DashScopeChatOptions.builder().model("qwen3.7-plus")
                        .multiModel(true).build())
                .build();
    }

    @GetMapping("/chat")
    public Flux<String> chat(String message, String chatId, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        return chatClient.prompt().user(message).advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId)).stream().content();
    }

    @GetMapping("/entity")
    public String structuredOut() throws JsonProcessingException {
        Book book = chatClient.prompt("你还是一个能推荐书籍的小助手").call().entity(Book.class);
        return objectMapper.writeValueAsString(book);
    }

    @GetMapping("/entityList")
    public String structuredOutList() throws JsonProcessingException {
        List<Book> books = chatClient.prompt("你还是一个能推荐书籍的小助手").call().entity(new ParameterizedTypeReference<List<Book>>() {
        });
        return objectMapper.writeValueAsString(books);
    }

    @GetMapping("/toolChat")
    public Flux<String> toolChat(String message, String chatId, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        return chatClient.prompt().user(message).advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId))
                .tools(weatherTool).stream().content();
    }
}
