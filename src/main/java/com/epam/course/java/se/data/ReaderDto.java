package com.epam.course.java.se.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReaderDto {
    private final int id;
    private final String name;
}
