import requests
import json
import os
import time

# Define the base URL for the OpenF1 API
base_url = 'https://api.openf1.org/v1/'

meeting_keys = [1236, 1244, 1248, 1249]
driver_numbers = [1, 16, 44, 55, 81]


def create_files(session_times):
    os.makedirs('data', exist_ok=True)
    for session_key, times in session_times.items():
        session_dir = os.path.join('data', f'session_{session_key}')
        os.makedirs(session_dir, exist_ok=True)

        with open(os.path.join(session_dir, 'session_metadata.json'), 'w') as f:
            json.dump(times, f, indent=2)


def fetch_with_delay(url, params):
    time.sleep(3)
    response = requests.get(url, params=params)
    return response


def fetch():
    session_times = {}
    for key in meeting_keys:
        session_times.update(fetch_sessions(key))
    return session_times


def fetch_sessions(meeting_key):
    session_url = f'{base_url}sessions'
    params = {'meeting_key': meeting_key, 'session_name': 'Race', 'session_type': 'Race'}

    response = fetch_with_delay(session_url, params)

    session_data = {}
    if response.status_code == 200:
        sessions = response.json()
        for session in sessions:
            session_key = session['session_key']
            date_start = session['date_start']
            date_end = session['date_end']
            session_data[session_key] = {
                'start_time': date_start,
                'end_time': date_end,
                'meeting_key': meeting_key
            }
    else:
        print(f"Failed to fetch sessions for meeting key {meeting_key}: {response.status_code}")

    return session_data


def fetch_weather(session_key, date_start, date_end):
    weather_url = f'{base_url}weather'
    params = {
        'session_key': session_key,
        'date>': date_start,
        'date<': date_end
    }

    response = fetch_with_delay(weather_url, params)

    if response.status_code == 200:
        weather_data = response.json()
        with open(os.path.join('data', f'session_{session_key}', 'weather.json'), 'w') as f:
            json.dump(weather_data, f, indent=2)
        return weather_data
    else:
        print(f"Failed to fetch weather for session {session_key}: {response.status_code}")
        return None


def fetch_location(session_key, date_start, date_end, driver_number):
    location_url = f'{base_url}location'
    params = {
        'session_key': session_key,
        'date>': date_start,
        'date<': date_end,
        'driver_number': driver_number
    }

    response = fetch_with_delay(location_url, params)

    if response.status_code == 200:
        location_data = response.json()
        with open(os.path.join('data', f'session_{session_key}', f'location_driver_{driver_number}.json'), 'w') as f:
            json.dump(location_data, f, indent=2)
        return location_data
    else:
        print(f"Failed to fetch location for driver {driver_number} in session {session_key}: {response.status_code}")
        return None


def fetch_car_data(session_key, date_start, date_end, driver_number):
    car_url = f'{base_url}car_data'
    params = {
        'session_key': session_key,
        'date>': date_start,
        'date<': date_end,
        'driver_number': driver_number
    }

    response = fetch_with_delay(car_url, params)

    if response.status_code == 200:
        car_data = response.json()
        with open(os.path.join('data', f'session_{session_key}', f'car_data_driver_{driver_number}.json'), 'w') as f:
            json.dump(car_data, f, indent=2)
        return car_data
    else:
        print(f"Failed to fetch car data for driver {driver_number} in session {session_key}: {response.status_code}")
        return None


def fetch_laps(session_key, date_start, date_end, driver_number):
    laps_url = f'{base_url}laps'
    params = {
        'session_key': session_key,
        'date_start>': date_start,
        'date_start<': date_end,
        'driver_number': driver_number
    }

    response = fetch_with_delay(laps_url, params)

    if response.status_code == 200:
        laps_data = response.json()
        with open(os.path.join('data', f'session_{session_key}', f'laps_driver_{driver_number}.json'), 'w') as f:
            json.dump(laps_data, f, indent=2)
        return laps_data
    else:
        print(f"Failed to fetch laps for driver {driver_number} in session {session_key}: {response.status_code}")
        return None


def fetch_intervals(session_key, date_start, date_end, driver_number):
    intervals_url = f'{base_url}intervals'
    params = {
        'session_key': session_key,
        'date>': date_start,
        'date<': date_end,
        'driver_number': driver_number
    }

    response = fetch_with_delay(intervals_url, params)

    if response.status_code == 200:
        intervals_data = response.json()
        with open(os.path.join('data', f'session_{session_key}', f'intervals_driver_{driver_number}.json'), 'w') as f:
            json.dump(intervals_data, f, indent=2)
        return intervals_data
    else:
        print(f"Failed to fetch intervals for driver {driver_number} in session {session_key}: {response.status_code}")
        return None


def fetch_position(session_key, date_start, date_end, driver_number):
    position_url = f'{base_url}position'
    params = {
        'session_key': session_key,
        'date>': date_start,
        'date<': date_end,
        'driver_number': driver_number
    }

    response = fetch_with_delay(position_url, params)

    if response.status_code == 200:
        position_data = response.json()
        with open(os.path.join('data', f'session_{session_key}', f'position_driver_{driver_number}.json'), 'w') as f:
            json.dump(position_data, f, indent=2)
        return position_data
    else:
        print(f"Failed to fetch position for driver {driver_number} in session {session_key}: {response.status_code}")
        return None


def fetch_stints(session_key, driver_number):
    stints_url = f'{base_url}stints'
    params = {
        'session_key': session_key,
        'driver_number': driver_number
    }

    response = fetch_with_delay(stints_url, params)

    if response.status_code == 200:
        stints_data = response.json()
        with open(os.path.join('data', f'session_{session_key}', f'stints_driver_{driver_number}.json'), 'w') as f:
            json.dump(stints_data, f, indent=2)
        return stints_data
    else:
        print(f"Failed to fetch stints for driver {driver_number} in session {session_key}: {response.status_code}")
        return None


def main():
    session_times = fetch()

    create_files(session_times)

    for session_key, session_info in session_times.items():
        print("STARTED!\n")
        fetch_weather(session_key, session_info['start_time'], session_info['end_time'])

        for driver_number in driver_numbers:
            fetch_location(session_key, session_info['start_time'], session_info['end_time'], driver_number)
            fetch_car_data(session_key, session_info['start_time'], session_info['end_time'], driver_number)
            fetch_laps(session_key, session_info['start_time'], session_info['end_time'], driver_number)
            fetch_intervals(session_key, session_info['start_time'], session_info['end_time'], driver_number)
            fetch_position(session_key, session_info['start_time'], session_info['end_time'], driver_number)
            fetch_stints(session_key, driver_number)


if __name__ == "__main__":
    main()