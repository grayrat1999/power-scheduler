package tech.powerscheduler.worker.sample.springboot.processor;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import tech.powerscheduler.worker.processor.JavaProcessor;
import tech.powerscheduler.worker.processor.ProcessResult;
import tech.powerscheduler.worker.task.TaskContext;

import java.util.concurrent.TimeUnit;

/**
 * 短时间的任务
 *
 * @author grayrat
 * @since 2025/6/3
 */
@Slf4j
@Component
public class ShortTimeJobProcessor extends JavaProcessor {

    @Override
    @Nullable
    public ProcessResult process(@NotNull TaskContext context) throws Exception {
        log.info("短暂的任务开始");
        TimeUnit.SECONDS.sleep(3);
        log.info("短暂的任务完成");
        return ProcessResult.success();
    }
}
