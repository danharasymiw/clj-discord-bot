start:
    lein uberjar
    - pkill -f java
    java -jar target/clj-discord-bot-*standalone.jar > /dev/null 2>&1 &