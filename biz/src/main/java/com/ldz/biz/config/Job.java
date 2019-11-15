package com.ldz.biz.config;

import com.ldz.biz.service.BizLcClService;
import com.ldz.biz.service.ZgjbxxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Job  {


    @Autowired
    private BizLcClService lcClService;

    @Autowired
    private ZgjbxxService zgjbxxService;

    /**
     * 清除训练车安全员分配数据
     */
//    @Scheduled(cron="0 0 0 * * ? ")
    @Scheduled(cron="0 0 1 * * ? ")
    public void clearCarAllot(){
        System.out.println("清除训练车安全员分配数据任务开始执行");
        lcClService.clearCarAllot();
        System.out.println("清除训练车安全员分配数据任执行结束");
    }

    /**
     * 每天2点清除人事表中安全员签到状态
     */
    @Scheduled(cron="0 0 2 * * ? ")    //表示每天2时执行
    public void clearAqyQd(){
        System.out.println("清除人事表中安全员签到状态任务开始执行");
        zgjbxxService.clearAqyQd();
        System.out.println("清除人事表中安全员签到状态任执行结束");
    }
    @Scheduled(cron="0 0 1 * * ?  ")
    public void reSetClZt(){
        System.out.println("重置车辆状态任务开始");
        lcClService.clearClZt();
        System.out.println("重置车辆状态任务结束");
    }

}
