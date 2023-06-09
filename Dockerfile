FROM openjdk:19
ARG JAR_FILE=build/libs/Backend-Review-0.0.1-SNAPSHOT.jar
ARG MONGODB_URI=mongodb://localhost:27017
ARG APPLICATION_PORT=8086
ARG USER_URL=http://localhost:8080/user
ENV MONGODB_URI=$MONGODB_URI
ENV APPLICATION_PORT=$APPLICATION_PORT
ENV PRODUCT_URL=$PRODUCT_URL
ENV USER_URL=$USER_URL
EXPOSE 8086
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]