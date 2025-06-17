INSERT INTO task_progress(
    job_id, job_instance_id, task_id, status,
    start_at, end_at, message,
    sub_task_list_body, sub_task_name
)
VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
