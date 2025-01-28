package big_data.january;

import big_data.january.model.Telemetry;
import big_data.january.swing.TelemetryPanel;
import big_data.january.util.TelemetryProcessor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import javax.swing.*;
import java.time.Duration;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Runner {

    public static ConcurrentLinkedQueue<Telemetry> telemetries = new ConcurrentLinkedQueue<>();
    public static String currentTimeStamp = "ama bas nista!";

    public static void main(String[] args) {
        KafkaConsumer<String, String> consumer = Consumer.createConsumer();

        JFrame frame = new JFrame("Telemetry Visualizer");
        TelemetryPanel telemetryPanel = new TelemetryPanel(telemetries,"yourFilePath:)");
        frame.add(telemetryPanel);
        frame.setSize(1280, 720);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        Timer timer = new Timer(100, e -> telemetryPanel.repaint());
        timer.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down consumer!");
            consumer.close();
            System.out.println("Consumer closed!");
        }));

        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(200));
                for (ConsumerRecord<String, String> record : records) {
                    if (record.value().equals("ALL-LINES-OF-TELEMETRY-READ")) {
                        System.out.println("END OF TELEMETRY. Shutting down consumer!");
                        return;
                    }

                    Telemetry t = TelemetryProcessor.processMessage(record.value());

                    if (currentTimeStamp.equals(t.date())) {
                        telemetries.add(t);
                    } else {
                        //possible solution, must try later
                        //telemetryPanel.repaint();
                        currentTimeStamp = t.date();
                        telemetries.clear();
                        telemetries.add(t);
                    }
                }
            }
        } finally {
            consumer.close();
        }
    }
}