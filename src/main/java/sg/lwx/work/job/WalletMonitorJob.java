package sg.lwx.work.job;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class WalletMonitorJob {


    @XxlJob("ethScanTransMonitor")
    public void ethScanTransMonitor() {
        try {
// todo
        } catch (Exception e) {

        }
    }


    public static void main(String[] args) {
        LocalDateTime now = LocalDateTime.now();
        var secondsPerDay = 60*60*24;
        System.out.println("secondsPerDay= "+secondsPerDay);

        var ethBlock = 13;
        var blockNumberPerDay = secondsPerDay/ethBlock;
        System.out.println("blockNumberPerDay= "+blockNumberPerDay);

       var blockNumberPerMonth = blockNumberPerDay * 31;






    }




}
