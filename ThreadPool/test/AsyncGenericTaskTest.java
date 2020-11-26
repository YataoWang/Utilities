package com.wyt.threadpool.test;

import com.wyt.threadpool.AsyncGenericTask;
import com.wyt.threadpool.ITaskEvent;
import com.wyt.threadpool.WorkerPolicy;

public class AsyncGenericTaskTest {
  public static void main(String[] args){
    AsyncGenericTask<String> task = new AsyncGenericTask<String>();
    try {
      AsyncGenericTask.TaskConfiguration conf = new AsyncGenericTask.TaskConfiguration();
      conf.loggerName = "test";
      conf.logLevel = AsyncGenericTask.TaskLoggerLevel.ALL;
      conf.policy = WorkerPolicy.BLOCK;
      conf.sink = new MyTaskEvent();
      task.initialize(conf);
      for (int i = 0; i < 100000; i++) {
        task.addTask((i + 1) + "");
      }

      Thread.sleep(15 * 1000);
    }catch (Exception ex) {
      ex.printStackTrace();
    } finally {
      task.shutdown();
    }
  }
}

class MyTaskEvent implements ITaskEvent<String> {
  @Override
  public void fireInitialize() throws Exception {
    System.out.println("fireInitialize");
  }

  @Override
  public void fireTask(String task) throws Exception {
    System.out.println("fireTask -> " + task);
  }

  @Override
  public void fireFinalize() {
    System.out.println("fireFinalize");
  }
}