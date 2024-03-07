FROM eclipse-temurin:21.0.1_12-jre

# Install software packages
RUN apt-get update && apt-get install -y sudo git tar gzip unzip xz-utils
# Install Java and set env variables
ARG JDK_URL="https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.9%2B9/OpenJDK17U-jdk_x64_linux_hotspot_17.0.9_9.tar.gz"
RUN wget -qO- $JDK_URL | tar -xz -C /usr/share/ && \
    mv /usr/share/jdk-* /usr/share/jdk
ENV JDK_HOME=/usr/share/jdk
# Install Maven and set env variables
ENV MAVEN_HOME=/usr/share/maven
ARG MAVEN_VERSION=3.8.4
ARG MAVEN_URL=https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz
RUN wget -qO- $MAVEN_URL | tar -xz -C /usr/share/ && \
    mv /usr/share/apache-maven-* $MAVEN_HOME
# Clean up
RUN apt-get remove --purge -y unzip xz-utils &&  \
    apt-get -y autoclean

# Hold the container open indefinitely \
ENTRYPOINT ["sleep"]
CMD ["infinity"]
