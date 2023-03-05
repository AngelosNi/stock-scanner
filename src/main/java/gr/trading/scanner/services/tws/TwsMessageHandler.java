package gr.trading.scanner.services.tws;

import com.ib.client.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class TwsMessageHandler {

    private EClientSocket client;

    private EReader reader;

    private EJavaSignal signal;

    private AtomicInteger idx = new AtomicInteger(0);

    private Map<Integer, SyncedBarsList> histDataByReqId;

    public TwsMessageHandler(EClientSocket client, EReader reader, EJavaSignal signal, Map<Integer, SyncedBarsList> histDataByReqId) {
        this.client = client;
        this.reader = reader;
        this.signal = signal;
        this.histDataByReqId = histDataByReqId;
    }

    public List<Bar> reqHistoricalData(Contract contract, String endDateTime, String duration, String interval) throws InterruptedException {
        int reqIdx = idx.getAndIncrement();
        initializeHistDataWithIdx(reqIdx);
        client.reqHistoricalData(reqIdx, contract, endDateTime, duration, interval, "TRADES", 0, 1, false, List.of());

        synchronized (histDataByReqId.get(reqIdx).getMutex()) {
            histDataByReqId.get(reqIdx).getMutex().wait();
        }


        return histDataByReqId.get(reqIdx).getBars();
    }

    public void startReceiving() {
        reader.start();
        while (client.isConnected()) {
            signal.waitForSignal();
            try {
                reader.processMsgs();
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }
        }
    }

    private void initializeHistDataWithIdx(Integer idx) {
        histDataByReqId.put(idx, new SyncedBarsList());
    }

    @Data
    public class SyncedBarsList {

        private Object mutex = new Object();

        private List<Bar> bars = Collections.synchronizedList(new ArrayList<>());
    }
}
