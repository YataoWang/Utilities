package com.wyt.downloader;

public interface IDownloadEvent {
  public void fireStarting(int threadId);
  public void fireRetrying(int threadId);
  public void fireTransfer(int threadId, int size);
  public void fireEnding(int threadId);
  public void fireError(int threadId, String message);
}
