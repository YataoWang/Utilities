package com.src;

public class Worker extends Thread {
  private IWorkerEvent sink;
  private WorkerState state;

  public Worker(String name) {
    this(name, null);
  }

  public Worker(String name, IWorkerEvent sink) {
    super(name);
    this.sink = sink;
    this.state = WorkerState.READYING;
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
    super.start();
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

  public void close() throws Exception {
    if (this.state != WorkerState.RUNNING) {
      return;
    }

    try {
      this.state = WorkerState.STOPPING;
      super.interrupt();
    } finally {
      // TODO, check with the run method final block
      this.state = WorkerState.STOPPED;
    }
  }

  public boolean isIdle() {
    return (this.state == WorkerState.READYING || this.state == WorkerState.STOPPED);
  }
}
