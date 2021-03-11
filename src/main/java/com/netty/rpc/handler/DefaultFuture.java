package com.netty.rpc.handler;

import com.netty.rpc.common.RpcResponse;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DefaultFuture {
    private RpcResponse rpcResponse;
    private volatile boolean isSucceed = false;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    public RpcResponse getRpcResponse(int timeout) {
        try{
            lock.lock();
            while (!isSucceed) {
                condition.await(timeout, TimeUnit.MILLISECONDS);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }

        return rpcResponse;
    }

    public void setResponse(RpcResponse response) {
        if (isSucceed) {
            return;
        }
        try{
            lock.lock();
            this.rpcResponse = response;
            this.isSucceed = true;
            condition.signal();
        }finally {
            lock.unlock();
        }


    }
}