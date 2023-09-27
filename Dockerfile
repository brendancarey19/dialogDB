# Start from a base image with Java and Scala
FROM hseeberger/scala-sbt:8u222_1.3.5_2.13.1

# Set the working directory in Docker
WORKDIR /app

# Copy the dependencies file to the working directory
COPY project /app/project
COPY build.sbt /app/
COPY data /app/

# Load the project dependencies
RUN sbt update

# Copy the content of the local src directory to the working directory
COPY src /app/src

# Compile the project
RUN sbt compile

# Command to run on container start
CMD [ "bash" ]