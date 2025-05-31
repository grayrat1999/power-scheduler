package org.grayrat.powerscheduler.worker.sample.frameless.processor;

import org.grayrat.powerscheduler.worker.job.JobContext;
import org.grayrat.powerscheduler.worker.processor.JavaProcessor;
import org.grayrat.powerscheduler.worker.processor.ProcessResult;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * @author grayrat
 * @description TODO
 * @since 2025/5/9
 */
public class MyProcessor extends JavaProcessor {

    @Override
    public ProcessResult process(@NotNull JobContext context) throws InterruptedException {
        TimeUnit.SECONDS.sleep(10);
        System.out.println("hello world");
        return ProcessResult.success();
    }

}
