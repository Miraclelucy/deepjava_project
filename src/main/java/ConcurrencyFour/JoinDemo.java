package ConcurrencyFour;

/**
 * 《java并发编程的艺术》4.3.5 Thread Join()的用法
 **  @author lushiqin 20181228
 */
public class JoinDemo {
    public static void main(String[] args) throws Exception {
        Thread previous = Thread.currentThread();
        for (int i = 0; i < 10; i++) {
            // 每个线程拥有前一个线程的引用，需要等待前一个线程终止，才能从等待中返回
            Thread thread = new Thread(new Domino(previous), String.valueOf(i));
            thread.start();
            previous = thread;
        }
        SleepUtils.second(5000);
        System.out.println(Thread.currentThread().getName() + " terminate.");
    }
    static class Domino implements Runnable {
        private Thread thread;
        public Domino(Thread thread) {
            this.thread = thread;
        }
        public void run() {
            try {
                thread.join();
            } catch (InterruptedException e) {
            }
            System.out.println(Thread.currentThread().getName() + " terminate.");
        }
    }
}
