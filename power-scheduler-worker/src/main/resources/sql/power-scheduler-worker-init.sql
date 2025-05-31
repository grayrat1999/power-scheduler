DROP TABLE IF EXISTS job_progress;

CREATE TABLE job_progress
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    job_id          BIGINT,
    job_instance_id BIGINT,
    status          VARCHAR(50),
    start_at        DATETIME,
    end_at          DATETIME,
    message         TEXT
);
