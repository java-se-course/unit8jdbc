package com.epam.course.java.se.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookDto {
    private final int id;
    private final String name;
    private final int authorId;
    private final Integer currentReaderId;
}
