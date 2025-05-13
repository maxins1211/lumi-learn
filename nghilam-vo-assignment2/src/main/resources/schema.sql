---- Drop tables in reverse dependency order
--DROP TABLE IF EXISTS lessonprogress;
--DROP TABLE IF EXISTS enrollment;
--DROP TABLE IF EXISTS lesson;
--DROP TABLE IF EXISTS course;
--DROP TABLE IF EXISTS user_role;
--DROP TABLE IF EXISTS sec_role;
--DROP TABLE IF EXISTS sec_user;
--
---- Create security tables
--CREATE TABLE IF NOT EXISTS sec_user (
--    userId SERIAL PRIMARY KEY,
--    email VARCHAR(100) NOT NULL UNIQUE,
--    encryptedPassword VARCHAR(100) NOT NULL
--);
--
--CREATE TABLE IF NOT EXISTS sec_role (
--    roleId SERIAL PRIMARY KEY,
--    roleName VARCHAR(100) NOT NULL UNIQUE
--);
--
--CREATE TABLE IF NOT EXISTS user_role (
--    id SERIAL PRIMARY KEY,
--    userId INTEGER NOT NULL,
--    roleId INTEGER NOT NULL,
--    FOREIGN KEY (userId) REFERENCES sec_user(userId),
--    FOREIGN KEY (roleId) REFERENCES sec_role(roleId)
--);
--
---- Application tables
--CREATE TABLE IF NOT EXISTS course (
--    id SERIAL PRIMARY KEY,
--    title VARCHAR(255),
--    description VARCHAR(255),
--    image_url VARCHAR(500) DEFAULT NULL
--);
--
--CREATE TABLE IF NOT EXISTS lesson (
--    id SERIAL PRIMARY KEY,
--    course_id INTEGER,
--    title VARCHAR(255),
--    intro_video_id VARCHAR(50) DEFAULT NULL,
--    content TEXT,
--    order_index INTEGER NOT NULL,
--    FOREIGN KEY (course_id) REFERENCES course(id)
--);
--
--CREATE TABLE IF NOT EXISTS enrollment (
--    id SERIAL PRIMARY KEY,
--    user_id INTEGER NOT NULL,
--    course_id INTEGER,
--    completion_status VARCHAR(20) DEFAULT 'in-progress',
--    FOREIGN KEY (user_id) REFERENCES sec_user(userId),
--    FOREIGN KEY (course_id) REFERENCES course(id)
--);
--
--CREATE TABLE IF NOT EXISTS lessonprogress (
--    id SERIAL PRIMARY KEY,
--    user_id INTEGER NOT NULL,
--    course_id INTEGER NOT NULL,
--    lesson_id INTEGER NOT NULL,
--    FOREIGN KEY (user_id) REFERENCES sec_user(userId),
--    FOREIGN KEY (course_id) REFERENCES course(id) ON DELETE CASCADE,
--    FOREIGN KEY (lesson_id) REFERENCES lesson(id) ON DELETE CASCADE,
--    UNIQUE (user_id, lesson_id)
--);
-- Add IF EXISTS to all DROP statements to prevent errors if tables don't exist
DROP TABLE IF EXISTS LESSONPROGRESS;
DROP TABLE IF EXISTS ENROLLMENT;
DROP TABLE IF EXISTS LESSON;
DROP TABLE IF EXISTS COURSE;
DROP TABLE IF EXISTS user_role;
DROP TABLE IF EXISTS sec_role;
DROP TABLE IF EXISTS sec_user;



-- Create security tables first
CREATE TABLE IF NOT EXISTS sec_user (
    userId BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(100) NOT NULL UNIQUE,
    encryptedPassword VARCHAR(100) NOT NULL
);


CREATE TABLE IF NOT EXISTS sec_role (
    roleId BIGINT PRIMARY KEY AUTO_INCREMENT,
    roleName VARCHAR(100) NOT NULL UNIQUE
);


CREATE TABLE IF NOT EXISTS user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    userId BIGINT NOT NULL,
    roleId BIGINT NOT NULL,
    FOREIGN KEY (userId) REFERENCES sec_user(userId),
    FOREIGN KEY (roleId) REFERENCES sec_role(roleId)
);


-- Now create application tables
CREATE TABLE IF NOT EXISTS Course (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255),
    description VARCHAR(255),
    image_url VARCHAR(500) DEFAULT NULL
);


CREATE TABLE IF NOT EXISTS Lesson (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    course_id BIGINT,
    title VARCHAR(255),
    intro_video_id VARCHAR(50) DEFAULT NULL,
    content TEXT,
    order_index INT NOT NULL,
    FOREIGN KEY (course_id) REFERENCES Course(id)
);


CREATE TABLE IF NOT EXISTS Enrollment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(100) NOT NULL,
    course_id BIGINT,
    completion_status VARCHAR(20) DEFAULT 'in-progress',
    FOREIGN KEY (course_id) REFERENCES Course(id)
);


CREATE TABLE IF NOT EXISTS LessonProgress (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(100) NOT NULL,
    course_id BIGINT NOT NULL,
    lesson_id BIGINT NOT NULL,
    FOREIGN KEY (course_id) REFERENCES Course(id) ON DELETE CASCADE,
    FOREIGN KEY (lesson_id) REFERENCES Lesson(id) ON DELETE CASCADE,
    UNIQUE (user_id, lesson_id)
);