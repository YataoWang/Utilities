package com.wyt.downloader.test;

import com.wyt.downloader.MultiDownloader;

import java.io.File;

public class DownloaderTest {
  public static void main(String[] args) throws Exception {

    Thread download = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          MultiDownloader downloader = new MultiDownloader(1);
          //downloader.download("https://downloads.apache.org/tomcat/tomcat-9/v9.0.44/bin/apache-tomcat-9.0.44.tar.gz", new File("C:\\Users\\TerranW\\Desktop\\apache-tomcat-9.0.44.tar.gz"));
          downloader.download("https://img0.baidu.com/it/u=1729283273,1790152810&fm=26&fmt=auto&gp=0.jpg", new File("C:\\Users\\TerranW\\Pictures\\1.jpg"));
        } catch (Exception ex) {
          ex.printStackTrace();
        }
      }
    });
    download.start();
    download.join();
  }
}
