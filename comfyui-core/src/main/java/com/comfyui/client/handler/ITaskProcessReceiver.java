package com.comfyui.client.handler;

import com.comfyui.entity.*;

/**
 * ComfyUI WebSocket策略者处理结果接收者
 */
public interface ITaskProcessReceiver {

    /**
     * 任务开始
     *
     * @param start 任务开始信息
     */
    void taskStart(ComfyUITaskStart start);

    /**
     * 当前执行的节点和节点执行进度
     *
     * @param progress 进度信息
     */
    void taskNodeProgress(ComfyUITaskNodeProgress progress);

    /**
     * 任务进度预览效果图
     *
     * @param preview 预览图信息
     */
    void taskProgressPreview(ComfyUITaskProgressPreview preview);

    /**
     * 任务输出的图片
     *
     * @param output 输出信息
     */
    void taskOutput(ComfyUITaskOutput output);

    /**
     * 任务完成
     *
     * @param complete 任务完成信息
     */
    void taskComplete(ComfyUITaskComplete complete);

    /**
     * 任务失败
     *
     * @param error 任务错误信息
     */
    void taskError(ComfyUITaskError error);

    /**
     * 绘图队列任务个数更新
     *
     * @param taskNumber 队列任务信息
     */
    void taskNumberUpdate(ComfyUITaskNumber taskNumber);

    /**
     * 当前系统负载
     *
     * @param performance 系统状态
     */
    void systemPerformance(ComfyUISystemPerformance performance);
}