package org.grayrat.powerscheduler.worker.sample.springboot.processor;

import lombok.extern.slf4j.Slf4j;
import org.grayrat.powerscheduler.worker.job.JobContext;
import org.grayrat.powerscheduler.worker.processor.JavaProcessor;
import org.grayrat.powerscheduler.worker.processor.ProcessResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author grayrat
 * @since 2025/5/9
 */
@Slf4j
@Component
public class MyProcessor extends JavaProcessor {

    @Override
    public ProcessResult process(JobContext context) throws InterruptedException {
        log.info("job start");
        TimeUnit.SECONDS.sleep(60);
        log.info("job complete");
        return ProcessResult.success();
    }

}
