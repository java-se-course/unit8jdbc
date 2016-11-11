package com.epam.course.java.se.dao;

import com.epam.course.java.se.data.AuthorDto;

public interface DaoFactory {
    AuthorDao getAuthorDao();

    ReaderDao getReaderDao();

    BookDao getBookDao();
}
