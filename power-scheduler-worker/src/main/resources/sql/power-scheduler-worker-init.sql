DROP TABLE IF EXISTS job_progress;

CREATE TABLE job_progress
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    job_id          BIGINT NOT NULL,
    job_instance_id BIGINT NOT NULL,
    task_id         BIGINT NOT NULL,
    status          VARCHAR(50) NOT NULL,
    start_at        DATETIME,
    end_at          DATETIME,
    message         TEXT
);
