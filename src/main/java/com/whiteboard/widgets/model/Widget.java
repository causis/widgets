package com.whiteboard.widgets.model;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Value
@Builder(toBuilder = true)
@Jacksonized
public class Widget {
    String id;

    Coordinates coordinates;
    int z;
    int width;
    int height;
    LocalDateTime lastModification;
}
