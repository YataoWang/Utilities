package com.src;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AsyncGenericTask<T> implements IWorkerEvent {
  private static final int DEFAULT_MAX_TASKS = 10000;
  private boolean _running = true;
  private ITaskEvent _sink;
  private IWorker _worker;
  private Queue<T> _mainTasks;
  private Queue<T> _pendingTasks;
  private final int _maxTaskNum;
  private final WorkerPolicy _policy;
  private final Logger _logger = Logger.getLogger("AsyncGenericTask");

  public AsyncGenericTask() {
    this(WorkerPolicy.BLOCK);
  }

  public AsyncGenericTask(WorkerPolicy policy) {
    this(policy, DEFAULT_MAX_TASKS);
  }

  public AsyncGenericTask(WorkerPolicy policy, int maxTasks) {
    this(policy, maxTasks, null);
  }

  public AsyncGenericTask(WorkerPolicy policy, int maxTasks, ITaskEvent sink) {
    this._sink = sink;
    this._mainTasks = new ArrayDeque<T>();
    this._pendingTasks = new ArrayDeque<T>();
    this._maxTaskNum = maxTasks;
    this._policy = policy;
  }

  public void setSink(ITaskEvent sink) {
    this._sink = sink;
  }

  public void initialize() throws Exception {
    if (null != this._sink) {
      this._sink.fireInitialize();
    }
    this._running = true;
    this._worker = WorkerPool.getInstance().acquireWorker(this);
  }

  public void shutdown() {
    this._running = false;
    WorkerPool.getInstance().releaseWorker(this._worker);
    if (null != this._sink) {
      this._sink.fireFinalize();
    }
  }

  public void addTask(T task) {
    if (this._mainTasks.size() < this._maxTaskNum) {
      this._mainTasks.add(task);
    } else {
      switch (this._policy) {
        case ABORT: {
          throw new RuntimeException(this._policy + "\r\nPlease increase the maximum task value.");
        }
        case DISCARD: {
          // DO nothing
          break;
        }
        case BLOCK: {
          this._pendingTasks.add(task);
          break;
        }
        default:
          throw new RuntimeException("Invalid worker policy.");
      }
    }
  }

  @Override
  public void runWorker() {
    WorkerState state = this._worker.getState();
    if (WorkerState.RUNNING != state) {
      return;
    }

    while (this._running) {
      if (this._mainTasks.size() > 0) {
        T task = this._mainTasks.poll();
        if (null != this._sink) {
          try {
            this._sink.fireTask(task);
          } catch (Exception ex) {
            this._logger.log(Level.SEVERE, ex.getMessage(), ex);
            if (this._running) {
              continue;
            } else {
              break;
            }
          }
        }
      }

      while (this._pendingTasks.size() > 0 && this._mainTasks.size() < this._maxTaskNum) {
        this._mainTasks.add(this._pendingTasks.poll());
      }
    }
  }
}
