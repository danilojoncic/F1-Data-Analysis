package big_data.january.model;

public record Telemetry(
         String date,
         double driverNumber,
         double rpm,
         double speed,
         double nGear,
         double throttle,
         double drs,
         double brake,
         double x,
         double y,
         double gapToLeader,
         double interval,
         double airTemperature,
         double humidity,
         double pressure,
         double rainfall,
         double trackTemperature,
         double windDirection,
         double windSpeed,
         double lapNumber,
         String tyreCompound
) {
}
/*
date,driver_number,rpm,speed,n_gear,throttle,drs,brake
,x,y,gap_to_leader,interval,air_temperature,humidity,pressure
,rainfall,track_temperature,wind_direction,
wind_speed,lap_number,tyre_compound
 */
