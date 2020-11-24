package com.src.test;

import com.src.AsyncGenericTask;
import com.src.ITaskEvent;
import com.src.WorkerPolicy;

public class AsyncGenericTaskTest {
  public static void main(String[] args){
    AsyncGenericTask<String> task = new AsyncGenericTask<String>(WorkerPolicy.BLOCK);
    try {
      task.setSink(new MyTaskEvent());
      task.initialize();
      for (int i = 0; i < 200000; i++) {
        task.addTask((i + 1) + "");
      }

      Thread.sleep(10 * 1000);
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