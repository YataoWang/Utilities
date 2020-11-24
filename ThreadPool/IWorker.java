package com.src;

public interface IWorker {
  public void start();
  public void stop();
  public WorkerState getState();
}
