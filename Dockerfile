FROM bimgbily/centos-java:latest
WORKDIR /root
ADD highload-all-1.0-SNAPSHOT.jar .
EXPOSE 80
CMD java -jar highload-all-1.0-SNAPSHOT.jar -Xms4096m -Xmx4096m -XX:CompileThreshold=100 -XX:+UseConcMarkSweepGC