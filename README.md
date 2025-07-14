# Power Scheduler

Power Scheduler 是一款分布式任务调度平台，支持多语言 Worker，具备高可用、易扩展、易集成等特性，适用于定时任务、批处理任务等场景的统一调度和管理。

## 主要特性

- **定时策略丰富**：支持 CRON，固定频率，固定延迟 和 一次性任务
- **高可用**：具备故障转移与自动恢复能力，保证任务调度不中断
- **高性能**：任务调度，分发，执行，上报的全流程均采用并行化的方案实现，支持高并发任务调度，适合大规模任务场景
- **可伸缩**：Server 与 Worker 节点均可水平扩展，支持动态增加节点以提升系统吞吐能力
- **依赖精简**：系统仅依赖关系型数据库
- **多语言 Worker 支持**：基于 HTTP 协议实现 Server 与 Worker 的通信协议，支持使用不同的编程语言开发 Worker 端 SDK
- **Docker 支持**：支持容器化运行

## 在线试用
- [试用地址](http://www.powerscheduler.tech)

## 官方文档

- [中文文档](https://github.com/grayrat1999/power-scheduler-doc/tree/main)

## 贡献

欢迎提交 Issue 和 PR！如有建议或问题请在 [GitHub Issues](https://github.com/grayrat1999/power-scheduler/issues) 反馈。

## License

[Apache License, Version 2.0](LICENSE)