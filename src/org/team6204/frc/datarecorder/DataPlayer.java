package org.team6204.frc.datarecorder;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import java.io.IOException;

public class DataPlayer {
    private List<Player<Double>> doublePlayers = Collections.synchronizedList(new ArrayList<Player<Double>>());
    private List<Player<String>> stringPlayers = Collections.synchronizedList(new ArrayList<Player<String>>());
    private List<Player<Boolean>> booleanPlayers = Collections.synchronizedList(new ArrayList<Player<Boolean>>());
    private List<Player<List<Double>>> doubleArrayPlayers = Collections.synchronizedList(new ArrayList<Player<List<Double>>>());
    private List<Player<List<String>>> stringArrayPlayers = Collections.synchronizedList(new ArrayList<Player<List<String>>>());
    private List<Player<List<Boolean>>> booleanArrayPlayers = Collections.synchronizedList(new ArrayList<Player<List<Boolean>>>());


    private Timer timer;

    /**
     * Output rate in hertz
     */
    private long outputRate = 50;

    public void addDoublePlayer(List<Double> data, Consumer<Double> function) {
        doublePlayers.add(new Player<Double>(data, function));
    }

    public void addStringPlayer(List<String> data, Consumer<String> function) {
        stringPlayers.add(new Player<String>(data, function));
    }

    public void addBooleanPlayer(List<Boolean> data, Consumer<Boolean> function) {
        booleanPlayers.add(new Player<Boolean>(data, function));
    }

    public void addDoubleArrayPlayer(List<List<Double>> data, Consumer<List<Double>> function) {
        doubleArrayPlayers.add(new Player<List<Double>>(data, function));
    }

    public void addStringArrayPlayer(List<List<String>> data, Consumer<List<String>> function) {
        stringArrayPlayers.add(new Player<List<String>>(data, function));
    }

    public void addBooleanArrayPlayer(List<List<Boolean>> data, Consumer<List<Boolean>> function) {
        booleanArrayPlayers.add(new Player<List<Boolean>>(data, function));
    }

    public void play() {
        playAll(doublePlayers);
        playAll(stringPlayers);
        playAll(booleanPlayers);
        playAll(doubleArrayPlayers);
        playAll(stringArrayPlayers);
        playAll(booleanArrayPlayers);
    }

    public void startPlayback() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                play();
                if (!checkAllNext()) timer.cancel();
            }
        }, 0, 1000 * 1/outputRate);
    }

    public void stopPlayback() {
        timer.cancel();
    }

    /**
     * Initialises only string players.
     * The output function may have to be a custom string conversion function.
     */
    public void read(String filename) throws IOException {
        CSV csv = new CSV();
        csv.read(filename);

        csv.getData().forEach( (String k, List<String> v) -> {
            Player<String> player = new Player<String>(v);
            player.setName(k);
            stringPlayers.add(player);
        });
    }

    public List<?> getPlayers(DataType type) {
        switch (type) {
        case Double:
            return doublePlayers;
        case DoubleArray:
            return doubleArrayPlayers;
        case String:
            return stringPlayers;
        case StringArray:
            return stringArrayPlayers;
        case Boolean:
            return booleanArrayPlayers;
        case BooleanArray:
            return booleanArrayPlayers;
        default:
            throw new AssertionError();
        }
    }

    @SuppressWarnings("unchecked")
    public Player<?> getPlayerByName(DataType type, String name) {
        for (Player<?> player : (List<Player<?>>) getPlayers(type)) {
            if (player.getName().equals(name)) {
                return player;
            }
        }
        throw new IllegalArgumentException();
    }

    public long getOutputRate() {
        return outputRate;
    }

    public void setOutputRate(long rate) {
        outputRate = rate;
    }

    private void playAll(List<? extends Player<?>> list) {
        for (Player<?> player : list) {
            player.play();
        }
    }

    private boolean checkAllNext() {
        if (!allHasNext(doublePlayers)) return false;
        if (!allHasNext(stringPlayers)) return false;
        if (!allHasNext(booleanPlayers)) return false;
        if (!allHasNext(doubleArrayPlayers)) return false;
        if (!allHasNext(stringArrayPlayers)) return false;
        if (!allHasNext(booleanArrayPlayers)) return false;
        return true;
    }

    private boolean allHasNext(List<? extends Player<?>> list) {
        for (Player<?> player : list) {
            if (!player.hasNext()) {
                return false;
            }
        }

        return true;
    }

}
