import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import utilities.common.Utilities;

import java.util.ArrayList;
import java.util.List;

public class JUnit4Executor {
  public static void main(String[] args) {
    RunListener listener = new JUnit4ResultListener();
    JUnitCore core = new JUnitCore();
    core.addListener(listener);
    core.run(getClasses());
  }

  private static Class[] getClasses() {
    List<Class> classes = new ArrayList<>();
    String jarPath = JUnit4Executor.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    List<String> classNames = Utilities.getClassNameFromJar(jarPath);
    for (int i = 0; i < classNames.size(); i++) {
      try {
        Class cls = Class.forName(classNames.get(i));
        if (cls != null) {
          classes.add(cls);
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }

    return classes.toArray(new Class[0]);
  }
}

class JUnit4ResultListener extends RunListener {
  private List<TestUnitCase> _TestCases;
  private long _TotalTime;
  public void testRunStarted(Description description) throws Exception {
    this._TestCases = new ArrayList<>();
  }

  public void testRunFinished(Result result) throws Exception {
    this._TotalTime = result.getRunTime();
  }

  public void testStarted(Description description) throws Exception {
    newTestUnitCase(description);
  }

  public void testFinished(Description description) throws Exception {
    TestUnitCase test = getTestUnitCaseByName(description.getMethodName());
    if (test != null) {
      test.setEndTime(System.currentTimeMillis());
      test.setResult(JUnit4Result.SUCCESS);
    }
  }

  public void testFailure(Failure failure) throws Exception {
    TestUnitCase test = getTestUnitCaseByName(failure.getDescription().getMethodName());
    if (test != null) {
      test.setEndTime(System.currentTimeMillis());
      test.setResult(JUnit4Result.FAILURE);
      test.setMessage(failure.getMessage());
      test.setException(failure.getException());
    }
  }

  public void testAssumptionFailure(Failure failure) {
    TestUnitCase test = getTestUnitCaseByName(failure.getDescription().getMethodName());
    if (test != null) {
      test.setEndTime(System.currentTimeMillis());
      test.setResult(JUnit4Result.FAILURE);
      test.setMessage(failure.getMessage());
      test.setException(failure.getException());
    }
  }

  public void testIgnored(Description description) throws Exception {
    TestUnitCase test = getTestUnitCaseByName(description.getMethodName());
    if (test != null) {
      test.setEndTime(System.currentTimeMillis());
      test.setResult(JUnit4Result.IGNORE);
    }
  }

  private void printResults() {
    for (int i = 0; i < this._TestCases.size(); i++) {
      TestUnitCase test = this._TestCases.get(i);
      StringBuffer sb = new StringBuffer();
      sb.append(i+1).append(") ").append(test.toString());
      System.out.println(sb.toString());
    }

    StringBuffer sb = new StringBuffer();
    sb.append("Total test cases:").append(this._TestCases.size())
      .append(" Success:").append(getUnitCount(JUnit4Result.SUCCESS))
      .append(" Failure:").append(getUnitCount(JUnit4Result.FAILURE))
      .append(" Ignore:").append(getUnitCount(JUnit4Result.IGNORE))
      .append(" Unknown:").append(getUnitCount(JUnit4Result.UNKNOWN))
      .append(" Total time:").append(this._TotalTime).append(" ms");
    System.out.println(sb.toString());
  }

  private int getUnitCount(String result) {
    int count = 0;
    for (int i = 0; i < this._TestCases.size(); i++) {
      TestUnitCase test = this._TestCases.get(i);
      if (test.getResult().equalsIgnoreCase(result)) {
        count++;
      }
    }

    return count;
  }

  private TestUnitCase newTestUnitCase(Description description) {
    TestUnitCase test = null;
    if (description.isTest()) {
      test = new TestUnitCase(null, description.getMethodName(), description.getClassName(), description.getDisplayName());
      test.setResult(JUnit4Result.UNKNOWN);
      this._TestCases.add(test);
    }

    return test;
  }

  private TestUnitCase getTestUnitCaseByName(String Name) {
    for (int i = 0; i < this._TestCases.size(); i++) {
      TestUnitCase test = this._TestCases.get(i);
      if (test.Name.equalsIgnoreCase(Name)) {
        return test;
      }
    }

    return null;
  }
}

class JUnit4Result {
  public static final String SUCCESS = "TRUE";
  public static final String FAILURE = "FALSE";
  public static final String IGNORE = "DISABLED";
  public static final String UNKNOWN = "UNKNOWN";
}
