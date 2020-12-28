package com.wyt.downloader;

import java.util.ArrayList;
import java.util.List;

class DownloaderMgr {
  private final List<Downloader> downloaderList;

  public DownloaderMgr() {
    this.downloaderList = new ArrayList<Downloader>();
  }

  public void addDownloader(Downloader downloader) {
    this.downloaderList.add(downloader);
  }

  public Downloader getDownloader(int id) {
    for (int i = 0; i < this.downloaderList.size(); i++) {
      Downloader downloader = this.downloaderList.get(i);
      if (id == downloader.getId()) {
        return downloader;
      }
    }

    return null;
  }
}
