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

  public synchronized Worker acquireWorker(IWorkerEvent event) throws Exception {
    Worker worker = null;
    for (int i = 0; i < this._workers.size(); i++) {
      worker = this._workers.get(i);
      if (worker.isIdle()) {
        worker.setSink(event);
        break;
      }
    }


    // TODO
    return worker;
  }


  public synchronized void releaseWorker(Worker worker) throws Exception {
    if (null == worker) {
      return;
    }

    worker.close();
  }

  private void initWorker() {
    for (int i = 0; i < this._minThreadSize; i++) {
      this._workers.add(new Worker("Thread-" + this._incrementNum.getNextNumber()));
    }
  }
}
