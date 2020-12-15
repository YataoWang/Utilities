package com.wyt.downloader.test;

import com.wyt.downloader.MultiDownloader;

import java.io.File;

public class DownloaderTest {
  public static void main(String[] args) throws Exception {

    Thread download = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          MultiDownloader downloader = new MultiDownloader(20);
          downloader.download("https://downloads.apache.org/tomcat/tomcat-9/v9.0.41/bin/apache-tomcat-9.0.41.tar.gz", new File("C:\\Users\\TerranW\\Desktop\\apache-tomcat-9.0.41.tar.gz"));
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    });
    download.start();
    download.join();
  }
}
