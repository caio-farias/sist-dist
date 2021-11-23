
clean:
	rm -f src/*/*.class && cd bin && rm *.class

compile:
	javac src/*/*.java -d ./bin -cp ./bin && javac -cp ./bin src/*.java -d ./bin

start-main:
	make compile && cd bin && java Main $(PORT)

start-proxy:
	make compile && cd bin && java Proxy $(PORT)

start-backup:
	make compile && cd bin && java MainBackup $(PORT_BACKEND))

start-backend:
	make compile && cd bin && java MainBackend $(PORT_BACKEND))

start-db:
	make compile && cd bin && java MainDB $(PORT_DB)