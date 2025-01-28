package big_data.january.util;

import big_data.january.model.Telemetry;

import java.util.*;


public class TelemetryProcessor {
    public static Telemetry processMessage(String message) {
        String[] data = message.split(",");
        Telemetry telemetry = new Telemetry(
                data[0].replaceAll("\"", ""), // date
                Double.parseDouble(data[1].replaceAll("\"", "")), // driverNumber
                Double.parseDouble(data[2].replaceAll("\"", "")), // rpm
                Double.parseDouble(data[3].replaceAll("\"", "")), // speed
                Double.parseDouble(data[4].replaceAll("\"", "")), // nGear
                Double.parseDouble(data[5].replaceAll("\"", "")), // throttle
                Double.parseDouble(data[6].replaceAll("\"", "")), // drs
                Double.parseDouble(data[7].replaceAll("\"", "")), // brake
                Double.parseDouble(data[8].replaceAll("\"", "")), // x
                Double.parseDouble(data[9].replaceAll("\"", "")), // y
                Double.parseDouble(data[10].replaceAll("\"", "")), // gapToLeader
                Double.parseDouble(data[11].replaceAll("\"", "")), // interval
                Double.parseDouble(data[12].replaceAll("\"", "")), // airTemperature
                Double.parseDouble(data[13].replaceAll("\"", "")), // humidity
                Double.parseDouble(data[14].replaceAll("\"", "")), // pressure
                Double.parseDouble(data[15].replaceAll("\"", "")), // rainfall
                Double.parseDouble(data[16].replaceAll("\"", "")), // trackTemperature
                Double.parseDouble(data[17].replaceAll("\"", "")), // windDirection
                Double.parseDouble(data[18].replaceAll("\"", "")), // windSpeed
                Double.parseDouble(data[19].replaceAll("\"", "")), // lapNumber
                data[20].replaceAll("\"", "") // tyreCompound
        );
        return telemetry;
    }
}