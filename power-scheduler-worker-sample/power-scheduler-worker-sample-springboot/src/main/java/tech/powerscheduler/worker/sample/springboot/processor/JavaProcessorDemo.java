package tech.powerscheduler.worker.sample.springboot.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tech.powerscheduler.worker.processor.JavaProcessor;
import tech.powerscheduler.worker.processor.ProcessResult;
import tech.powerscheduler.worker.task.TaskContext;

import java.util.concurrent.TimeUnit;

/**
 * @author grayrat
 * @since 2025/5/9
 */
@Slf4j
@Component
public class JavaProcessorDemo extends JavaProcessor {

    @Override
    public ProcessResult process(TaskContext context) throws InterruptedException {
        log.info("job start");
        TimeUnit.SECONDS.sleep(60);
        log.info("job complete");
        return ProcessResult.success();
    }

}
