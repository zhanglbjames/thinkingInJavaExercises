package concurrent.chapter4;


import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zhanglbjames@163.com
 * @version Created on 2017/4/2.
 */
public class DefaultThreadPool<Job extends Runnable> implements ThreadPool<Job> {

    private static final int MAX_WORKER_NUMBERS = 10;
    private static final int DEFAULT_WORKER_NUMBERS = 5;
    private static final int MIN_WORKER_NUMBERS = 1;
    private final LinkedList<Job> jobs = new LinkedList<Job>();
    private final List<Worker> workers = Collections.synchronizedList(new ArrayList<Worker>());
    private int workerNum = DEFAULT_WORKER_NUMBERS;
    private AtomicLong threadNum = new AtomicLong();

    public DefaultThreadPool() {
        initializeWorkers(DEFAULT_WORKER_NUMBERS);
    }

    public DefaultThreadPool(int num) {
        workerNum = num > MAX_WORKER_NUMBERS ? MAX_WORKER_NUMBERS : num < MIN_WORKER_NUMBERS ? MAX_WORKER_NUMBERS : num;
        initializeWorkers(workerNum);

    }

    public void execute(Job job) {

        if (job != null) {
            synchronized (jobs) {
                jobs.addLast(job);
                jobs.notifyAll();
            }
        }
    }

    public void shutdown() {
        for (Worker worker : workers) {
            worker.shutdown();
        }

    }

    public void addWorkers(int num) {

        synchronized (jobs) {
            if (num + this.workerNum > MAX_WORKER_NUMBERS) {
                num = MAX_WORKER_NUMBERS - this.workerNum;
            }
            initializeWorkers(num);
            this.workerNum += num;
        }
    }

    public void removeWorkers(int num) {
        synchronized (jobs) {
            if (num >= this.workerNum) {
                throw new IllegalArgumentException("beyond workerNum");
            }
            int count = 0;
            while (count < num) {
                Worker worker = workers.get(count);
                if (workers.remove(worker)) {
                    worker.shutdown();
                    count++;
                }
            }
            this.workerNum -= count;

        }
    }

    public int getJobSize() {
        return jobs.size();
    }

    private void initializeWorkers(int num) {
        for (int i = 0; i < num; i++) {
            Worker worker = new Worker();
            workers.add(worker);
            Thread thread = new Thread(worker, "ThreadPool-Worker-" + threadNum.incrementAndGet());
            thread.start();
        }
    }

    class Worker implements Runnable {

        private volatile boolean running = true;

        public void run() {
            while (running) {
                Job job = null;
                synchronized (jobs) {//先加锁
                    while (jobs.isEmpty()) {
                        try {
                            jobs.wait();//在jobs上等待的线程
                        } catch (InterruptedException e) {//响应外部对Worker的中断操作,抛出异常时，清除中断标志
                            e.printStackTrace();
                            Thread.currentThread().interrupt();//重新设置自己的中断标志
                            return;//直接返回
                        }
                    }
                    job = jobs.removeFirst();
                }
                if (job != null) {
                    try {
                        job.run();
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
            }

        }

        public void shutdown() {
            running = false;
        }
    }
}
