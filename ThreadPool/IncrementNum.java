package com.src;

public class IncrementNum {
  private int number = 0;

  public synchronized int getNextNumber() {
    return ++number;
  }
}
