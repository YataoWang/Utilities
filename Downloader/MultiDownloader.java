package com.wyt.downloader;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

public class MultiDownloader implements IMultiDownloader {
  private int id;
  private final int threadNum;
  private IDownloadEvent sink;
  private DownloaderMgr downloaderMgr;

  public MultiDownloader(int threadNum) {
    this(threadNum, null);
  }

  public MultiDownloader(int threadNum, IDownloadEvent sink) {
    this.threadNum = threadNum;
    this.sink = sink;
    this.downloaderMgr = new DownloaderMgr();
  }

  public void setSink(IDownloadEvent sink) {
    this.sink = sink;
  }

  public void download(String url, File target) throws Exception {
    Utils.ensureExist(target);

    long totalSize = getDownloadSize(url);
    if (totalSize <= 0) {
      return;
    }

    long from = 0;
    long size = totalSize / this.threadNum + 1;
    for (int i = 0; i < this.threadNum; i++, from += size) {
      Downloader downloader = new Downloader()
              .setId(getId())
              .setFrom(from)
              .setTo(from + size)
              .setUrl(url)
              .setTarget(target)
              .setPerDownSize(Const.PER_DOWN_SIZE)
              .setSink(this)
              .ready();
      this.downloaderMgr.addDownloader(downloader);

      downloader.start();
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

  private void doRetry(int id) {
    Downloader downloader = this.downloaderMgr.getDownloader(id);
    if (downloader.getHasRetry() < Const.MAX_RETRY_COUNTER) {
      downloader.retry();
    }
  }

  @Override
  public void fireStarting(int id) {
    if (null != this.sink) {
      this.sink.fireStarting(id);
    }
  }

  @Override
  public void fireRetrying(int id) {
    if (null != this.sink) {
      this.sink.fireRetrying(id);
    }
  }

  @Override
  public void fireTransfer(int id, int size) {
    if (null != this.sink) {
      this.sink.fireTransfer(id, size);
    }
  }

  @Override
  public void fireEnding(int id) {
    if (null != this.sink) {
      this.sink.fireEnding(id);
    }
  }

  @Override
  public void fireError(int id, String message) {
    if (null != this.sink) {
      this.sink.fireError(id, message);
    }

    doRetry(id);
  }
}
