package com.comfyui.annotation.enums;

import com.comfyui.common.process.ComfySystemPerformance;
import com.comfyui.common.process.ComfyTaskComplete;
import com.comfyui.common.process.ComfyTaskError;
import com.comfyui.common.process.ComfyTaskNodeProgress;
import com.comfyui.common.process.ComfyTaskNumber;
import com.comfyui.common.process.ComfyTaskOutput;
import com.comfyui.common.process.ComfyTaskProgressPreview;
import com.comfyui.common.process.ComfyTaskStart;
import com.comfyui.common.process.IComfyTaskProcess;
import com.comfyui.common.process.QueneNumber;
import lombok.Getter;

/**
 * 绘画任务进度类型
 * @author Sun_12138
 */
@Getter
public enum TaskProcessType {
    /**
     * 任务开始
     */
    START(ComfyTaskStart.class),
    /**
     * 进度更新
     */
    PROGRESS(ComfyTaskNodeProgress.class),
    /**
     * 预览图
     */
    PREVIEW(ComfyTaskProgressPreview.class),
    /**
     * 任务输出
     */
    OUTPUT(ComfyTaskOutput.class),
    /**
     * 任务完成
     */
    COMPLETE(ComfyTaskComplete.class),
    /**
     * 任务失败
     */
    ERROR(ComfyTaskError.class),
    /**
     * 队列数量更新
     */
    NUMBER_UPDATE(ComfyTaskNumber.class),
    /**
     * 系统状态更新
     */
    SYSTEM_PERFORMANCE(ComfySystemPerformance.class),
    /**
     * 队列数量更新
     */
    QUEUE_NUMBER_UPDATE(QueneNumber.class);

    /**
     * 对应的实体类类型
     */
    private final Class<? extends IComfyTaskProcess> entityClass;

    TaskProcessType(Class<? extends IComfyTaskProcess> entityClass) {
        this.entityClass = entityClass;
    }
}
