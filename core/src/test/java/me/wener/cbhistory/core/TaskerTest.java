package me.wener.cbhistory.core;

import java.util.Timer;
import java.util.TimerTask;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Ignore
public class TaskerTest
{
    @Test
    public void test()
    {

    }

    @Test
    public void testHandler() throws InterruptedException
    {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("running...");
            }
        }, 1000, 1000);
        Thread.sleep(3000);
    }
}
