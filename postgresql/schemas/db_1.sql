CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE SCHEMA IF NOT EXISTS quizdb;

CREATE TABLE IF NOT EXISTS quizdb.users(
    id BIGINT PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    group_id TEXT
);

CREATE TABLE IF NOT EXISTS quizdb.groups(
    id TEXT PRIMARY KEY DEFAULT uuid_generate_v4()
);

CREATE TABLE IF NOT EXISTS quizdb.quizzes(
    id TEXT PRIMARY KEY DEFAULT uuid_generate_v4(),
    group_id TEXT REFERENCES quizdb.groups(id),
    name TEXT NOT NULL,
    description TEXT NOT NULL
);