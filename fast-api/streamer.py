import os.path
import time

from fastapi import FastAPI, Response
from fastapi.responses import StreamingResponse
import csv
from datetime import datetime

app = FastAPI()

# Health check route
@app.get("/health")
def health_check():
    return {"status": "ok"}

def generate(csv_file_path):
    with open(csv_file_path, mode="r") as file:
        reader = csv.DictReader(file)
        previous_time = None

        for row in reader:
            current_time = datetime.fromisoformat(row["date"])

            if previous_time is not None:
                time_delta = (current_time - previous_time).total_seconds()
                time.sleep(time_delta)

            previous_time = current_time

            csv_string = ",".join([f'"{value}"' for value in row.values()]) + "\n"
            yield csv_string.encode("utf-8")

        yield "ALL-LINES-OF-TELEMETRY-READ".encode("utf-8")


@app.get("/monaco-telemetry")
def stream_csv1():
    csv_file_path = os.path.join("resources", "combined_telemetry_meeting_9523.csv")
    return StreamingResponse(generate(csv_file_path), media_type="text/event-stream")

@app.get("/monza-telemetry")
def stream_csv2():
    csv_file_path = os.path.join("resources", "combined_telemetry_meeting_9590.csv")
    return StreamingResponse(generate(csv_file_path), media_type="text/event-stream")


@app.get("/mexico-telemetry")
def stream_csv3():
    csv_file_path = os.path.join("resources", "combined_telemetry_meeting_9625.csv")
    return StreamingResponse(generate(csv_file_path), media_type="text/event-stream")


@app.get("/brazil-telemetry")
def stream_csv4():
    csv_file_path = os.path.join("resources", "combined_telemetry_meeting_9636.csv")
    return StreamingResponse(generate(csv_file_path), media_type="text/event-stream")



# Run the app with Uvicorn
if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)