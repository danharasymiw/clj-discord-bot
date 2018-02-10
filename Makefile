start:
	- pkill -f java
	lein uberjar
	java -jar target/clj-discord-bot-*standalone.jar > /dev/null 2>&1 &

