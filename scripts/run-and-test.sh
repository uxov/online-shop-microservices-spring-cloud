sh kill-java.sh &&
podman-compose down && podman-compose up -d &&
sh run-jars.sh &&
sleep 15
gradle clean test -p ../spTest -i --tests AddTestData &&
sleep 15
echo '>>>>>> start testing'
gradle clean test -p ../spTest --tests TestAllSuit
