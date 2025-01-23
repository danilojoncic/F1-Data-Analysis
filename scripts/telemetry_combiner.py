import pandas as pd
import numpy as np
from datetime import timedelta



meeting_keys = [9523, 9590, 9625, 9636]
driver_numbers = [1, 16, 44, 55, 81]

def load_driver_data(meeting_key, driver_number):

    base_path = f"data/session_{meeting_key}"
    return {
        "speed": pd.read_json(f"{base_path}/car_data_driver_{driver_number}.json", convert_dates=["date"]),
        "gap": pd.read_json(f"{base_path}/intervals_driver_{driver_number}.json", convert_dates=["date"]),
        "lap": pd.read_json(f"{base_path}/laps_driver_{driver_number}.json", convert_dates=["date_start"]),
        "position": pd.read_json(f"{base_path}/position_driver_{driver_number}.json", convert_dates=["date"]),
        "location": pd.read_json(f"{base_path}/location_driver_{driver_number}.json", convert_dates=["date"]),
        "stint": pd.read_json(f"{base_path}/stints_driver_{driver_number}.json"),
        "weather": pd.read_json(f"{base_path}/weather.json", convert_dates=["date"])
    }

def process_racing_data(df_speed, df_gap, df_lap, df_position, df_location, df_stint, df_weather):

    all_timestamps = pd.concat([df_speed['date'], df_location['date'], df_gap['date']]).drop_duplicates().sort_values()
    unified_grid = pd.DataFrame({'date': all_timestamps})

    df_speed_reindexed = df_speed.set_index('date').reindex(unified_grid['date']).interpolate(method='time').reset_index()

    df_location_reindexed = df_location.set_index('date').reindex(unified_grid['date']).interpolate(method='time').reset_index()

    df_gap_reindexed = df_gap.set_index('date').reindex(unified_grid['date']).interpolate(method='time').reset_index()

    combined_df = pd.merge(
        df_speed_reindexed,
        df_location_reindexed,
        on=["date", "session_key", "meeting_key", "driver_number"],
        how="outer"
    )

    combined_df = pd.merge(
        combined_df,
        df_gap_reindexed,
        on=["date", "session_key", "meeting_key", "driver_number"],
        how="outer"
    )

    combined_df = pd.merge_asof(
        combined_df.sort_values('date'),
        df_weather.sort_values('date'),
        on="date",
        direction="backward"
    )

    combined_df["lap_number"] = np.nan

    for _, lap in df_lap.iterrows():
        lap_start = lap["date_start"]
        lap_duration = lap.get("lap_duration")

        if pd.isna(lap_duration):
            continue

        lap_end = lap_start + timedelta(seconds=lap_duration)
        mask = (combined_df["date"] >= lap_start) & (combined_df["date"] <= lap_end)
        combined_df.loc[mask, "lap_number"] = lap["lap_number"]

    combined_df["tyre_compound"] = np.nan
    combined_df["tyre_compound"] = combined_df["tyre_compound"].astype("object")

    for _, stint in df_stint.iterrows():
        mask = (combined_df["lap_number"] >= stint["lap_start"]) & (combined_df["lap_number"] <= stint["lap_end"])
        combined_df.loc[mask, "tyre_compound"] = stint["compound"]

    columns_to_drop = ["session_key", "meeting_key", "z","session_key_x", "meeting_key_x","session_key_y", "meeting_key_y"]
    columns_to_drop = [col for col in columns_to_drop if col in combined_df.columns]
    combined_df.drop(columns=columns_to_drop, inplace=True)

    lap_number_threshold = 1
    combined_df = combined_df[combined_df["lap_number"] > lap_number_threshold]
    return combined_df

def run():

    for meeting_key in meeting_keys:
        all_drivers_data = []

        for driver_number in driver_numbers:
            try:
                driver_data = load_driver_data(meeting_key, driver_number)

                processed_data = process_racing_data(
                    driver_data["speed"],
                    driver_data["gap"],
                    driver_data["lap"],
                    driver_data["position"],
                    driver_data["location"],
                    driver_data["stint"],
                    driver_data["weather"]
                )

                all_drivers_data.append(processed_data)
                print(f"Processed data for meeting {meeting_key}, driver {driver_number}")
            except FileNotFoundError:
                print(f"Data not found for meeting {meeting_key}, driver {driver_number}")
                continue
            except Exception as e:
                print(f"Error processing data for meeting {meeting_key}, driver {driver_number}: {e}")
                continue

        combined_data = pd.concat(all_drivers_data, ignore_index=True)

        combined_data.sort_values(by="date", inplace=True)

        output_file = f"combined_telemetry_meeting_{meeting_key}.csv"
        combined_data.to_csv(output_file, index=False)
        print(f"Combined telemetry for meeting {meeting_key} saved to {output_file}")

if __name__ == "__main__":
    run()