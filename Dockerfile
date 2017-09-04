FROM bimgbily/centos-java:latest
WORKDIR /root
ADD libs/gs-rest-service-0.1.0.jar .
EXPOSE 80
CMD java -jar gs-rest-service-0.1.0.jar