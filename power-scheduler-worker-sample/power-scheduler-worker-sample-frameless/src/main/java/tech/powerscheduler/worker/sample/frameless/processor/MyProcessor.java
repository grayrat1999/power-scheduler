package tech.powerscheduler.worker.sample.frameless.processor;

import org.jetbrains.annotations.NotNull;
import tech.powerscheduler.worker.job.JobContext;
import tech.powerscheduler.worker.processor.JavaProcessor;
import tech.powerscheduler.worker.processor.ProcessResult;

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
