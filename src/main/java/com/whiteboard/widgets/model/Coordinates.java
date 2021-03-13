package com.whiteboard.widgets.model;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class Coordinates {
    int x;
    int y;
}
