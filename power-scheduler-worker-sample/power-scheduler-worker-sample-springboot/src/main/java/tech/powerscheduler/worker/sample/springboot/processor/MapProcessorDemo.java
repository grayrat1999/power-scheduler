package tech.powerscheduler.worker.sample.springboot.processor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import tech.powerscheduler.worker.processor.MapProcessor;
import tech.powerscheduler.worker.processor.ProcessResult;
import tech.powerscheduler.worker.task.MapTaskContext;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author grayrat
 * @since 2025/6/17
 */
@Component
public class MapProcessorDemo extends MapProcessor {

    record NumberPrintSubTask(Integer minId, Integer maxId) {
    }

    @Override
    public @Nullable ProcessResult process(@NotNull MapTaskContext context) {
        if (isRootTask(context)) {
            List<NumberPrintSubTask> numberPrintSubTaskList = splitTask();
            return map(numberPrintSubTaskList, "subTask");
        } else if (Objects.equals(context.getTaskName(), "subTask")) {
            NumberPrintSubTask numberPrintSubTask = context.getSubTask(NumberPrintSubTask.class);
            if (numberPrintSubTask != null) {
                handleSubTask(numberPrintSubTask);
            }
            return ProcessResult.success(true);
        } else {
            throw new RuntimeException("unknown task: " + context.getTaskName());
        }
    }

    private void handleSubTask(NumberPrintSubTask numberPrintSubTask) {
        for (Integer i = numberPrintSubTask.minId; i < numberPrintSubTask.maxId; i++) {
            System.out.println(i);
        }
    }

    private List<NumberPrintSubTask> splitTask() {
        return Stream.iterate(0, i -> i + 1)
                .limit(10)
                .map(i -> new NumberPrintSubTask(i * 10 + 1, (i + 1) * 10))
                .toList();
    }
}
