package com.wyt.downloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

class Downloader implements Runnable {
  private int id;
  private long from = 0;
  private long to;
  private String url;
  private File target;
  private int perDownSize;
  private IMultiDownloader sink;
  private volatile DownloadStatus status;
  private long hasDown = 0;
  private int hasRetry = 0;

  public int getId() {
    return this.id;
  }

  public Downloader setId(int id) {
    this.id = id;
    return this;
  }

  public Downloader setFrom(long from) {
    this.from = from;
    return this;
  }

  public Downloader setTo(long to) {
    this.to = to;
    return this;
  }

  public Downloader setUrl(String url) {
    this.url = url;
    return this;
  }

  public Downloader setTarget(File file) {
    this.target = file;
    return this;
  }

  public Downloader setPerDownSize(int perDownSize) {
    this.perDownSize = perDownSize;
    return this;
  }

  public Downloader setSink(IMultiDownloader sink) {
    this.sink = sink;
    return this;
  }

  public Downloader ready() {
    this.status = DownloadStatus.READYING;
    return this;
  }

  public int getHasRetry() {
    return this.hasRetry;
  }

  public void start() {
    if (this.status != DownloadStatus.READYING) {
      return;
    }

    this.status = DownloadStatus.STARTING;
    startThread();
    fireStarting();
  }

  public void retry() {
    if (this.status != DownloadStatus.STARTING) {
      return;
    }

    ++this.hasRetry;
    this.from = this.hasDown;
    this.status = DownloadStatus.RETRY;
    startThread();
    fireRetrying();
  }

  public void pause() {
  }

  public void resume() {
  }

  public void cancel() {
  }

  @Override
  public void run() {
    process();
  }

  private void startThread() {
    new Thread(this).start();
  }

  private void process() {
    HttpURLConnection connection = null;
    InputStream inputStream = null;
    RandomAccessFile writeFile = null;
    try {
      connection = getHttpConnection();
      inputStream = connection.getInputStream();
      writeFile = new RandomAccessFile(this.target, "rw");
      writeFile.seek(getSeekPos());

      byte[] buffer = new byte[this.perDownSize];
      int read;
      while ((read = inputStream.read(buffer, 0, buffer.length)) > 0) {
        writeFile.write(buffer, 0, read);
        this.hasDown += read;
        fireTransfer(read);
      }

      fireEnding();
    } catch (Exception ex) {
      fireError(Utils.getErrorMessage(ex));
    } finally {
      if (null != writeFile) {
        try {
          writeFile.close();
        } catch (Exception ex) {;}
      }
      if (null != inputStream) {
        try {
          inputStream.close();
        } catch (Exception ex) {;}
      }
      if (null != connection) {
        connection.disconnect();
      }
    }
  }

  private HttpURLConnection getHttpConnection() throws IOException {
    HttpURLConnection connection = (HttpURLConnection) (new URL(this.url)).openConnection();
    connection.setRequestProperty("Range", "bytes=" + this.from + "-" + this.to);
    connection.setRequestProperty("connection", "keep-alive");
    connection.setRequestProperty("accept", "*/*");
    connection.setRequestMethod(Const.HTTP_GET);
    connection.setConnectTimeout(Const.TIME_OUT);
    connection.setReadTimeout(Const.TIME_OUT);
    return connection;
  }

  private long getSeekPos() {
    if (this.status == DownloadStatus.RETRY) {
      return this.hasDown;
    } else {
      return this.from;
    }
  }

  private void fireStarting() {
    if (null != this.sink) {
      this.sink.fireStarting(this.id);
    }
  }

  private void fireRetrying() {
    if (null != this.sink) {
      this.sink.fireRetrying(this.id);
    }
  }

  private void fireTransfer(int size) {
    if (null != this.sink) {
      this.sink.fireTransfer(this.id, size);
    }
  }

  private void fireEnding() {
    if (null != this.sink) {
      this.sink.fireEnding(this.id);
    }
  }

  private void fireError(String message) {
    if (null != this.sink) {
      this.sink.fireError(this.id, message);
    }
  }
}
