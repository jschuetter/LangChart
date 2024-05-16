pushd src
javac *.java
jar cvfe StudyChart.jar Driver *.class
mv StudyChart.jar ..
