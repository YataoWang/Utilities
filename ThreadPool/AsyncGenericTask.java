package com.src;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AsyncGenericTask<T> implements IWorkerEvent {
  private final Object syncObj = new Object();
  private static final int DEFAULT_MAX_TASKS = 10000;
  private boolean _running;
  private ITaskEvent _sink;
  private int _maxTaskNum;
  private WorkerPolicy _policy;
  private IWorker _worker;
  private Queue<T> _mainTasks;
  private Queue<T> _pendingTasks;
  private Logger _logger;
  private Level _level;

  public AsyncGenericTask() {
    this(null);
  }

  public AsyncGenericTask(ITaskEvent sink) {
    this._sink = sink;
    this._mainTasks = new ArrayDeque<T>();
    this._pendingTasks = new ArrayDeque<T>();
  }

  public void setSink(ITaskEvent sink) {
    this._sink = sink;
  }

  /**
   * Initialize the {@link AsyncGenericTask} with {@link TaskConfiguration}, starting the task running thread. It does nothing if it's already initialized.
   * The task can be added by {@link #addTask(Object)} after {@link AsyncGenericTask} is initialized
   * @param conf The configuration value to initialize the {@link TaskConfiguration}. Must not ne null.
   * @throws Exception
   */
  public void initialize(TaskConfiguration conf) throws Exception {
    if (this._running) {
      return;
    }
    if (null == conf) {
      throw new Exception("Invalid configuration.");
    }

    this._policy = conf.policy;
    this._maxTaskNum = conf.maxTaskNum > 0 ? conf.maxTaskNum : DEFAULT_MAX_TASKS;
    this._sink = conf.sink;
    if (null != conf.loggerName && conf.loggerName.length() > 0) {
      this._level = getLevel(conf.logLevel);
      this._logger = Logger.getLogger(conf.loggerName);
      this._logger.setLevel(this._level);
    }

    this._running = true;
    if (null != this._sink) {
      this._sink.fireInitialize();
    }
    this._worker = WorkerPool.getInstance().acquireWorker(this);
  }

  /**
   * Shutdown the {@link AsyncGenericTask}, close the task thread
   */
  public void shutdown() {
    this._running = false;
    WorkerPool.getInstance().releaseWorker(this._worker);
    if (null != this._sink) {
      this._sink.fireFinalize();
    }
  }

  /**
   * Add task to main queue. If the main queue is full, doing the following policy
   * <ul>
   *   <li>{@link WorkerPolicy#ABORT}<br>
   *     Throw an exception
   *   </li>
   *   <li>{@link WorkerPolicy#DISCARD}<br>
   *     Discard the task
   *   </li>
   *   <li>{@link WorkerPolicy#BLOCK}<br>
   *     Add the task to pending queue, then move the task from pending queue to main queue if main queue executes a task
   *   </li>
   * </ul>
   * @param task
   */
  public void addTask(T task) {
    synchronized (this.syncObj) {
      if (this._mainTasks.size() < this._maxTaskNum) {
        this._mainTasks.add(task);
      } else {
        switch (this._policy) {
          case ABORT: {
            log(Level.SEVERE, this._policy + "\r\nPlease increase the maximum task value.");
            throw new RuntimeException();
          }
          case DISCARD: {
            log(Level.WARNING, this._policy + "\r\nPlease increase the maximum task value.");
            break;
          }
          case BLOCK: {
            this._pendingTasks.add(task);
            break;
          }
          default:
            log(Level.SEVERE, "Invalid worker policy.");
            throw new RuntimeException("Invalid worker policy.");
        }
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
      T task = null;
      synchronized (this.syncObj) {
        if (!this._mainTasks.isEmpty()) {
          task = this._mainTasks.remove();
        }
      }

      if (null != task) {
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

      synchronized (this.syncObj) {
        while (!this._pendingTasks.isEmpty() && this._mainTasks.size() < this._maxTaskNum) {
          this._mainTasks.add(this._pendingTasks.remove());
        }
      }
    }
  }

  private Level getLevel(TaskLoggerLevel level) {
    switch (level) {
      case ALL: {
        return Level.ALL;
      }
      case ERROR: {
        return Level.SEVERE;
      }
      case WARNING: {
        return Level.WARNING;
      }
      case INFO: {
        return Level.INFO;
      }
      case VERBOSE: {
        return Level.CONFIG;
      }
      case DEBUG: {
        return Level.FINE;
      }
      case OFF: {
        return Level.OFF;
      }
      default: {
        return Level.INFO;
      }
    }
  }

  private void log(Level level, String message) {
    log(level, message, null);
  }

  private void log(Level level, String message, Throwable ex) {
    if (null == this._logger) {
      return;
    }

    if (level.intValue() >= this._level.intValue()) {
      this._logger.log(level, message, ex);
    }
  }

  public static class TaskConfiguration {
    /**
     * @serial The worker's policy when the main task is full
     *
     * <ul>
     *
     * <li> {@link WorkerPolicy#BLOCK} It's default value
     * If the main task query is full, the added task will be added to pending query. Running a task from main queue, add a task from pending queue
     *
     * <li> {@link WorkerPolicy#DISCARD}
     * If the main task query is full, discard the added task
     *
     * <li> {@link WorkerPolicy#ABORT}
     * If the main task query is full, the current thread will throw an exception
     *
     * </ul>
     */
    public WorkerPolicy policy = WorkerPolicy.BLOCK;

    /**
     * @serial The maximum task count of the main queue.
     * If it does not specify this value, it uses {@link AsyncGenericTask#DEFAULT_MAX_TASKS}
     */
    public int maxTaskNum = 0;

    /**
     * @serial The logger name, it's used the {@link java.util.logging.Logger}
     */
    public String loggerName; // java.util.logging.Logger

    /**
     * @serial The logger level
     * The default value is {@link TaskLoggerLevel#INFO}
     */
    public TaskLoggerLevel logLevel = TaskLoggerLevel.INFO;

    /**
     * @serial The task's event
     */
    public ITaskEvent sink;
  }

  public enum TaskLoggerLevel {
    ALL {
      @Override
      public int valueOf() {
        return 0;
      }
    },
    ERROR {
      @Override
      public int valueOf() {
        return 1;
      }
    },
    WARNING {
      @Override
      public int valueOf() {
        return 2;
      }
    },
    INFO {
      @Override
      public int valueOf() {
        return 3;
      }
    },
    VERBOSE {
      @Override
      public int valueOf() {
        return 4;
      }
    },
    DEBUG {
      @Override
      public int valueOf() {
        return 5;
      }
    },
    OFF {
      @Override
      public int valueOf() {
        return Integer.MAX_VALUE;
      }
    };

    public abstract int valueOf();
  }
}
