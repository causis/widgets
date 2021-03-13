package com.whiteboard.widgets.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.ImmutableList;
import com.whiteboard.widgets.model.Coordinates;
import com.whiteboard.widgets.model.Widget;
import com.whiteboard.widgets.service.request.WidgetContent;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class WidgetIntegrationTest {
    private static final int X = 10;
    private static final int Y = 20;
    private static final int Z = -10;
    private static final int HEIGHT = 30;
    private static final int WIDTH = 40;

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    @Autowired
    public WidgetIntegrationTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Test
    void save_noZIndex_ok() throws Exception {
        LocalDateTime before = LocalDateTime.now();

        Widget widget = createWidget(Optional.empty());

        assertNotNull(widget.getId());
        assertEquals(0, widget.getZ());
        assertEquals(Coordinates.builder().x(X).y(Y).build(), widget.getCoordinates());
        assertEquals(HEIGHT, widget.getHeight());
        assertEquals(WIDTH, widget.getWidth());

        assertNotNull(widget.getLastModification());
        assertTrue(before.isBefore(widget.getLastModification()) || before.isEqual(widget.getLastModification()));
        assertTrue(LocalDateTime.now().isAfter(widget.getLastModification()) || LocalDateTime.now().isEqual(widget.getLastModification()));

        deleteWidget(widget.getId());
    }

    @Test
    void save_withZIndex_ok() throws Exception {
        Widget widget = createWidget(Optional.of(-10));
        assertNotNull(widget.getLastModification());
        assertNotNull(widget.getId());
        assertEquals(Coordinates.builder().x(X).y(Y).build(), widget.getCoordinates());
        assertEquals(HEIGHT, widget.getHeight());
        assertEquals(WIDTH, widget.getWidth());
        assertEquals(Z, widget.getZ());

        deleteWidget(widget.getId());
    }

    @Test
    void delete_ok() throws Exception {
        Widget widget = createWidget(Optional.empty());

        deleteWidget(widget.getId());

        mockMvc.perform(get("/widgets"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void update_noZIndex_ok() throws Exception {
        Widget stored1 = createWidget(Optional.empty());
        Widget stored2 = createWidget(Optional.empty());

        LocalDateTime before = LocalDateTime.now();

        Widget updated = updateWidget(stored1.getId(), Optional.empty());

        assertEquals(stored1.getId(), updated.getId());

        assertNotNull(updated.getLastModification());
        assertTrue(before.isBefore(updated.getLastModification()) || before.isEqual(updated.getLastModification()));
        assertTrue(LocalDateTime.now().isAfter(updated.getLastModification()) || LocalDateTime.now().isEqual(updated.getLastModification()));

        verifyList(ImmutableList.of(stored2, updated));

        deleteWidget(stored2.getId());
        deleteWidget(updated.getId());
    }

    @Test
    void update_newZIndex_ok() throws Exception {
        Widget stored1 = createWidget(Optional.of(1));
        Widget stored2 = createWidget(Optional.of(2));

        Widget updated = updateWidget(stored1.getId(), Optional.of(5));

        assertEquals(5, updated.getZ());

        verifyList(ImmutableList.of(stored2, updated));

        deleteWidget(stored2.getId());
        deleteWidget(updated.getId());
    }

    @Test
    void update_invalidWidgetId_notFound() throws Exception {
        mockMvc.perform(put("/widgets/invalidId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(widgetContent(Optional.empty()))))
                .andExpect(status().isNotFound());
    }

    @Test
    void list_empty_ok() throws Exception {
        mockMvc.perform(get("/widgets"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void list_withElement_ok() throws Exception {
        Widget widget = createWidget(Optional.empty());

        verifyList(ImmutableList.of(widget));

        deleteWidget(widget.getId());
    }

    @Test
    void list_example1_ok() throws Exception {
        Widget widget1 = createWidget(Optional.of(1));
        Widget widget2 = createWidget(Optional.of(2));
        Widget widget3 = createWidget(Optional.of(3));
        Widget widgetNew2 = createWidget(Optional.of(2));

        verifyList(ImmutableList.of(
                buildWidget(widget1.getId(), widget1.getLastModification(), 1),
                buildWidget(widgetNew2.getId(), widgetNew2.getLastModification(), 2),
                buildWidget(widget2.getId(), widget2.getLastModification(), 3),
                buildWidget(widget3.getId(), widget3.getLastModification(), 4)));

        deleteWidget(widget1.getId());
        deleteWidget(widget2.getId());
        deleteWidget(widget3.getId());
        deleteWidget(widgetNew2.getId());
    }

    @Test
    void list_example2_ok() throws Exception {
        Widget widget1 = createWidget(Optional.of(1));
        Widget widget5 = createWidget(Optional.of(5));
        Widget widget6 = createWidget(Optional.of(6));
        Widget widget2 = createWidget(Optional.of(2));

        verifyList(ImmutableList.of(
                buildWidget(widget1.getId(), widget1.getLastModification(), 1),
                buildWidget(widget2.getId(), widget2.getLastModification(), 2),
                buildWidget(widget5.getId(), widget5.getLastModification(), 5),
                buildWidget(widget6.getId(), widget6.getLastModification(), 6)));

        deleteWidget(widget1.getId());
        deleteWidget(widget2.getId());
        deleteWidget(widget5.getId());
        deleteWidget(widget6.getId());
    }

    @Test
    void list_example3_ok() throws Exception {
        Widget widget1 = createWidget(Optional.of(1));
        Widget widget2 = createWidget(Optional.of(2));
        Widget widget4 = createWidget(Optional.of(4));
        Widget widgetNew2 = createWidget(Optional.of(2));

        verifyList(ImmutableList.of(
                buildWidget(widget1.getId(), widget1.getLastModification(), 1),
                buildWidget(widgetNew2.getId(), widgetNew2.getLastModification(), 2),
                buildWidget(widget2.getId(), widget2.getLastModification(), 3),
                buildWidget(widget4.getId(), widget4.getLastModification(), 4)));

        deleteWidget(widget1.getId());
        deleteWidget(widget2.getId());
        deleteWidget(widget4.getId());
        deleteWidget(widgetNew2.getId());
    }

    @Test
    void get_invalidWidgetId_notFound() throws Exception {
        mockMvc.perform(get("/widgets/invalidId"))
                .andExpect(status().isNotFound());
    }

    @Test
    void get_ok() throws Exception {
        Widget widget = createWidget(Optional.empty());

        verifyWidget(widget);

        deleteWidget(widget.getId());
    }

    private Widget buildWidget(String id, LocalDateTime lastModification, Integer zIndex) {
        return Widget.builder()
                .id(id)
                .coordinates(Coordinates.builder().x(X).y(Y).build())
                .height(HEIGHT)
                .width(WIDTH)
                .lastModification(lastModification)
                .z(zIndex)
                .build();
    }

    private WidgetContent widgetContent(Optional<Integer> zIndex) {
        return WidgetContent.builder()
                .coordinates(Coordinates.builder().x(X).y(Y).build())
                .height(HEIGHT)
                .width(WIDTH)
                .z(zIndex)
                .build();
    }

    private void verifyList(List<Widget> expectedList) throws Exception {
        mockMvc.perform(get("/widgets"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .string(Matchers.equalTo(objectMapper.writeValueAsString(expectedList))));

        expectedList.forEach(this::verifyWidget);
    }

    private void verifyWidget(Widget widget) {
        try {
            mockMvc.perform(get("/widgets/{id}", widget.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().string(Matchers.equalTo(objectMapper.writeValueAsString(widget))));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Widget updateWidget(String id, Optional<Integer> zIndex) throws Exception {
        return objectMapper.readValue(mockMvc
                .perform(put("/widgets/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(widgetContent(zIndex))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(), Widget.class);
    }

    private Widget createWidget(Optional<Integer> zIndex) throws Exception {
        return objectMapper.readValue(mockMvc
                .perform(post("/widgets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(widgetContent(zIndex))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(), Widget.class);
    }

    private void deleteWidget(String id) throws Exception {
        mockMvc.perform(delete("/widgets/{id}", id))
                .andExpect(status().isOk());
    }
}
