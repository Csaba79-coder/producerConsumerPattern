package csaba79coder.model;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MyBlockingQueue {

    private Queue<E> queue;
    private int max = 16;
    private ReentrantLock lock = new ReentrantLock(true);
    private Condition notEmpty = lock.newCondition();
    private Condition notFull = lock.newCondition();
    private Object notEmptySync = new Object();
    private Object notFullSync = new Object();

    public MyBlockingQueue(int size) {
        queue = new LinkedList<E>();
        this.max = size;
    }

    public void putLocks(E e) {
        lock.lock();
        try {
            if (queue.size() == max) {
                // block the thread! (because the size is full!)
                try {
                    notFull.await();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
            // we are protecting with a lock!
            queue.add(e);
            notEmpty.signalAll(); // signals to the consumer thread that it is not empty anymore!
        } finally {
            lock.unlock();
        }
    }

    public E takeLocks() {
        // protected by the same lock!
        lock.lock();
        try {
            while (queue.size() == 0) { // instead of if, we turn it to while,
                // that means not all the threads try to reach 1 single item, if it is only one in the queue!
                // block the thread, meanwhile there is no min 1 value in it! && signalling all the threads!!!!
                try {
                    notEmpty.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            E item = queue.remove();
            notFull.signalAll(); // signals to the producer thread that it is not full anymore! && signalling all the threads!!!!
            return item;
        } finally {
            lock.unlock();
        }
    }

    public synchronized void putWaitAndNotify(E e) {
        while (queue.size() == max) {
            try {
                notFullSync.wait();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
        queue.add(e);
        notEmptySync.notifyAll();
    }
}
