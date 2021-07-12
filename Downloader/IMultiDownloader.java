package utilities.downloader;

public interface IMultiDownloader {
  public void fireStarting(int id);
  public void fireRetrying(int id);
  public void fireTransfer(int id, int size);
  public void fireEnding(int id);
  public void fireError(int id, String message);
}
