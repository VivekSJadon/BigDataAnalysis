# BD24_Project_A9_C: A9_Radiations_Docker

## DOCKER
### Setting up the Docker Desktop:
Follow the steps on this link to set up Docker and Docker Desktop on your Windows/Mac/Linux device: [https://docs.docker.com/get-docker/](https://docs.docker.com/get-docker/).

## Dockerhub Repository Links:
1. [nikhilphadke24/my-producer-improved:latest](https://hub.docker.com/repository/docker/nikhilphadke24/my-producer-improved/general)
2. [nikhilphadke24/my-custom-mysql:latest](https://hub.docker.com/repository/docker/nikhilphadke24/my-custom-mysql/general)
3. [nikhilphadke24/flink:1.17.2-scala_2.12](https://hub.docker.com/repository/docker/nikhilphadke24/flink/general)
4. [nikhilphadke24/zookeeper:latest](https://hub.docker.com/repository/docker/nikhilphadke24/zookeeper/general)
5. [nikhilphadke24/kafka:latest](https://hub.docker.com/repository/docker/nikhilphadke24/kafka/general)
6. [nikhilphadke24/my-flink-job:latest](https://hub.docker.com/repository/docker/nikhilphadke24/my-flink-job/general)
7. [nikhilphadke24/my-frontend-flask:latest](https://hub.docker.com/repository/docker/nikhilphadke24/my-frontend-flask/general)

## Getting started with running the application:
1. Open terminal in this root folder and run the command `docker-compose up -d`. 
2. This will pull the necessary docker images of Apache Kafka, Zookeeper, Kafka-Producer, MySQL Database, Apache Flink, Job_Submitter, Flask-App, etc.
3. After the images are pulled, the images will start running. 
4. The Kafka image will create the required topics, the Kafka-Producer will produce messages to the respective topics, the MySQL image will create the required tables, the flask-app image will provide a link to the front end and the job-submitter image will send a job to the flink server after waiting for 100 seconds.
5. Meanwhile, the producer will produce messages to the respective kafka topics and after these 100 seconds, the job-submitter will send a job to the flink server. To check the flink dashboard, go to your browser and navigate to the link 'localhost:8081'.
6. This flink server will then do all the stream processing at the backend and write data to the MySQL tables. 
7. Meanwhile, open logs of the image 'flask-app' to get the link to the front end.
8. You will see the following link in the log terminal: http://127.0.0.1:5000
9. Click on this link to access the UI map dashboard of this application. 
10. You will see points being plotted with different colours based on their radiation value.
11. In the dropdown, you can select a specific continent or all of the continents to display the plotting.
12. You can also view the maximum, minimum, average and total radiation on the dashboard.

## Turning everything off:
1. Make sure to turn off all the running docker images and containers.
2. Close all the webpages opened.

## Troubleshooting:
1. It might happen that the job-submitter sends the flink job before other images are fully configured and running, which will lead to the failure of the job.
2. In such cases, simply run the job-submitter again after a couple of minutes to give enough time to other images.
3. In case the kafka-producer raises an error of Geocoder Server Timeout, simply run that image again and it will start producing messages to the topics.