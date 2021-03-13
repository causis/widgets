package com.whiteboard.widgets.api;

import com.whiteboard.widgets.model.Widget;
import com.whiteboard.widgets.service.WidgetService;
import com.whiteboard.widgets.service.request.WidgetContent;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@AllArgsConstructor
public class WidgetController {
    private final WidgetService widgetService;

    @PostMapping("/widgets")
    public Widget create(@RequestBody WidgetContent input) {
        return widgetService.create(input);
    }

    @PutMapping("/widgets/{id}")
    public Widget update(@PathVariable String id, @RequestBody WidgetContent input) {
        return widgetService.update(id, input)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/widgets/{id}")
    public void delete(@PathVariable String id) {
        widgetService.delete(id);
    }

    @GetMapping("/widgets")
    public List<Widget> list() {
        return widgetService.list();
    }

    @GetMapping("/widgets/{id}")
    public Widget get(@PathVariable String id) {
        return widgetService.get(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
