package com.wyt.threadpool;

public enum WorkerState {
  READYING,
  STARTING,
  RUNNING,
  STOPPING,
  STOPPED,
}
