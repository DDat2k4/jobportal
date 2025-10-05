package com.example.jobportal.extension.paging;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Order {

    private String property;
    private Direction direction = Direction.ASC; // mặc định ASC

    public Order() {}

    public Order(String property, Direction direction) {
        this.property = property;
        this.direction = direction != null ? direction : Direction.ASC;
    }

    public boolean isAsc() {
        return direction == Direction.ASC;
    }

    public String getPropertyOrDefault(String defaultProperty) {
        return property != null ? property : defaultProperty;
    }

    public enum Direction {
        ASC, DESC
    }
}
