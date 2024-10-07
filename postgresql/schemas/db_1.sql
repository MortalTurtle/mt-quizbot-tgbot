CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP SCHEMA IF EXISTS quizdb CASCADE;

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

CREATE TABLE IF NOT EXISTS quizdb.group_roles(
    id TEXT PRIMARY KEY DEFAULT uuid_generate_v4(),
    name TEXT NOT NULL
);

INSERT INTO quizdb.group_roles(name) VALUES
('Owner'),
('Contributor'),
('Participant');

CREATE TABLE IF NOT EXISTS quizdb.group_users(
    group_id TEXT REFERENCES quizdb.groups(id),
    user_id BIGINT REFERENCES quizdb.users(id),
    group_role_id TEXT REFERENCES quizdb.group_roles(id),
    UNIQUE(group_id, user_id)
);

CREATE TABLE IF NOT EXISTS quizdb.tests(
    id TEXT PRIMARY KEY DEFAULT uuid_generate_v4(),
    group_id TEXT REFERENCES quizdb.groups(id),
    owner_id BIGINT REFERENCES quizdb.users(id),
    name TEXT NOT NULL,
    min_score INTEGER,
    description TEXT NOT NULL,
    created_ts TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_by_created_ts_tests ON quizdb.tests(created_ts);

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
    type_id TEXT REFERENCES quizdb.question_type(id),
    weight INTEGER,
    text TEXT NOT NULL,
    created_ts TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_by_created_ts_questions ON quizdb.test_questions(created_ts);

CREATE TABLE IF NOT EXISTS quizdb.question_answer(
    question_id TEXT REFERENCES quizdb.test_questions(id),
    text TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS quizdb.question_false_answers(
    question_id TEXT REFERENCES quizdb.test_questions(id),
    text TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS quizdb.test_results(
    user_id BIGINT REFERENCES quizdb.users(id),
    test_id TEXT REFERENCES quizdb.tests(id),
    score INTEGER NOT NULL
);