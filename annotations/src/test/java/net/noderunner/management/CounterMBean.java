package net.noderunner.management;

/**
 * Interface useful for reflection.
 */
public interface CounterMBean {

    void resetCounter();

    int getCounter();

    void setCounter(int counter);

    void resetCounter2(int counter);

    String getName();

}