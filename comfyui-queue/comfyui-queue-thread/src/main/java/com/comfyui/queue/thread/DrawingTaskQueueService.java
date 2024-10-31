package com.comfyui.queue.thread;

import com.comfyui.common.entity.ComfyWorkFlow;
import com.comfyui.queue.common.DrawingTaskExecutor;
import com.comfyui.queue.common.DrawingTaskInfo;
import com.comfyui.queue.common.IDrawingTaskSubmit;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 绘图任务队列管理者
 *
 * @author Sun_12138
 */
@Slf4j
public class DrawingTaskQueueService implements IDrawingTaskSubmit {
    private final ExecutorService executorService;

    /**
     * 任务队列
     */
    private final ArrayDeque<DrawingTaskInfo> taskQueue;

    /**
     * 绘图任务执行者
     */
    private final DrawingTaskExecutor taskExecutor;

    /**
     * @param taskExecutor 绘图任务执行者
     */
    public DrawingTaskQueueService(DrawingTaskExecutor taskExecutor) {
        this.executorService = Executors.newSingleThreadExecutor();
        this.taskQueue = new ArrayDeque<>();
        this.taskExecutor = taskExecutor;
        this.startTaskProcessing();
    }

    /**
     * 提交任务
     *
     * @param taskInfo 任务信息
     * @return 是否成功
     */
    @Override
    public boolean submit(DrawingTaskInfo taskInfo) {
        if ("prepend".equals(taskInfo.getJoinType())) {
            return taskQueue.offerFirst(taskInfo);
        } else {
            return taskQueue.offer(taskInfo);
        }
    }

    @Override
    public void clear() {
        taskQueue.clear();
    }

    /**
     * 开启任务线程
     */
    private void startTaskProcessing() {
        executorService.execute(this::processTaskQueue);
    }

    /**
     * 处理任务队列
     */
    private void processTaskQueue() {
        while (true) {
            try {
                synchronized (taskQueue) {
                    DrawingTaskInfo taskInfo = taskQueue.peek();
                    if (taskInfo == null) {
                        continue;
                    }
                    String taskId = taskInfo.getTaskId();
                    ComfyWorkFlow flow = taskInfo.getFlow();
                    long timeout = taskInfo.getTimeout();
                    TimeUnit unit = taskInfo.getUnit();
                    //执行绘图任务
                    taskExecutor.execDrawingTask(taskId, flow, timeout, unit);
                    //出队该任务
                    taskQueue.removeFirst();
                }
            } catch (Exception e) {
                // 重置中断状态
                Thread.currentThread().interrupt();
                //重启线程
                this.startTaskProcessing();
                log.error("绘图任务线程发生中断, 重启线程");
                break;
            }
        }
    }
}
