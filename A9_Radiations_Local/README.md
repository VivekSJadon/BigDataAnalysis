# BD24_Project_A9_C: A9_Radiations_Local

## DOCKER
### Setting up the Docker Desktop:
Follow the steps on this link to set up Docker and Docker Desktop on your Windows/Mac/Linux device: [https://docs.docker.com/get-docker/](https://docs.docker.com/get-docker/).

## PYTHON
### Setting up Python on your device:
1. Follow the steps on this link to get python running on your Windows/Mac/Linux device: [https://www.python.org/downloads/](https://www.python.org/downloads/).
2. Open terminal in this root folder and run the command `pip install -r requirements.txt`.

## Getting started with running the application:
1. Open terminal in this root folder and run the command `docker-compose up -d`. 
2. This will pull the necessary docker images of Apache Kafka, Zookeeper, MySQL Database and Apache Flink.
3. After the images are pulled, the images will start running. 
4. The Kafka image will create the required topics, the MySQL image will create the required tables and the job-submitter image will send a job to the flink server after waiting for 100 seconds.
5. In these 100 seconds, open terminal in the 'kafka/kafka-producer' and run the following command `python producer.py`.
6. Now, the producer will produce messages to the respective kafka topics and after these 100 seconds, the job-submitter will send a job to the flink server. To check the flink dashboard, go to your browser and navigate to the link 'localhost:8081'.
7. This flink server will then do all the stream processing at the backend and write data to the MySQL tables. 
8. Meanwhile, open terminal in the 'front-end\' and run the following command `python mainweb.py`.
9. Once this code runs, you will get the following link in the terminal: http://127.0.0.1:5000
10. Click on this link to access the UI map dashboard of this application. 
11. You will see points being plotted with different colours based on their radiation value.
12. In the dropdown, you can select a specific continent or all of the continents to display the plotting.
13. You can also view the maximum, minimum, average and total radiation on the dashboard.

## Turning everything off:
1. Make sure to turn off all the running docker images and containers.
2. Also, stop the two python files that are running.
3. Close all the webpages opened.

## Troubleshooting:
1. It might happen that the job-submitter sends the flink job before other images are fully configured and running, which will lead to the failure of the job.
2. In such cases, simply run the job-submitter again after a couple of minutes to give enough time to other images.