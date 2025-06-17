package tech.powerscheduler.worker.sample.springboot.processor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import tech.powerscheduler.worker.processor.MapReduceProcessor;
import tech.powerscheduler.worker.processor.ProcessResult;
import tech.powerscheduler.worker.task.MapTaskContext;
import tech.powerscheduler.worker.task.ReduceTaskContext;
import tech.powerscheduler.worker.task.TaskResultSet;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author grayrat
 * @since 2025/6/18
 */
@Component
public class MapReduceProcessorDemo extends MapReduceProcessor {

    record NumberSumSubTask(Integer minId, Integer maxId) {
    }

    record Result(Integer sumValue) {
    }

    @Override
    @Nullable
    public ProcessResult process(@NotNull MapTaskContext context) throws Exception {
        if (isRootTask(context)) {
            List<NumberSumSubTask> numberSumSubTaskList = splitTask();
            return map(numberSumSubTaskList, "subTask");
        } else if (Objects.equals(context.getTaskName(), "subTask")) {
            NumberSumSubTask numberSumSubTask = context.getSubTask(NumberSumSubTask.class);
            if (numberSumSubTask != null) {
                Integer result = handleSubTask(numberSumSubTask);
                return ProcessResult.success(result);
            }
            return ProcessResult.success(0);
        } else {
            throw new RuntimeException("unknown task: " + context.getTaskName());
        }
    }

    private Integer handleSubTask(NumberSumSubTask numberSumSubTask) {
        return Stream.iterate(numberSumSubTask.minId, min -> min + 1)
                .mapToInt(Integer::intValue)
                .limit(numberSumSubTask.maxId - numberSumSubTask.minId + 1)
                .sum();
    }

    private List<NumberSumSubTask> splitTask() {
        return Stream.iterate(0, i -> i + 1)
                .limit(10)
                .map(i -> new NumberSumSubTask(i * 10 + 1, (i + 1) * 10))
                .toList();
    }

    @Override
    @Nullable
    public ProcessResult reduce(@NotNull ReduceTaskContext context) throws Exception {
        context.setFetchResultBatchSize(3);
        TaskResultSet taskResultSet = context.getTaskResultSet();
        for (TaskResultSet.Item resultItem : taskResultSet) {
            System.out.println(resultItem);
        }
        return ProcessResult.success();
    }

}
