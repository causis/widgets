package com.whiteboard.widgets.config.spring;

import com.whiteboard.widgets.model.Widget;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class RepositoryConfig {

    @Bean
    public Map<String, Widget> widgetsById() {
        return new HashMap<>();
    }

    @Bean
    public SortedMap<Integer, Widget> widgetsByZIndex() {
        return new TreeMap<>();
    }
}
