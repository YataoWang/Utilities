package com.src;

import java.util.Vector;

public class WorkerPool {
  private final int _minThreadSize;
  private final int _maxThreadSize;
  private final WorkerPolicy _workerPolicy;
  private Vector<Worker> _workers;

  public WorkerPool(int minThreadSize, int maxThreadSize, WorkerPolicy workerPolicy) {
    this._minThreadSize = minThreadSize;
    this._maxThreadSize = maxThreadSize;
    this._workerPolicy = workerPolicy;
    this._workers = new Vector<Worker>();
  }

  public Worker acquireWorker() throws Exception {

  }
}
