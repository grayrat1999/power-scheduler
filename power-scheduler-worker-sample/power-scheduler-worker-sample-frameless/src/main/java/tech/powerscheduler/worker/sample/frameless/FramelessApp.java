package tech.powerscheduler.worker.sample.frameless;

import tech.powerscheduler.worker.PowerSchedulerWorker;
import tech.powerscheduler.worker.PowerSchedulerWorkerProperties;
import tech.powerscheduler.worker.processor.ProcessorRegistry;
import tech.powerscheduler.worker.sample.frameless.processor.MyProcessor;
import tech.powerscheduler.worker.util.ClasspathUtil;

import java.io.InputStream;

/**
 * 无框架应用启动类
 *
 * @author grayrat
 * @since 2025/5/7
 */
public class FramelessApp {

    public static void main(String[] args) {
        // 注册任务处理器
        ProcessorRegistry.register(new MyProcessor());
        // 读取配置文件对PowerSchedulerWorker进行初始化
        InputStream inputStream = ClasspathUtil.getInputStream("power-scheduler-worker.properties");
        PowerSchedulerWorkerProperties properties = PowerSchedulerWorkerProperties.load(inputStream);
        PowerSchedulerWorker powerSchedulerWorker = new PowerSchedulerWorker(properties);
        powerSchedulerWorker.init();
    }

}
