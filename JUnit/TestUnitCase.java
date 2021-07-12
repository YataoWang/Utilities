package utilities.junit;

import utilities.common.Utilities;

public class TestUnitCase {
  public final String Id;
  public final String Name;
  private final String ClassName;
  private final String Description;
  private final long StartTime;
  private long EndTime;
  private String Result;
  private String Message;
  private Throwable InnerEx;

  public TestUnitCase(String Id, String Name, String ClassName, String Description) {
    this.Id = Id;
    this.Name = Name;
    this.ClassName = ClassName;
    this.Description = Description;
    this.StartTime = System.currentTimeMillis();
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(getUnitName())
      .append(" Success:").append(this.Result)
      .append(" Message:").append(getUnitMessage())
      .append(" Time:").append(getRunTime());
    return sb.toString();
  }

  public void setResult(String result) {
    this.Result = result;
  }

  public String getResult() {
    return this.Result;
  }

  public void setMessage(String message) {
    this.Message = message;
  }

  public void setException(Throwable ex) {
    this.InnerEx = ex;
  }

  public void setEndTime(long endTime) {
    this.EndTime = endTime;
  }

  public long getRunTime() {
    return this.EndTime - this.StartTime;
  }

  public String getUnitName() {
    return "[" + this.ClassName + "].[" + this.Name + "]";
  }

  public String getUnitMessage() {
    StringBuffer sb = new StringBuffer();
    sb.append(this.Message);
    if (this.InnerEx != null) {
      sb.append(Utilities.getFullStack(this.InnerEx));
    }

    return sb.toString();
  }
}
