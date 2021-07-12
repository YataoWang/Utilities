package com.wyt.downloader.test;

import com.wyt.downloader.IDownloadEvent;
import com.wyt.downloader.MultiDownloader;

import java.io.File;

public class DownloaderTest {
  public static void main(String[] args) throws Exception {

    Thread download = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          MultiDownloader downloader = new MultiDownloader(1, new DownloadEvent());
          //downloader.download("https://downloads.apache.org/tomcat/tomcat-9/v9.0.44/bin/apache-tomcat-9.0.44.tar.gz", new File("C:\\Users\\TerranW\\Desktop\\apache-tomcat-9.0.44.tar.gz"));
          downloader.download("https://img0.baidu.com/it/u=1247506875,2919233442&fm=26&fmt=auto&gp=0.jpg", new File("C:\\Users\\TerranW\\Desktop\\1.jpg"));
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    });
    download.start();
    download.join();
  }

  static class DownloadEvent implements IDownloadEvent {

    @Override
    public void fireStarting(int threadId) {
      System.out.println("fireStarting -> " + threadId);
    }

    @Override
    public void fireRetrying(int threadId) {
      System.out.println("fireRetrying -> " + threadId);
    }

    @Override
    public void fireTransfer(int threadId, int size) {
      System.out.println("fireTransfer -> " + threadId + ", transfer -> " + size);
    }

    @Override
    public void fireEnding(int threadId) {
      System.out.println("fireEnding -> " + threadId);
    }

    @Override
    public void fireError(int threadId, String message) {
      System.err.println("fireError -> " + threadId);
    }
  }
}
