package com.wyt.threadpool;

public class Worker implements IWorker, Runnable {
  private IWorkerEvent sink;
  private WorkerState state;
  private Thread workThread;

  public Worker(String name) {
    this(name, null);
  }

  public Worker(String name, IWorkerEvent sink) {
    this.sink = sink;
    this.state = WorkerState.READYING;
    this.workThread = new Thread(this, name);
  }

  public void setSink(IWorkerEvent sink) {
    this.sink = sink;
  }

  @Override
  public void start() {
    // Only READYING and STOPPED state can run
    if (this.state != WorkerState.READYING && this.state != WorkerState.STOPPED) {
      return;
    }

    this.state = WorkerState.STARTING;
    this.workThread.start();
  }

  @Override
  public void stop() {
    this.state = WorkerState.STOPPING;
    try {
      this.workThread.interrupt();
      this.workThread.join();
    } catch (Exception ex) {
      ex.printStackTrace();
    } finally {
      // TODO, check with the run method final block
      this.state = WorkerState.STOPPED;
    }
  }

  @Override
  public WorkerState getState() {
    return this.state;
  }

  public boolean idle() {
    return (this.state == WorkerState.READYING || this.state == WorkerState.STOPPED);
  }

  @Override
  public void run() {
    this.state = WorkerState.RUNNING;
    if (null != this.sink) {
      this.sink.runWorker();
    }
  }
}
