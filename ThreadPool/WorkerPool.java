package com.src;

import java.util.Vector;

/**
 * A Singleton class
 * Using {@link WorkerPool#getInstance()} to get instance
 */
public class WorkerPool {
  private static WorkerPool instance = new WorkerPool();
  private final IncrementNum _incrementNum;
  private final int _minThreadSize;
  private final int _maxThreadSize;
  private Vector<Worker> _workers;

  private WorkerPool() {
    this._incrementNum = new IncrementNum();
    this._minThreadSize = Runtime.getRuntime().availableProcessors() * 2;
    this._maxThreadSize = this._minThreadSize * 100;
    this._workers = new Vector<Worker>();
    initWorker();
  }

  public static synchronized WorkerPool getInstance() {
    return instance;
  }

  /**
   * Acquire a {@link IWorker} instance and start it
   * @param event The event {@link IWorkerEvent} to be called by the worker thread
   * @return
   */
  public synchronized IWorker acquireWorker(IWorkerEvent event) {
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
        throw new RuntimeException("There isn't idle worker.");
      }
    }

    worker.start();
    return worker;
  }

  /**
   * Give back the {@link IWorker} instance and stop it
   * @param worker
   */
  public synchronized void releaseWorker(IWorker worker) {
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
    return new Worker("WorkerPool Thread-" + this._incrementNum.getNextNumber());
  }
}
