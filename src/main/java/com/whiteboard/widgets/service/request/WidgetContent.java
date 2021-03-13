package com.whiteboard.widgets.service.request;

import com.whiteboard.widgets.model.Coordinates;
import lombok.Builder;
import lombok.Value;

import java.util.Optional;

@Value
@Builder
public class WidgetContent {
    Coordinates coordinates;
    Optional<Integer> z;
    int width;
    int height;
}
