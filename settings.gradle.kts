rootProject.name = "power-scheduler"

include(
    "power-scheduler-common",
    "power-scheduler-server",
    "power-scheduler-server:power-scheduler-server-infrastructure",
    "power-scheduler-server:power-scheduler-server-domain",
    "power-scheduler-server:power-scheduler-server-application",
    "power-scheduler-server:power-scheduler-server-interface",
    "power-scheduler-server:power-scheduler-server-bootstrap",
    "power-scheduler-worker",
    "power-scheduler-worker-spring-boot-autoconfigure",
    "power-scheduler-worker-sample",
    "power-scheduler-worker-sample:power-scheduler-worker-sample-frameless",
    "power-scheduler-worker-sample:power-scheduler-worker-sample-springboot",
)
