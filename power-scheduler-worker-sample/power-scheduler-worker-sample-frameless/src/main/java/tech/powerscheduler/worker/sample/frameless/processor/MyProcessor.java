package tech.powerscheduler.worker.sample.frameless.processor;

import org.jetbrains.annotations.NotNull;
import tech.powerscheduler.worker.processor.JavaProcessor;
import tech.powerscheduler.worker.processor.ProcessResult;
import tech.powerscheduler.worker.task.TaskContext;

import java.util.concurrent.TimeUnit;

/**
 * 自定义的任务处理器
 *
 * @author grayrat
 * @since 2025/5/9
 */
public class MyProcessor extends JavaProcessor {

    @Override
    public ProcessResult process(@NotNull TaskContext context) throws InterruptedException {
        TimeUnit.SECONDS.sleep(10);
        System.out.println("hello world");
        return ProcessResult.success();
    }

}
