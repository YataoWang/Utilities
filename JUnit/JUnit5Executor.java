import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.Filter;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.discovery.ClassNameFilter;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.*;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

import java.util.ArrayList;
import java.util.List;

public class JUnit5Executor {
  public static void main(String[] args) {
    LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request().selectors(getSelector()).filters(getFilter()).build();
    Launcher launcher = LauncherFactory.create();
    JUnit5ResultListener listener = new JUnit5ResultListener();
    launcher.execute(request, listener);
  }

  private static List<DiscoverySelector> getSelector() {
    List<DiscoverySelector> selectors = new ArrayList<>();
    String jarPath = JUnit5Executor.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    List<String> classNames = Utilities.getClassNameFromJar(jarPath);
    for (int i = 0; i < classNames.size(); i++) {
      DiscoverySelector selector = DiscoverySelectors.selectClass(classNames.get(i));
      if (selector != null) {
        selectors.add(selector);
      }
    }

    return selectors;
  }

  private static Filter[] getFilter() {
    Filter[] filters = new Filter[2];
    filters[0] = ClassNameFilter.includeClassNamePatterns(null);
    filters[1] = ClassNameFilter.excludeClassNamePatterns(null);
    return filters;
  }
}

class JUnit5ResultListener implements TestExecutionListener {
  private List<TestUnitCase>  _TestCases = new ArrayList<TestUnitCase>();
  private long _StartTme;
  private long _EndTime;

  @Override
  public void testPlanExecutionStarted(TestPlan testPlan) {
    this._StartTme = System.currentTimeMillis();
  }

  @Override
  public void testPlanExecutionFinished(TestPlan testPlan) {
    this._EndTime = System.currentTimeMillis();
    printResults();
  }

  @Override
  public void executionSkipped(TestIdentifier testIdentifier, String reason) {
    TestUnitCase test = newTestUnitCase(testIdentifier);
    if (test != null) {
      test.setResult(JUnit5Result.DISABLED);
      test.setMessage(reason);
      test.setEndTime(System.currentTimeMillis());
    }
  }

  @Override
  public void executionStarted(TestIdentifier testIdentifier) {
    newTestUnitCase(testIdentifier);
  }

  @Override
  public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
    TestUnitCase test = getTestUnitCaseById(testIdentifier.getUniqueId());
    if (test != null) {
      test.setEndTime(System.currentTimeMillis());

      String result = JUnit5Result.UNKNOWN;
      if (testExecutionResult.getStatus() == TestExecutionResult.Status.SUCCESSFUL) {
        result = JUnit5Result.SUCCESS;
      } else if (testExecutionResult.getStatus() == TestExecutionResult.Status.FAILED) {
        result = JUnit5Result.FAILURE;
      } else if (testExecutionResult.getStatus() == TestExecutionResult.Status.ABORTED) {
        result = JUnit5Result.ABORT;
      }
      test.setResult(result);

      String message = "";
      Throwable innerEx = null;
      if (testExecutionResult.getThrowable().isPresent()) {
        message = testExecutionResult.getThrowable().get().getMessage();
        innerEx = testExecutionResult.getThrowable().get();
      }
      test.setMessage(message);
      test.setException(innerEx);
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
      .append(" Success:").append(getUnitCount(JUnit5Result.SUCCESS))
      .append(" Failure:").append(getUnitCount(JUnit5Result.FAILURE))
      .append(" Disabled:").append(getUnitCount(JUnit5Result.DISABLED))
      .append(" Abort:").append(getUnitCount(JUnit5Result.ABORT))
      .append(" Unknown:").append(getUnitCount(JUnit5Result.UNKNOWN))
      .append(" Total time:").append(this._EndTime - this._StartTme).append(" ms");
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

  private TestUnitCase newTestUnitCase(TestIdentifier testIdentifier) {
    TestUnitCase test = null;
    if (testIdentifier.isTest()) {
      String name = testIdentifier.getDisplayName();
      int idx = name.indexOf("(");
      if (idx > -1) {
        name = name.substring(0, idx);
      }

      String className = "";
      String clsSeperator = "class:";
      if (testIdentifier.getParentId().isPresent()) {
        className = testIdentifier.getParentId().get();
        className = className.substring(className.indexOf(clsSeperator) + clsSeperator.length(), className.lastIndexOf("]"));
      }

      test = new TestUnitCase(testIdentifier.getUniqueId(), name, className, testIdentifier.getDisplayName());
      this._TestCases.add(test);
    }

    return test;
  }

  private TestUnitCase getTestUnitCaseById(String Id) {
    for (int i = 0; i < this._TestCases.size(); i++) {
      TestUnitCase test = this._TestCases.get(i);
      if (test.Id.equalsIgnoreCase(Id)) {
        return test;
      }
    }

    return null;
  }
}

class JUnit5Result {
  public static final String SUCCESS = "TRUE";
  public static final String FAILURE = "FALSE";
  public static final String DISABLED = "DISABLED";
  public static final String ABORT = "ABORT";
  public static final String UNKNOWN = "UNKNOWN";
}
