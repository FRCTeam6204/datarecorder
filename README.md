# Data Recorder
Data Recorder is a small Java library that provides easy recording and playback of data.

## DataRecorder
```java
DataRecorder recorder = new DataRecorder();
recorder.recordDouble("Example Double", someObject::doubleSupplier);
```
When supplied with a (type)-returning method (in this example, a double-returning method), DataRecorder will record the output of that method every time `record()` is called. The recorded data can be accessed via `getRecords()` or `getRecorderByName()`, e.g. `recorder.getRecorderByName(DataType.Double, "Example Double")`.

DataRecorder also provides an automatic recording system. The `startRecording()` method records data at a fixed rate, defined using `setSamplingRate()` (50 Hz default). Recording is stopped via `stopRecording()`.

Six types of objects can be recorded: Doubles, Strings, Booleans, and Lists (`java.util.List`) of each.

Recorded data can be written to a CSV file via `save()`.

## DataPlayer
DataPlayer is essentially the inverse of DataRecorder.
```java
DataPlayer player = new DataPlayer();
List<Double> data = (List<Double>) recorder.getRecorderByName(DataType.Double, "Example Double").getRecord();
player.addDoublePlayer(data, someObject::doubleConsumer);
```
When supplied with a List and a (type)-accepting method (in this example, a double-accepting method), DataPlayer will supply that method with the next element in the List every time `play()` is called. Players can be accessed via `getPlayers()` or `getPlayerByName()`, in the same way as DataRecorder.

An automatic playback system is provided through `startPlayback()` and `stopPlayback()`. The output rate is defined using `setOutputRate()` (50 Hz default). Playback will automatically stop once all players have finished iteration.

The same objects supported by DataRecorder are supported by DataPlayer.

Data can be read from CSV files via `read()`. **Note:** Any data read from a CSV file will be interpreted as String data. Also, after reading from a CSV, each player will need to be provided with a consuming method using `Player.setOutputFunction()`.
```java
player.read("data.csv");
player.getPlayerByName(DataType.String, "Example Double").setOutputFunction( s -> {
    someObject.doubleConsumer(Double.parseDouble((String) s));
    });
```

---

This project was originally written for use in the 2018 season of the FIRST Robotics Competition, by team 6204.
