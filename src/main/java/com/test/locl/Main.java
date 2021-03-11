package com.test.locl;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    static ReentrantLock reentrantLock = new ReentrantLock();
    static Condition condition = reentrantLock.newCondition();
    static Condition condition2 = reentrantLock.newCondition();
    static int something = 0;
    public static void main(String[] args) {
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    reentrantLock.lock();
                    for(;;){
                        something ++;
                        System.out.println("生产了一个东西..等待售出");
                        condition.await();
                        condition2.signal();

                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    reentrantLock.unlock();
                }

            }
        });
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for(;;){
                        reentrantLock.lock();
                        if(something > 0){
                            something --;
                        }
                        System.out.println("销售了一个东西");
                        condition.signal();
                        condition2.await();

                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    reentrantLock.unlock();
                }
            }
        });
        t1.start();
        t2.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
