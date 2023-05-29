[![Build Flow](https://github.com/HRV-Mart/Backend-Review/actions/workflows/build.yaml/badge.svg)](https://github.com/HRV-Mart/Backend-Review/actions/workflows/build.yaml)
![Docker Pulls](https://img.shields.io/docker/pulls/harsh3305/hrv-mart-backend-review)
![Docker Image Size (latest by date)](https://img.shields.io/docker/image-size/harsh3305/hrv-mart-backend-review)
![Docker Image Version (latest by date)](https://img.shields.io/docker/v/harsh3305/hrv-mart-backend-review)
## Set up application locally
```
git clone https://github.com/HRV-Mart/Backend-Review.git
gradle clean build
```
## Set up application using Docker
```
docker run  --name HRV-Mart-Backend-Review -it --init --net="host" -d harsh3305/hrv-mart-backend-review:latest
```