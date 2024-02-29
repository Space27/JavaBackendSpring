package edu.java.scrapper.service.api.controller.linksApi;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.scrapper.service.api.controller.request.AddLinkRequest;
import edu.java.scrapper.service.api.controller.request.RemoveLinkRequest;
import edu.java.scrapper.repository.LinkStorage;
import edu.java.scrapper.service.api.controller.response.LinkResponse;
import edu.java.scrapper.service.api.controller.response.ListLinkResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.net.URI;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LinksApiController.class)
@Import(LinksHandler.class)
class LinkControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    LinkStorage linkStorage;

    @Test
    @DisplayName("Корректный запрос добавления")
    void post_shouldReturnOkForCorrectRequest() throws Exception {
        Mockito.when(linkStorage.contains(any())).thenReturn(true);
        Mockito.when(linkStorage.add(any(), any())).thenReturn(1L);
        AddLinkRequest addLinkRequest = new AddLinkRequest(URI.create("https://gist.github.com/"));

        mockMvc.perform(post("/links")
                .header("Tg-Chat-Id", 1)
                .content(asJsonString(addLinkRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
            .andExpect(MockMvcResultMatchers.jsonPath("$.url").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.url").value("https://gist.github.com/"));
    }

    @Test
    @DisplayName("Добавление уже добавленной ссылки")
    void post_shouldReturnErrorForRepeatLink() throws Exception {
        Mockito.when(linkStorage.contains(any())).thenReturn(true);
        Mockito.when(linkStorage.add(any(), any())).thenReturn(null);
        AddLinkRequest addLinkRequest = new AddLinkRequest(URI.create("https://gist.github.com/"));

        mockMvc.perform(post("/links")
                .header("Tg-Chat-Id", 1)
                .content(asJsonString(addLinkRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("409"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Ссылка уже добавлена"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.stacktrace").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.exceptionMessage").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.exceptionName").exists());
    }

    @Test
    @DisplayName("Добавление ссылки в несуществующий чат")
    void post_shouldReturnErrorForNotExistingChat() throws Exception {
        Mockito.when(linkStorage.contains(any())).thenReturn(false);
        AddLinkRequest addLinkRequest = new AddLinkRequest(URI.create("https://gist.github.com/"));

        mockMvc.perform(post("/links")
                .header("Tg-Chat-Id", 1)
                .content(asJsonString(addLinkRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotAcceptable())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("406"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Нельзя получить доступ к чату"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.stacktrace").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.exceptionMessage").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.exceptionName").exists());
    }

    @Test
    @DisplayName("Корректный запрос получения ссылок")
    void get_shouldReturnOkForCorrectRequest() throws Exception {
        List<LinkStorage.Link> links = List.of(
            new LinkStorage.Link(1L, "https://gist.github.com/"),
            new LinkStorage.Link(2L, "https://github.com/")
        );
        ListLinkResponse expected = new ListLinkResponse(links.stream()
            .map(link -> new LinkResponse(link.id(), URI.create(link.link())))
            .toList(), links.size());
        Mockito.when(linkStorage.contains(any())).thenReturn(true);
        Mockito.when(linkStorage.get(any())).thenReturn(links);

        mockMvc.perform(get("/links")
                .header("Tg-Chat-Id", 1)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.links").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.links").isArray())
            .andExpect(MockMvcResultMatchers.content().json(asJsonString(expected)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.size").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(links.size()));
    }

    @Test
    @DisplayName("Запрос получения ссылок из несуществующего чата")
    void get_shouldReturnErrorForNotExistingChat() throws Exception {
        Mockito.when(linkStorage.contains(any())).thenReturn(false);

        mockMvc.perform(get("/links")
                .header("Tg-Chat-Id", 1)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotAcceptable())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("406"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Нельзя получить доступ к чату"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.stacktrace").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.exceptionMessage").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.exceptionName").exists());
    }

    @Test
    @DisplayName("Корректный запрос удаления ссылки")
    void delete_shouldReturnOkForCorrectRequest() throws Exception {
        Mockito.when(linkStorage.contains(any())).thenReturn(true);
        Mockito.when(linkStorage.remove(any(), any())).thenReturn(1L);
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(URI.create("https://gist.github.com/"));

        mockMvc.perform(delete("/links")
                .header("Tg-Chat-Id", 1)
                .content(asJsonString(removeLinkRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
            .andExpect(MockMvcResultMatchers.jsonPath("$.url").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.url").value("https://gist.github.com/"));
    }

    @Test
    @DisplayName("Запрос удаления ссылки из несуществующего чата")
    void delete_shouldReturnErrorForNotExistingChat() throws Exception {
        Mockito.when(linkStorage.contains(any())).thenReturn(false);
        Mockito.when(linkStorage.remove(any(), any())).thenReturn(1L);
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(URI.create("https://gist.github.com/"));

        mockMvc.perform(delete("/links")
                .header("Tg-Chat-Id", 1)
                .content(asJsonString(removeLinkRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotAcceptable())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("406"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Нельзя получить доступ к чату"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.stacktrace").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.exceptionMessage").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.exceptionName").exists());
    }

    @Test
    @DisplayName("Запрос удаления несуществующей ссылки")
    void delete_shouldReturnErrorForNotExistingLink() throws Exception {
        Mockito.when(linkStorage.contains(any())).thenReturn(true);
        Mockito.when(linkStorage.remove(any(), any())).thenReturn(null);
        RemoveLinkRequest removeLinkRequest = new RemoveLinkRequest(URI.create("https://gist.github.com/"));

        mockMvc.perform(delete("/links")
                .header("Tg-Chat-Id", 1)
                .content(asJsonString(removeLinkRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("404"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Ссылка не найдена"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.stacktrace").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.exceptionMessage").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.exceptionName").exists());
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
