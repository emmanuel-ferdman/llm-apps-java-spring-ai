package com.thomasvitale.ai.spring.model;

import com.thomasvitale.ai.spring.BookService;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.model.function.FunctionCallbackWrapper;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Chat examples using the low-level ChatModel API.
 */
@RestController
@RequestMapping("/model")
class ChatModelController {

    private final BookService bookService;
    private final ChatModel chatModel;

    ChatModelController(BookService bookService, ChatModel chatModel) {
        this.bookService = bookService;
        this.chatModel = chatModel;
    }

    @GetMapping("/chat/function")
    String chat(String authorName) {
        var userPromptTemplate = new PromptTemplate("""
                What books written by {author} are available in the library?
                """);
        Map<String,Object> model = Map.of("author", authorName);
        var prompt = userPromptTemplate.create(model, OllamaOptions.builder()
                .withFunctions(Set.of("booksByAuthor"))
                .build());

        var chatResponse = chatModel.call(prompt);
        return chatResponse.getResult().getOutput().getContent();
    }

    @GetMapping("/chat/function/explicit")
    String chatVariant(String authorName) {
        var userPromptTemplate = new PromptTemplate("""
                What books written by {author} are available in the library?
                """);
        Map<String,Object> model = Map.of("author", authorName);
        var prompt = userPromptTemplate.create(model, OllamaOptions.builder()
                .withFunctionCallbacks(List.of(
                        FunctionCallbackWrapper.builder(bookService::getBooksByAuthor)
                                .withDescription("Get the list of books written by the given author available in the library")
                                .withName("BooksByAuthor")
                                .withInputType(BookService.Author.class)
                                .withResponseConverter(Object::toString)
                                .build()
                ))
                .build());

        var chatResponse = chatModel.call(prompt);
        return chatResponse.getResult().getOutput().getContent();
    }

}
