package com.src;

import java.util.Vector;

public class WorkerPool {
  private final IncrementNum _incrementNum;
  private final int _minThreadSize;
  private final int _maxThreadSize;
  private final WorkerPolicy _workerPolicy;
  private Vector<Worker> _workers;

  public WorkerPool(int maxThreadSize, WorkerPolicy workerPolicy) {
    this._incrementNum = new IncrementNum();
    this._minThreadSize = Runtime.getRuntime().availableProcessors() * 2;
    this._maxThreadSize = maxThreadSize;
    this._workerPolicy = workerPolicy;
    this._workers = new Vector<Worker>();
    initWorker();
  }

  public synchronized IWorker acquireWorker(IWorkerEvent event) throws Exception {
    return acquireWorker(event, WorkerPolicy.BLOCK);
  }

  public synchronized IWorker acquireWorker(IWorkerEvent event, WorkerPolicy policy) throws Exception {
    Worker worker = null;
    for (int i = 0; i < this._workers.size(); i++) {
      worker = this._workers.get(i);
      if (worker.idle()) {
        worker.setSink(event);
        break;
      }
    }

    if (null == worker) {
      int currentSize = this._workers.size();
      if (currentSize < this._maxThreadSize) {
        worker = createWorker();
        worker.setSink(event);
        this._workers.add(worker);
      } else {
        throw new Exception("There isn't idle worker.");
      }
    }

    return worker;
  }

  public synchronized void releaseWorker(Worker worker) throws Exception {
    if (null == worker) {
      return;
    }

    worker.stop();
  }

  private void initWorker() {
    for (int i = 0; i < this._minThreadSize; i++) {
      this._workers.add(createWorker());
    }
  }

  private Worker createWorker() {
    return new Worker("Thread-" + this._incrementNum.getNextNumber(), this._workerPolicy, null);
  }
}
