package com.src;

public class Worker implements IWorker, Runnable {
  private IWorkerEvent sink;
  private WorkerState state;
  private WorkerPolicy policy;
  private Thread workThread;

  public Worker(String name, IWorkerEvent sink) {
    this(name, WorkerPolicy.BLOCK, sink);
  }

  public Worker(String name, WorkerPolicy policy, IWorkerEvent sink) {
    this.sink = sink;
    this.state = WorkerState.READYING;
    this.policy = policy;
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
    if (this.state != WorkerState.RUNNING) {
      return;
    }

    try {
      this.state = WorkerState.STOPPING;
      this.workThread.interrupt();
    } finally {
      // TODO, check with the run method final block
      this.state = WorkerState.STOPPED;
    }
  }

  @Override
  public boolean idle() {
    return (this.state == WorkerState.READYING || this.state == WorkerState.STOPPED);
  }

  @Override
  public void run() {
    if (null == this.sink) {
      return;
    }

    try {
      this.state = WorkerState.RUNNING;
      this.sink.runWorker();
    } finally {
      this.state = WorkerState.STOPPED;
    }
  }
}
