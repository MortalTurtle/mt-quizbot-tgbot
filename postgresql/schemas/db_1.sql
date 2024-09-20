CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE SCHEMA IF NOT EXISTS quizdb;

CREATE TABLE IF NOT EXISTS quizdb.groups(
    id TEXT PRIMARY KEY DEFAULT uuid_generate_v4(),
    name TEXT NOT NULL,
    description TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS quizdb.users(
    id BIGINT PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    group_id TEXT
);

CREATE TABLE IF NOT EXISTS quizdb.group_role(
    id TEXT PRIMARY KEY DEFAULT uuid_generate_v4(),
    name TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS quizdb.group_users(
    group_id TEXT REFERENCES quizdb.groups(id),
    user_id BIGINT REFERENCES quizdb.users(id)
);

CREATE TABLE IF NOT EXISTS quizdb.tests(
    id TEXT PRIMARY KEY DEFAULT uuid_generate_v4(),
    group_id TEXT REFERENCES quizdb.groups(id),
    owner BIGINT REFERENCES quizdb.users(id),
    name TEXT NOT NULL,
    min_score INTEGER NOT NULL,
    description TEXT NOT NULL,
    created_ts TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_by_created_ts ON quizdb.tests(created_ts);

CREATE TABLE IF NOT EXISTS quizdb.question_type(
    id TEXT PRIMARY KEY DEFAULT uuid_generate_v4(),
    type TEXT NOT NULL,
    description TEXT NOT NULL
);

INSERT INTO quizdb.question_type(type, description) VALUES
('Choose', 'You have to choose the right answer'),
('Write', 'You have to write the right answer');

CREATE TABLE IF NOT EXISTS quizdb.test_questions(
    id TEXT PRIMARY KEY DEFAULT uuid_generate_v4(),
    test_id TEXT REFERENCES quizdb.tests(id),
    weight INTEGER NOT NULL,
    text TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS quizdb.question_answer(
    question_id TEXT REFERENCES quizdb.test_questions(id),
    text TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS quizdb.test_results(
    user_id BIGINT REFERENCES quizdb.users(id),
    test_id TEXT REFERENCES quizdb.tests(id),
    score INTEGER NOT NULL
);