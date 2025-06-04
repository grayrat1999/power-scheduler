package tech.powerscheduler.worker.sample.springboot.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tech.powerscheduler.worker.job.JobContext;
import tech.powerscheduler.worker.processor.JavaProcessor;
import tech.powerscheduler.worker.processor.ProcessResult;

import java.util.concurrent.TimeUnit;

/**
 * 长时间的任务
 *
 * @author grayrat
 * @since 2025/6/3
 */
@Slf4j
@Component
public class LongTimeJobProcessor extends JavaProcessor {

    @Override
    public ProcessResult process(JobContext context) throws InterruptedException {
        log.info("耗时的任务开始");
        TimeUnit.SECONDS.sleep(300);
        log.info("耗时的任务完成");
        return ProcessResult.success();
    }

}
