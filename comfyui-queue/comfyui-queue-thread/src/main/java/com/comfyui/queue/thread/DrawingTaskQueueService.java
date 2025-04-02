package com.comfyui.queue.thread;

import com.comfyui.queue.common.DrawingTaskExecutor;
import com.comfyui.queue.common.DrawingTaskInfo;
import com.comfyui.queue.common.IDrawingTaskSubmit;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 绘图任务队列管理者
 *
 * @author Sun_12138
 */
@Slf4j
public class DrawingTaskQueueService implements IDrawingTaskSubmit {
    private static final int MAX_RETRIES = 3; // 最大重试次数
    private final ExecutorService executorService;
    /**
     * 任务队列
     */
    private final ArrayDeque<DrawingTaskInfo> taskQueue;

    /**
     * 绘图任务执行者
     */
    private final DrawingTaskExecutor taskExecutor;
    private final AtomicInteger retryCount = new AtomicInteger(0); // 当前线程的重试计数器

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
        if (retryCount.get() >= MAX_RETRIES) { // 原子读取
            log.error("达到最大重试次数({}), 停止任务处理", MAX_RETRIES);
            return;
        }
        executorService.execute(() -> {
            try {
                processTaskQueue();
            } catch (Exception e) {
                handleException(e);
            }
        });
    }

    /**
     * 处理任务队列
     */
    private void processTaskQueue() {
        while (true) {
            try {
                synchronized (taskQueue) {
                    // 取出并执行任务
                    DrawingTaskInfo taskInfo = taskQueue.poll();
                    if (taskInfo == null) continue;
                    taskExecutor.execDrawingTask(
                            taskInfo.getTaskId(),
                            taskInfo.getFlow(),
                            taskInfo.getTimeout(),
                            taskInfo.getUnit()
                    );
                }
            } catch (Exception e) {
                handleException(e);
                break;
            }
        }
    }

    private void handleException(Exception e) {
        log.error("任务处理异常", e);
        int current = retryCount.incrementAndGet(); // 原子递增
        if (current < MAX_RETRIES) {
            int delay = (int) Math.pow(2, current) * 1000;
            try {
                Thread.sleep(delay);
            } catch (InterruptedException interrupt) {
                Thread.currentThread().interrupt();
            }
            startTaskProcessing();
        } else {
            log.error("达到最大重试次数({}), 停止任务处理", MAX_RETRIES);
        }
    }

}
