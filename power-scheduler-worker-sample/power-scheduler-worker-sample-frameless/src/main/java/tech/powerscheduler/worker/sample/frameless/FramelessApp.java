package tech.powerscheduler.worker.sample.frameless;

import tech.powerscheduler.worker.PowerSchedulerWorker;
import tech.powerscheduler.worker.PowerSchedulerWorkerProperties;
import tech.powerscheduler.worker.processor.ProcessorRegistry;
import tech.powerscheduler.worker.sample.frameless.processor.MyProcessor;
import tech.powerscheduler.worker.util.ClasspathUtil;

import java.io.InputStream;

/**
 * @author grayrat
 * @description TODO
 * @since 2025/5/7
 */
public class FramelessApp {

    public static void main(String[] args) {
        ProcessorRegistry.register(new MyProcessor());
        InputStream inputStream = ClasspathUtil.getInputStream("power-scheduler-worker.properties");
        PowerSchedulerWorkerProperties properties = PowerSchedulerWorkerProperties.load(inputStream);
        PowerSchedulerWorker powerSchedulerWorker = new PowerSchedulerWorker(properties);
        powerSchedulerWorker.init();
    }

}
