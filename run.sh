./gradlew bootJar
SERVER_PORT=80 java \
  -Dcom.sun.management.jmxremote \
  -Dcom.sun.management.jmxremote.port=7799 \
  -Dcom.sun.management.jmxremote.rmi.port=7799 \
  -Dcom.sun.management.jmxremote.local.only=false \
  -Dcom.sun.management.jmxremote.authenticate=false \
  -Dcom.sun.management.jmxremote.ssl=false \
  -Djava.rmi.server.hostname=hackserver.allegrogroup.com \
  -Djmx.rmi.registry.port=6001 \
  -jar build/libs/hackserver-0.0.1-SNAPSHOT.jar > /var/log/hackserver.log
