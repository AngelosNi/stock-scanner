package gr.trading.scanner.configuration;

import com.ib.client.EClientSocket;
import com.ib.client.EJavaSignal;
import com.ib.client.EReader;
import gr.trading.scanner.repositories.SymbolsRepository;
import gr.trading.scanner.repositories.TwsSymbolsRepository;
import gr.trading.scanner.services.tws.TwsMessageHandler;
import gr.trading.scanner.services.tws.TwsMessageListener;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.HashMap;
import java.util.Map;


@Configuration
@Slf4j
public class TwsConfig {

    private Map<Integer, TwsMessageHandler.SyncedBarsList> histDataByReqId = new HashMap<>();

    @Bean
    public EJavaSignal eJavaSignal() {
        return new EJavaSignal();
    }

    @Bean
    public TwsMessageListener twsReceiver() {
        return new TwsMessageListener(histDataByReqId);
    }

    @Bean
    public EClientSocket eClientSocket(TwsMessageListener receiver, EJavaSignal eJavaSignal) {
        EClientSocket eClientSocket = new EClientSocket(receiver, eJavaSignal);
        eClientSocket.eConnect("127.0.0.1", 4001, 0);
        while (!eClientSocket.isConnected());

        log.info("Client connected.");

        return eClientSocket;
    }

    @Bean
    public EReader eReader(EClientSocket eClientSocket, EJavaSignal eJavaSignal) {
        return new EReader(eClientSocket, eJavaSignal);
    }

    @Bean
    public TwsMessageHandler twsMessageProcessor(EClientSocket eClientSocket, EReader eReader, EJavaSignal eJavaSignal) {
        return new TwsMessageHandler(eClientSocket, eReader, eJavaSignal, histDataByReqId);
    }

    @Configuration
    @AllArgsConstructor
    public static class TwsMessageProcessorConfig {

        private TwsMessageHandler twsMessageHandler;

        @PostConstruct
        public void init() {
            new Thread(twsMessageHandler::startReceiving).start();
        }
    }
}
