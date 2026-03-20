package hse.java.lectures.lecture6.tasks.synchronizer;

public class StreamingMonitor {
    private int currId;
    private final int workerNum;
    private final int ticksPerWriter;
    private int refreshCnt = 0;

    public StreamingMonitor(int id, int num, int ticks) {
        currId = id;
        workerNum = num;
        ticksPerWriter = ticks;
    }

    public void waitQueue (int id) {
        synchronized (this) {
            while (currId != id && !checkAllTicksPrinted()) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void refresh(int id) {
        synchronized (this) {
            refreshCnt++;
            currId = currId % workerNum + 1;
            notifyAll();
        }
    }

    public boolean checkAllTicksPrinted() {
        synchronized (this) {
            return refreshCnt >= ticksPerWriter * workerNum;
        }
    }

}
