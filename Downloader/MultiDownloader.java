package com.wyt.downloader;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MultiDownloader {
  private int id;
  private final int threadNum;
  private IDownloadEvent sink;

  public MultiDownloader(int threadNum) {
    this(threadNum, null);
  }

  public MultiDownloader(int threadNum, IDownloadEvent sink) {
    this.threadNum = threadNum;
    this.sink = sink;
  }

  public void setSink(IDownloadEvent sink) {
    this.sink = sink;
  }

  public void download(String url, File target) throws Exception {
    long totalSize = getDownloadSize(url);
    if (totalSize <= 0) {
      return;
    }

    Utils.ensureExist(target);

    DownloadEvent event = new DownloadEvent();
    long size = totalSize / this.threadNum + 1;
    long from = 0;
    for (int i = 0; i < this.threadNum; i++) {
      new Downloader()
              .setId(getId())
              .setFrom(from)
              .setTo(from + size)
              .setUrl(url)
              .setTarget(target)
              .setPerDownSize(Const.PER_DOWN_SIZE)
              .setSink(event)
              .ready()
              .start();
      from += size;
    }
  }

  public void pause() throws Exception {

  }

  public void resume() throws Exception {

  }

  public void cancel() throws Exception {

  }

  // 0 - 100
  public int getProgress() {
    // TODO
    return 0;
  }

  private long getDownloadSize(String url) throws IOException {
    HttpURLConnection conn = null;
    try {
      conn = (HttpURLConnection) (new URL(url)).openConnection();
      conn.setRequestProperty("connection", "keep-alive");
      conn.setRequestProperty("accept", "*/*");
      conn.setRequestMethod(Const.HTTP_GET);
      return conn.getContentLengthLong();
    } finally {
      if (null != conn) {
        conn.disconnect();
      }
    }
  }

  private synchronized int getId() {
    int id = ++this.id;
    if (this.id > Short.MAX_VALUE) {
      this.id = 0;
    }

    return id;
  }
}

class DownloadEvent implements IDownloadEvent {

  @Override
  public void fireStarting(int id) {
    System.out.println("fireStarting -> " + id);
  }

  @Override
  public void fireTransfer(int id, int size) {
    System.out.println("fireTransfer -> " + id + " , transfer -> " + size);
  }

  @Override
  public void fireEnding(int id) {
    System.out.println("fireEnding -> " + id);
  }

  @Override
  public void fireError(int id, String message) {
    System.out.println("fireEnding -> " + id + " , error -> " + message);
  }
}
