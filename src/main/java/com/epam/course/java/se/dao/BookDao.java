package com.epam.course.java.se.dao;

import com.epam.course.java.se.data.BookDto;

import java.util.List;

public interface BookDao {
    List<BookDto> findByName(String name);
}
