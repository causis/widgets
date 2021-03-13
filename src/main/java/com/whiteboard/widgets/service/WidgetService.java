package com.whiteboard.widgets.service;

import com.google.common.collect.ImmutableList;
import com.whiteboard.widgets.model.Widget;
import com.whiteboard.widgets.service.request.WidgetContent;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class WidgetService {
    private final Map<String, Widget> widgetsById;
    private final SortedMap<Integer, Widget> widgetsByZIndex;

    public synchronized Widget create(WidgetContent input) {
        return store(widgetBuilder(input).id(UUID.randomUUID().toString()).build());
    }

    public synchronized Optional<Widget> update(String id, WidgetContent input) {
        return get(id)
                .map(stored -> {
                    widgetsByZIndex.remove(stored.getZ());
                    return widgetBuilder(input).id(id).build();
                }).map(this::store);
    }

    private Widget store(Widget input) {
        Optional<Widget> toInsert = Optional.of(input);
        while (toInsert.isPresent()) {
            Widget inserting = toInsert.get();
            widgetsById.put(inserting.getId(), inserting);
            toInsert = Optional.ofNullable(widgetsByZIndex.put(inserting.getZ(), inserting))
                    .filter(replaced -> !replaced.getId().equals(inserting.getId()))
                    .map(replaced -> replaced.toBuilder().z(replaced.getZ() + 1).build());
        }
        return input;
    }

    private Widget.WidgetBuilder widgetBuilder(WidgetContent input) {
        return Widget.builder()
                .coordinates(input.getCoordinates())
                .height(input.getHeight())
                .width(input.getWidth())
                .z(input.getZ().orElseGet(() -> widgetsByZIndex.isEmpty() ? 0 : widgetsByZIndex.lastKey() + 1))
                .lastModification(LocalDateTime.now());
    }

    public synchronized void delete(String id) {
        widgetsByZIndex.remove(widgetsById.get(id).getZ());
        widgetsById.remove(id);
    }

    public Optional<Widget> get(String id) {
        return Optional.ofNullable(widgetsById.get(id));
    }

    public List<Widget> list() {
        return widgetsByZIndex.values().stream().collect(ImmutableList.toImmutableList());
    }
}
