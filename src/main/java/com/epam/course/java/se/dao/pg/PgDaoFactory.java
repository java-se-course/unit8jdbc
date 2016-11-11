package com.epam.course.java.se.dao.pg;

import com.epam.course.java.se.dao.AuthorDao;
import com.epam.course.java.se.dao.BookDao;
import com.epam.course.java.se.dao.DaoFactory;
import com.epam.course.java.se.dao.ReaderDao;

public class PgDaoFactory implements DaoFactory {
    @Override
    public AuthorDao getAuthorDao() {
        return new PgAuthorDao();
    }

    @Override
    public ReaderDao getReaderDao() {
        return new PgReaderDao();
    }

    @Override
    public BookDao getBookDao() {
        throw new UnsupportedOperationException();
    }
}
