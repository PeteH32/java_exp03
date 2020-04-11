
buildit:
	./gradlew build

runserver:
	java -cp ./build/classes/java/main java_exp03.Main

runsenders:
	java -cp ./build/classes/java/main java_exp03.tester.DataSenders 10000
