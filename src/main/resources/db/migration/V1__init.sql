CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50),
    email VARCHAR(100),
    position VARCHAR(30),
    birth DATE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE team (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    region VARCHAR(50),
    intro TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    leader_id BIGINT,
    FOREIGN KEY (leader_id) REFERENCES users(id)
);


CREATE TABLE team_member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    team_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role ENUM('LEADER','MANAGER','PLAYER') DEFAULT 'PLAYER',
    joined_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (team_id) REFERENCES team(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);


CREATE TABLE post (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    team_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (team_id) REFERENCES team(id),
    FOREIGN KEY (author_id) REFERENCES users(id)
);


CREATE TABLE event (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    team_id BIGINT NOT NULL,
    title VARCHAR(100),
    start_at DATETIME,
    end_at DATETIME,
    place VARCHAR(100),
    type ENUM('TRAINING','MATCH','ETC') DEFAULT 'ETC',
    FOREIGN KEY (team_id) REFERENCES team(id)
);


CREATE TABLE stadium (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(200),
    region VARCHAR(50),
    capacity INT,
    surface VARCHAR(50),
    phone VARCHAR(30),
    is_available BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE match_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    team1_id BIGINT NOT NULL,
    team2_id BIGINT NOT NULL,
    team1_score INT DEFAULT 0,
    team2_score INT DEFAULT 0,
    match_date DATETIME,
    place VARCHAR(100),
    summary TEXT,
    thumbnail_url VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (team1_id) REFERENCES team(id),
    FOREIGN KEY (team2_id) REFERENCES team(id)
);


CREATE TABLE media (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    team_id BIGINT NOT NULL,
    uploader_id BIGINT NOT NULL,
    file_url VARCHAR(255),
    type ENUM('IMAGE','VIDEO') DEFAULT 'IMAGE',
    uploaded_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (team_id) REFERENCES team(id),
    FOREIGN KEY (uploader_id) REFERENCES users(id)
);


CREATE TABLE chat_room (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    team_id BIGINT,
    name VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (team_id) REFERENCES team(id)
);

CREATE TABLE chat_message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    message TEXT NOT NULL,
    sent_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES chat_room(id),
    FOREIGN KEY (sender_id) REFERENCES users(id)
);


