CREATE SCHEMA unit8schema;

CREATE SEQUENCE unit8schema.author_seq;

CREATE TABLE unit8schema.author (
  id INTEGER PRIMARY KEY,
  name VARCHAR(255) NOT NULL
);

CREATE SEQUENCE unit8schema.reader_seq;

CREATE TABLE unit8schema.reader (
  id INTEGER PRIMARY KEY,
  name VARCHAR(255) NOT NULL
);

CREATE SEQUENCE unit8schema.book_seq;

CREATE TABLE unit8schema.book (
  id INTEGER PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  author_id INTEGER NOT NULL,
  current_reader_id INTEGER NULL,
  FOREIGN KEY (author_id) REFERENCES unit8schema.author(id),
  FOREIGN KEY (current_reader_id) REFERENCES unit8schema.reader(id)
);

CREATE TABLE unit8schema.reading_history (
  book_id INTEGER NOT NULL,
  reader_id INTEGER NOT NULL,
  from_date TIMESTAMP NOT NULL,
  to_date TIMESTAMP NOT NULL,
  FOREIGN KEY (book_id) REFERENCES unit8schema.book(id),
  FOREIGN KEY (reader_id) REFERENCES unit8schema.reader(id)
);