package utilities.threadpool;

public enum WorkerPolicy {
  ABORT {
    @Override
    protected String getSymbol() {
      return "ABORT";
    }
  },
  DISCARD {
    @Override
    protected String getSymbol() {
      return "DISCARD";
    }
  },
  BLOCK {
    @Override
    protected String getSymbol() {
      return "BLOCK";
    }
  };

  @Override
  public String toString() {
    return getSymbol();
  }

  protected abstract String getSymbol();
}
