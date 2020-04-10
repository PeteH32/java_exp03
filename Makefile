
buildit:
	./gradlew build

runserver:
	java -cp ./build/classes/java/main java_exp03.Server

runsenders:
	java -cp ./build/classes/java/main java_exp03.DataSenders
