package com.src;

public interface ITaskEvent<T> {
  public void fireInitialize() throws Exception;
  public void fireTask(T task) throws Exception;
  public void fireFinalize();
}
