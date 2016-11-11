package com.epam.course.java.se.dao;

import com.epam.course.java.se.data.AuthorDto;

import java.util.Optional;

public interface AuthorDao {
    Optional<AuthorDto> getByName(String name);
}
