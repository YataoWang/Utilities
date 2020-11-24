package com.src;

public class IncrementNum {
  private int number = 0;

  public synchronized int getNextNumber() {
    int val = (++this.number);
    if (val >= Integer.MAX_VALUE) {
      val = 1;
    }

    return val;
  }
}
