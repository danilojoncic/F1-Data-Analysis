# F1-Data-Analysis
Exam project for BigData course at RAF

## Preface
This repository contains code for the following: 
- real time data visualization using Kafka, FastAPI , Java Swing GUI,
- data analysis on fully loaded datasets using Apache Spark and Pandas on the Databricks platform

## Contents:
- Data collection and data set creation
- FastAPI service
- Kafka (producer and consumer) and real time visualization
- Databricks analysis

## Data collection and data set creation
To be able to analyse the data, first we need to get the data (ko bih rekao?). Data collection is done using python script which will do several GET requests to the API for several endpoints (car_data,intervals,locations etc.) Once the data is fetched and kept in coresponding .json files for each driver. We then need to combine it. For that we will need to create a unified timeline of all timestamps that one driver's data has. Then we interpolate or manually calculate the missing data for the timestamps which are until now blank. This process is essential in creating somewhat real-time data (based on the timestamps, each row is a moment in time, and the next row is several miliseconds after). Bear in mind that the unified timestamps for all drivers differ, The decision not to create a new even bigger and even more "unified" timeline was done because the amount of interpolation then would skeew and artificially make the data bad. All the code is located in the /scripts directory and is runnable!


## FastAPI service
To simulate the way the data is sent i needed to create an API service that would serve the data in a continuous text stream. The API sends the data based on the delta of 2 adjecent rows meaning i send 1 row, wait for the difference in time between that row and the next one until i send the next one (Varies based on the interpolation but its around 50-100m miliseconds) The FastAPI code relies on 4 datasets which are served from 4 endpoints in real time. Meaning if the dataset has data that lasted 2 hourse , then the text stream will last 2 hours. Please do not try to get the data from the browser, as eventually it will error out because of the constant stream of csv text!


## Kafka (producer and consumer) and real time visualization
As the exam project specifiaction specified and awarded points for the use of Apache Kafka with its Producer and Consumer this was done aswell. Bear in mind that to be able to run this, its recommended to downlaod the Apache Kafka and Zookeeper Docker images and run them. The code for the Producer and Consumer is simple Java code, no more than 5 classes including a model record class. The Producers job is to call the FastAPI service's endpoint and grab the text stream. As the text stream rolls in simulating the way the data flows in F1, each row is packed and sent to the Kafka Topic coresponding to the race/endpoint. All of this is basic, so dont expect any Kafka partition work. The Consumer on the other side grabs the data from the topic, parses it into Java Telemetry records and uses them for visualization later on. The Visualizer is a basic Java Swing JFrame and JPanel, we draw the race-track and the drivers on it as the data is rolling in. As previously stated the data is not 100% interpolated which means that for some moments on the timeline not all data for all drivers is available, this causes a noticable flicker on the visualizer. But after more research and after seeing that the Official Sauber F1 team visualizer also has a flicker i decieded to just continue on. The process happens like this we grab the Telemetry record data, and place it in a ConncurentQueue (as normal Java and Java Swing run on different threads this is needed) When we encounter a timestamp , we grab all telemtry objects , meaning all drivers that are present for that timestamp (be it 1 or all 5) and put them in the ConcurrentQueue, the Java Swing painter paints from the contents of that ConcurrentQueue and when done we empty it out since there is no use in storing 100.000 of Telemetry objects, lets just hold 5 at max
- Visualizer explainer and [inspiration](https://www.youtube.com/watch?v=0sR5oCIfXDI)


## Databricks analysis
Databricks analyses are all done using PySpark and Pandas. The visualization is done using MatPlotLib and SeaBorne. To keep the README short, all the descriptions of the analyses are located in the /databricks directory. The preview images are located bellow this text and should provide enough information without even reading the full .html and .ipynb files. All the analyses use statistacal tests to try to prove or disprove various hypotheses.


## Requirements:
- Python 3.12 with libraries (Pandas, Numpy, FastAPI, SeaBorn, MatPlotLib, PySpark)
- Docker
- Databricks Community Edition account for platform access
- Apache Kafka Docker Image
- Apache Kafka Producer Java code
- Apache Kafka Consumer Java code
- F1 [API](https://openf1.org/)

## Instructions
- To use the visualizer, make sure to have all the requirements (Kafka docker image,Java code for Kafka producer and Consumer and the FastAPI python code with all the necessary datasets)
- Start up the FastAPI service
- Start up docker image with Apache Kafka and Apache Zookeeper
- Start up the Kafka producer and assigne a Kafka topic where the data will be sent
- Start up the Kafka consumer, when active a JavaSwing window will appear, you need to wait a few moments until the Zookeper registers the consumer and the topic becomes availeble for the consumer


## Possible additions
- Grafana dashboard for visualization of telemetry in real time
- Analysis on the entire season data set (>20 races)


## Images, Charts and Animations
![image](https://github.com/user-attachments/assets/2ef3075b-eeee-4300-9125-a01a2b48924b)
![ezgif-4e88cd9d554a2](https://github.com/user-attachments/assets/3f9340f6-4524-43d6-8d6e-04af7dc42f77)
![image](https://github.com/user-attachments/assets/90d86f6d-f135-4ea5-92dd-97ccdc0917b2)
![image](https://github.com/user-attachments/assets/907fa1e8-c273-4d84-a5ed-d7e521bbae6e)
![image](https://github.com/user-attachments/assets/044fcbed-f61a-43b4-a0b0-8daa2e05bb4b)
![image](https://github.com/user-attachments/assets/4a6ba4b1-e38a-4312-95aa-cb29929329d4)
![image](https://github.com/user-attachments/assets/232d12ef-527f-4134-95bc-d04a8efdb5a9)
![image](https://github.com/user-attachments/assets/11d6ced7-1036-4068-9eec-8eb42f1b3477)
![image](https://github.com/user-attachments/assets/ffc48b1b-9e9f-4fa4-badd-b35f18605f26)
![image](https://github.com/user-attachments/assets/451220e1-84c2-4161-af51-5dbf84d05080)
![image](https://github.com/user-attachments/assets/261d7ce2-3271-46f5-8509-64377bedbcb0)
![image](https://github.com/user-attachments/assets/4c143ab3-3415-486d-8d24-dcd116de5041)
![image](https://github.com/user-attachments/assets/ea8c9d5d-1a76-4f82-b8df-eff67d3b1814)
![image](https://github.com/user-attachments/assets/67d0e9e9-c38e-408a-9fd1-8cd56cf9a4f2)
![image](https://github.com/user-attachments/assets/061a13ed-b8ae-4da7-b55e-19974a06989b)
![image](https://github.com/user-attachments/assets/9a33e0bc-3ff6-4620-834e-8f4137f876a3)














