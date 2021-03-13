package com.whiteboard.widgets.service;

import com.google.common.collect.ImmutableList;
import com.whiteboard.widgets.model.Widget;
import com.whiteboard.widgets.service.request.WidgetContent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WidgetServiceTest {
    private static final String ID_1 = "id1";
    private static final String ID_2 = "id2";
    private static final String ID_3 = "id3";

    private static final int Z_INDEX = 10;

    @Mock
    private Map<String, Widget> widgetsById;

    @Mock
    private SortedMap<Integer, Widget> widgetsByZIndex;

    private WidgetService widgetService;

    @Captor
    private ArgumentCaptor<String> idCaptor;

    @Captor
    private ArgumentCaptor<Integer> zCaptor;

    @Captor
    private ArgumentCaptor<Widget> widgetByIdCaptor;

    @Captor
    private ArgumentCaptor<Widget> widgetByZCaptor;

    @BeforeEach
    void setUp() {
        widgetService = new WidgetService(widgetsById, widgetsByZIndex);
    }

    @Test
    void get_doesNotExist_empty() {
        assertEquals(Optional.empty(), widgetService.get(ID_1));
    }

    @Test
    void get_exists_ok() {
        Widget widget = widget(ID_1);
        when(widgetsById.get(ID_1)).thenReturn(widget);

        assertEquals(Optional.of(widget), widgetService.get(ID_1));
    }

    @Test
    void list_empty_ok() {
        when(widgetsByZIndex.values()).thenReturn(ImmutableList.of());

        assertEquals(ImmutableList.of(), widgetService.list());
    }

    @Test
    void list_withValues_ok() {
        when(widgetsByZIndex.values()).thenReturn(ImmutableList.of(widget(ID_1), widget(ID_2), widget(ID_3)));

        assertEquals(ImmutableList.of(widget(ID_1), widget(ID_2), widget(ID_3)), widgetService.list());
    }


    @Test
    void delete_ok() {
        when(widgetsById.get(eq(ID_1))).thenReturn(widget(ID_1));

        widgetService.delete(ID_1);

        verify(widgetsById).remove(ID_1);
        verify(widgetsByZIndex).remove(Z_INDEX);
    }

    @Test
    void create_withZIndex_ok() {
        widgetService.create(WidgetContent.builder().z(Optional.of(Z_INDEX)).build());

        verify(widgetsById).put(idCaptor.capture(), widgetByIdCaptor.capture());
        verify(widgetsByZIndex).put(eq(Z_INDEX), widgetByZCaptor.capture());

        Widget insertedById = widgetByIdCaptor.getValue();
        Widget insertedByZ = widgetByZCaptor.getValue();

        assertEquals(insertedById, insertedByZ);
        assertEquals(idCaptor.getValue(), insertedById.getId());
        assertEquals(Z_INDEX, insertedById.getZ());
    }

    @Test
    void create_noZIndexEmptyMap_0ZIndex() {
        when(widgetsByZIndex.isEmpty()).thenReturn(true);

        widgetService.create(WidgetContent.builder().z(Optional.empty()).build());

        verify(widgetsById).put(idCaptor.capture(), widgetByIdCaptor.capture());
        verify(widgetsByZIndex).put(zCaptor.capture(), widgetByZCaptor.capture());

        Widget insertedById = widgetByIdCaptor.getValue();
        Widget insertedByZ = widgetByZCaptor.getValue();

        assertEquals(insertedById, insertedByZ);
        assertEquals(idCaptor.getValue(), insertedById.getId());
        assertEquals(zCaptor.getValue(), insertedById.getZ());
        assertEquals(0, insertedById.getZ());
    }

    @Test
    void create_noZIndexNonEmptyMap_0ZIndex() {
        when(widgetsByZIndex.isEmpty()).thenReturn(false);
        when(widgetsByZIndex.lastKey()).thenReturn(15);

        widgetService.create(WidgetContent.builder().z(Optional.empty()).build());

        verify(widgetsById).put(idCaptor.capture(), widgetByIdCaptor.capture());
        verify(widgetsByZIndex).put(zCaptor.capture(), widgetByZCaptor.capture());

        Widget insertedById = widgetByIdCaptor.getValue();
        Widget insertedByZ = widgetByZCaptor.getValue();

        assertEquals(insertedById, insertedByZ);
        assertEquals(idCaptor.getValue(), insertedById.getId());
        assertEquals(zCaptor.getValue(), insertedById.getZ());
        assertEquals(16, insertedById.getZ());
    }

    private Widget widget(String id) {
        return Widget.builder().id(id).z(Z_INDEX).build();
    }
}