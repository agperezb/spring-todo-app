//// Copyright (c) Microsoft Corporation. All rights reserved.
//// Licensed under the MIT License.
//
//package com.azure.spring.samples;
//
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.BDDMockito.willAnswer;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//
//import com.azure.spring.samples.controller.TodoListController;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.invocation.InvocationOnMock;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.web.servlet.MockMvc;
//
//import com.azure.spring.samples.dao.TodoItemRepository;
//import com.azure.spring.samples.model.TodoItem;
//
//@ExtendWith(SpringExtension.class)
//@WebMvcTest(controllers = TodoListController.class)
//public class TodoApplicationTest {
//    static final String MOCK_ID = "mockId";
//    static final String MOCK_DESC = "Mock Item";
//    static final String MOCK_OWNER = "Owner of mock item";
//    final Map<String, TodoItem> repository = new HashMap<>();
//    final TodoItem mockItemA = new TodoItem(MOCK_ID + "-A", MOCK_DESC + "-A", MOCK_OWNER + "-A");
//    final TodoItem mockItemB = new TodoItem(MOCK_ID + "-B", MOCK_DESC + "-B", MOCK_OWNER + "-B");
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private TodoItemRepository todoItemRepository;
//
//    @BeforeEach
//    public void setUp() {
//        repository.clear();
//        repository.put(mockItemA.getId(), mockItemA);
//        repository.put(mockItemB.getId(), mockItemB);
//
//        given(this.todoItemRepository.save(any(TodoItem.class))).willAnswer((InvocationOnMock invocation) -> {
//            final TodoItem item = invocation.getArgument(0);
//            if (repository.containsKey(item.getId())) {
//                throw new Exception("Conflict.");
//            }
//            repository.put(item.getId(), item);
//            return item;
//        });
//
//        given(this.todoItemRepository.findById(any(String.class))).willAnswer((InvocationOnMock invocation) -> {
//            final String id = invocation.getArgument(0);
//            return Optional.of(repository.get(id));
//        });
//
//        given(this.todoItemRepository.findAll()).willAnswer((InvocationOnMock invocation) -> {
//            return new ArrayList<TodoItem>(repository.values());
//        });
//
//        willAnswer((InvocationOnMock invocation) -> {
//            final String id = invocation.getArgument(0);
//            if (!repository.containsKey(id)) {
//                throw new Exception("Not Found.");
//            }
//            repository.remove(id);
//            return null;
//        }).given(this.todoItemRepository).deleteById(any(String.class));
//    }
//
//    @AfterEach
//    public void tearDown() {
//        repository.clear();
//    }
//
//    @Test
//    public void shouldRenderDefaultTemplate() throws Exception {
//        mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk()).andExpect(forwardedUrl("index.html"));
//    }
//
//    @Test
//    public void canGetTodoItem() throws Exception {
//        mockMvc.perform(get(String.format("/api/todolist/%s", mockItemA.getId()))).andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().json(String.format("{\"id\":\"%s\",\"description\":\"%s\",\"owner\":\"%s\"}",
//                        mockItemA.getId(), mockItemA.getDescription(), mockItemA.getOwner())));
//    }
//
//    @Test
//    public void canGetAllTodoItems() throws Exception {
//        mockMvc.perform(get("/api/todolist")).andDo(print()).andExpect(status().isOk()).andExpect(content()
//                .json(String.format("[{\"id\":\"%s\"}, {\"id\":\"%s\"}]", mockItemA.getId(), mockItemB.getId())));
//    }
//
//    @Test
//    public void canSaveTodoItems() throws Exception {
//        final int size = repository.size();
//        final TodoItem mockItemC = new TodoItem(null, MOCK_DESC + "-C", MOCK_OWNER + "-C");
//        mockMvc.perform(post("/api/todolist").contentType(MediaType.APPLICATION_JSON_VALUE).content(String
//                .format("{\"description\":\"%s\",\"owner\":\"%s\"}", mockItemC.getDescription(), mockItemC.getOwner())))
//                .andDo(print()).andExpect(status().isCreated());
//        assertTrue(size + 1 == repository.size());
//    }
//
//    @Test
//    public void canDeleteTodoItems() throws Exception {
//        final int size = repository.size();
//        mockMvc.perform(delete(String.format("/api/todolist/%s", mockItemA.getId()))).andDo(print())
//                .andExpect(status().isOk());
//        assertTrue(size - 1 == repository.size());
//        assertFalse(repository.containsKey(mockItemA.getId()));
//    }
//
//    @Test
//    public void canUpdateTodoItems() throws Exception {
//        final String newItemJsonString = String.format("{\"id\":\"%s\",\"description\":\"%s\",\"owner\":\"%s\"}",
//                mockItemA.getId(), mockItemA.getDescription(), "New Owner");
//        mockMvc.perform(put("/api/todolist").contentType(MediaType.APPLICATION_JSON_VALUE).content(newItemJsonString))
//                .andDo(print()).andExpect(status().isOk());
//        assertTrue(repository.get(mockItemA.getId()).getOwner().equals("New Owner"));
//    }
//
//    @Test
//    public void canNotDeleteNonExistingTodoItems() throws Exception {
//        final int size = repository.size();
//        mockMvc.perform(delete(String.format("/api/todolist/%s", "Non-Existing-ID"))).andDo(print())
//                .andExpect(status().isNotFound());
//        assertTrue(size == repository.size());
//    }
//
//    /**
//     * PUT should be idempotent.
//     */
//    @Test
//    public void idempotenceOfPut() throws Exception {
//        final String newItemJsonString = String.format("{\"id\":\"%s\",\"description\":\"%s\",\"owner\":\"%s\"}",
//                mockItemA.getId(), mockItemA.getDescription(), "New Owner");
//        mockMvc.perform(put("/api/todolist").contentType(MediaType.APPLICATION_JSON_VALUE).content(newItemJsonString))
//                .andDo(print()).andExpect(status().isOk());
//        final TodoItem firstRes = repository.get(mockItemA.getId());
//        mockMvc.perform(put("/api/todolist").contentType(MediaType.APPLICATION_JSON_VALUE).content(newItemJsonString))
//                .andDo(print()).andExpect(status().isOk());
//        final TodoItem secondRes = repository.get(mockItemA.getId());
//        assertTrue(firstRes.equals(secondRes));
//    }
//}
