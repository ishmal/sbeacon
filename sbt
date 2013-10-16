# java -Dcom.sun.management.jmxremote.port=3333 \
#     -Dcom.sun.management.jmxremote.ssl=false \
#      -Dcom.sun.management.jmxremote.authenticate=false \
java -Xms512M -Xmx1536M -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=384M -jar `dirname $0`/sbt-launch.jar "$@"
