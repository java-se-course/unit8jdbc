package com.epam.course.java.se.dao.pg;

import com.epam.course.java.se.dao.ReaderDao;
import com.epam.course.java.se.data.ReaderDto;

import java.util.Optional;

public class PgReaderDao implements ReaderDao {
    @Override
    public Optional<ReaderDto> getByName(String name) {
        throw new UnsupportedOperationException();
    }
}
