package com.example.demo.processEventsImplementations;

import com.example.demo.alertConfigs.AlertConfigItem;
import com.example.demo.dispatchConfigs.DispatchStrategy;
import com.example.demo.interfaces.ProcessAlertEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.PriorityBlockingQueue;

@Component
public class ProcessSlidingWindow implements ProcessAlertEvents {

    private PriorityBlockingQueue<DispatchStrategy> alertConfigPriorityBlockingQueue;
    private static final Logger logger = LogManager.getLogger(ProcessSlidingWindow.class);

    ProcessSlidingWindow(){
        this.alertConfigPriorityBlockingQueue = new PriorityBlockingQueue<>();
    }

    @Override
    public void enqueueAlertConfig(AlertConfigItem alertConfigItem){
        alertConfigItem.getDispatchStrategyList().stream().forEach(dispatchStrategy -> {
            dispatchStrategy.setTimestamp(LocalDateTime.now());
            this.alertConfigPriorityBlockingQueue.add(dispatchStrategy);
        });
    }

    @Override
    public void processAlertConfigs(){
        while(!this.alertConfigPriorityBlockingQueue.isEmpty()){
            processDispatchStrategy(this.alertConfigPriorityBlockingQueue.remove());
        }
    }

    private void processDispatchStrategy(DispatchStrategy dispatchStrategy) {
        switch(dispatchStrategy.getType()){
            case CONSOLE:
                logConsoleDispatch(dispatchStrategy);
                break;
            case EMAIL:
                logEmailDispatch();
                break;
        }
    }

    private void logConsoleDispatch(DispatchStrategy dispatchStrategy) {
        logger.info("[INFO] AlertingService: \u001B[1mDispatching to Console\u001B[0m");
        logger.warn("[WARN] Alert: \u001B[1m`" + dispatchStrategy.getMessage() + "`\u001B[0m");
    }

    private void logEmailDispatch() {
        logger.info("[INFO] AlertingService: \u001B[1mDispatching an Email\u001B[0m");
    }

}
