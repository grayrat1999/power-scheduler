package tech.powerscheduler.worker.sample.springboot.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tech.powerscheduler.worker.job.JobContext;
import tech.powerscheduler.worker.processor.JavaProcessor;
import tech.powerscheduler.worker.processor.ProcessResult;

import java.util.concurrent.TimeUnit;

/**
 * 50%概率失败的任务
 *
 * @author grayrat
 * @since 2025/6/3
 */
@Slf4j
@Component
public class MayFailJobProcessor extends JavaProcessor {

    @Override
    public ProcessResult process(JobContext context) throws InterruptedException {
        boolean fail = System.currentTimeMillis() % 2 == 1;
        TimeUnit.SECONDS.sleep(3);
        if (fail) {
            return ProcessResult.failure("这是错误信息的内容");
        }
        return ProcessResult.success();
    }

}
