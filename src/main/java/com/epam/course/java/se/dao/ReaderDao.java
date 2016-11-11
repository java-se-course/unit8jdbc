package com.epam.course.java.se.dao;

import com.epam.course.java.se.data.ReaderDto;

import java.util.Optional;

public interface ReaderDao {
    Optional<ReaderDto> getByName(String name);
}
