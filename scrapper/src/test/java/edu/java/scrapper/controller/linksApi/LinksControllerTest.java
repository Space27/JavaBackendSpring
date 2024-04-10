package edu.java.scrapper.controller.linksApi;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.scrapper.controller.linksApi.exception.ChatNotExistsException;
import edu.java.scrapper.controller.linksApi.exception.LinkAlreadyExistsException;
import edu.java.scrapper.controller.linksApi.exception.LinkNotFoundException;
import edu.java.scrapper.controller.request.AddLinkRequest;
import edu.java.scrapper.controller.request.RemoveLinkRequest;
import edu.java.scrapper.domain.dto.Link;
import edu.java.scrapper.controller.response.LinkResponse;
import edu.java.scrapper.controller.response.ListLinkResponse;
import edu.java.scrapper.service.daoService.LinkService;
import edu.java.scrapper.service.linkUpdateService.clientUpdate.ClientUpdater;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LinksController.class)
class LinksControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean(name = "jooqLinkService")
    LinkService linkService;
    @MockBean
    ClientUpdater clientUpdater;

    @Test
    @DisplayName("Корректный запрос добавления")
    void post_shouldReturnOkForCorrectRequest() throws Exception {
        Mockito.when(clientUpdater.supports(any())).thenReturn(true);
        Mockito.when(linkService.add(any(), any())).thenReturn(new Link(
            1L,
            URI.create("https://gist.github.com/"),
            OffsetDateTime.now(),
            OffsetDateTime.now()
        ));
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
        Mockito.when(clientUpdater.supports(any())).thenReturn(true);
        Mockito.when(linkService.add(any(), any())).thenThrow(new LinkAlreadyExistsException("fr"));
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
        Mockito.when(clientUpdater.supports(any())).thenReturn(true);
        Mockito.when(linkService.add(any(), any())).thenThrow(new ChatNotExistsException(1L));
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
    @DisplayName("Некорректная ссылка при добавлении")
    void post_shouldReturnValidErrorForIncorrectLink() throws Exception {
        AddLinkRequest addLinkRequest = new AddLinkRequest(URI.create("https://github.com/"));

        mockMvc.perform(post("/links")
                .header("Tg-Chat-Id", 1)
                .content(asJsonString(addLinkRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Некорректные параметры запроса"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.stacktrace").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.exceptionMessage").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.exceptionName").exists());
    }

    @Test
    @DisplayName("Корректный запрос получения ссылок")
    void get_shouldReturnOkForCorrectRequest() throws Exception {
        List<Link> links = List.of(
            new Link(1L, URI.create("https://gist.github.com/"), OffsetDateTime.now(), OffsetDateTime.now()),
            new Link(2L, URI.create("https://github.com/"), OffsetDateTime.now(), OffsetDateTime.now())
        );
        ListLinkResponse expected = new ListLinkResponse(links.stream()
            .map(link -> new LinkResponse(link.id(), link.url()))
            .toList(), links.size());
        Mockito.when(linkService.listAll(any())).thenReturn(links);

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
        Mockito.when(linkService.listAll(any())).thenThrow(new ChatNotExistsException(1L));

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
        Mockito.when(linkService.remove(any(), any())).thenReturn(new Link(
            1L,
            URI.create("https://gist.github.com/"),
            OffsetDateTime.now(),
            OffsetDateTime.now()
        ));
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
        Mockito.when(linkService.remove(any(), any())).thenThrow(new ChatNotExistsException(1L));
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
        Mockito.when(linkService.remove(any(), any())).thenThrow(new LinkNotFoundException("fr"));
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
