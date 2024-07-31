import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;



/*
 * Complete the 'LogServer' class below.
 *
 */

class LogRecord {
    int logId;
    int timestamp;

    LogRecord(int logId, int timestamp) {
        this.logId = logId;
        this.timestamp = timestamp;
    }
}

class LogServer {
    private int maxLogs;
    private Deque<LogRecord> logDeque;

    public LogServer(int maxLogs) {
        this.maxLogs = maxLogs;
        this.logDeque = new LinkedList<>();
    }

    public void recordLog(int logId, int timestamp) {
        logDeque.add(new LogRecord(logId, timestamp));
        while (logDeque.size() > maxLogs) {
            logDeque.pollFirst(); // remove oldest log to maintain the size
        }
    }

    public String getLogs() {
        if (logDeque.isEmpty()) return "";

        int latestTimestamp = logDeque.peekLast().timestamp;
        int oneHourAgo = latestTimestamp - 3600;
        List<Integer> result = new ArrayList<>();

        for (LogRecord log : logDeque) {
            if (log.timestamp > oneHourAgo) {
                result.add(log.logId);
            }
        }

        return String.join(",", result.stream().map(String::valueOf).toArray(String[]::new));
    }

    public int getLogCount() {
        if (logDeque.isEmpty()) return 0;

        int latestTimestamp = logDeque.peekLast().timestamp;
        int oneHourAgo = latestTimestamp - 3600;
        int count = 0;

        for (LogRecord log : logDeque) {
            if (log.timestamp >= oneHourAgo) {
                count++;
            }
        }

        return count;
    }
}

 public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        Scanner scanner = new Scanner(System.in);

        int maxLogs = scanner.nextInt();
        LogServer logServer = new LogServer(maxLogs);

        int numQueries = scanner.nextInt();
        scanner.nextLine();  // consume the rest of the line

        for (int i = 0; i < numQueries; i++) {
            String[] query = scanner.nextLine().split(" ");

            if (query[0].equals("RECORD")) {
                logServer.recordLog(Integer.parseInt(query[1]), Integer.parseInt(query[2]));
            } else if (query[0].equals("GET_LOGS")) {
                bufferedWriter.write(logServer.getLogs());
                bufferedWriter.newLine();
            } else if (query[0].equals("COUNT")) {
                bufferedWriter.write(Integer.toString(logServer.getLogCount()));
                bufferedWriter.newLine();
            }
        }

        scanner.close();
        bufferedWriter.close();
    }
}
