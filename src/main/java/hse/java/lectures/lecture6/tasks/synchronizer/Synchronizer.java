package hse.java.lectures.lecture6.tasks.synchronizer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Synchronizer {

    public static final int DEFAULT_TICKS_PER_WRITER = 10;
    private final List<StreamWriter> tasks;
    private final int ticksPerWriter;

    public Synchronizer(List<StreamWriter> tasks) {
        this(tasks, DEFAULT_TICKS_PER_WRITER);
    }

    public Synchronizer(List<StreamWriter> tasks, int ticksPerWriter) {
        this.tasks = tasks;
        this.ticksPerWriter = ticksPerWriter;
    }

    /**
     * Starts infinite writer threads and waits until each writer prints exactly ticksPerWriter ticks
     * in strict ascending id order.
     */
    public void execute() throws InterruptedException {
        List<StreamWriter> sortedTasks = tasks.stream()
                .sorted(Comparator.comparingInt(StreamWriter::getId))
                .toList();
        StreamingMonitor monitor = new StreamingMonitor(sortedTasks.get(0).getId(), sortedTasks.size(), ticksPerWriter);
        List<Thread> threads = new ArrayList<>();
        for (StreamWriter writer : sortedTasks) {
            writer.attachMonitor(monitor);
            Thread worker = new Thread(writer, "stream-writer-" + writer.getId());
            worker.setDaemon(false);
            worker.start();
            threads.add(worker);
        }

        for (Thread worker: threads) {
            worker.join();
        }
    }

}
