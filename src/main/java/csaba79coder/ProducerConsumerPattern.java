package csaba79coder;

import csaba79coder.model.Item;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;

public class ProducerConsumerPattern {

    public static void main(String[] args) {

        // handles concurrent thread access
        // thread safe datastructures!
        BlockingDeque<Item> queue = (BlockingDeque<Item>) new ArrayBlockingQueue<Item>(10);


        // Producer
        final Runnable producer = () -> {
            while (true) {
                try {
                    queue.put(new Item().createItem()); // thread blocks if queue is full!
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        new Thread(producer).start();
        new Thread(producer).start();


        // Consumer
        final Runnable consumer = () -> {
            while (true) {
                try {
                    Item i = queue.take(); // thread blocks if queue is empty!
                    process(i);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        new Thread(consumer).start();
        new Thread(consumer).start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void process(Item i) {
    }
}
