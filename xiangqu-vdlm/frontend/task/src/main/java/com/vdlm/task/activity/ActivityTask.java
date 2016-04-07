package com.vdlm.task.activity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vdlm.service.activity.ActivityService;

@Component
public class ActivityTask {

    @Autowired
    private ActivityService activityService;

    @Scheduled(cron = "0 */2 * * * ?")
    public void autoUpdateActivityStatus() {
    	
    	// 开启活动主表状态
    	activityService.updateStatusToInProgress(); 
    	
    	// 更新活动ticket, 改价
        activityService.batchUpdateStatusToInProgressPrivate();
        activityService.batchUpdateStatusToInProgressPublic();
        
        // 关闭活动主表状态
        activityService.updateStatusToClosed();  
        
    	// 关闭活动ticket, 价格还原
        activityService.batchUpdateStatusInProgressToClosed();
        
        // 异常活动 还未启动但已过了结束时间的活动
        activityService.batchUpdateStatusNotStartedToClosed(); 
    }
}
