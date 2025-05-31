package org.grayrat.powerscheduler.worker.sample.frameless;

import org.grayrat.powerscheduler.worker.PowerSchedulerWorker;
import org.grayrat.powerscheduler.worker.PowerSchedulerWorkerProperties;
import org.grayrat.powerscheduler.worker.processor.ProcessorRegistry;
import org.grayrat.powerscheduler.worker.sample.frameless.processor.MyProcessor;
import org.grayrat.powerscheduler.worker.util.ClasspathUtil;

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
