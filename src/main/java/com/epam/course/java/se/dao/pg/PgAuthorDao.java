package com.epam.course.java.se.dao.pg;

import com.epam.course.java.se.dao.AuthorDao;
import com.epam.course.java.se.data.AuthorDto;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Optional;

public class PgAuthorDao implements AuthorDao {
    @Override
    public Optional<AuthorDto> getByName(String name) {
        throw new UnsupportedOperationException();
    }
}
