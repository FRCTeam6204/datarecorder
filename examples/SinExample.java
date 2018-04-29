import org.team6204.frc.datarecorder.DataRecorder;
import org.team6204.frc.datarecorder.Recorder;
import org.team6204.frc.datarecorder.DataPlayer;
import org.team6204.frc.datarecorder.Player;
import org.team6204.frc.datarecorder.DataType;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.io.IOException;

/**
 * Prints a sine wave and records the values
 */

public class SinExample {
    private static final long FREQ = 20;
    private static final int INC = 5;
    private static final int LIMIT = 360 * INC;
    private static final int A = 10;
    private double x;
    private double y;
    private static SinExample example = new SinExample();
    private static DataRecorder recorder = new DataRecorder();
    private static DataPlayer player = new DataPlayer();
    private static String filename = "";

    public static void main(String[] args) {
        if (args.length > 0) {
            filename = args[0];
        }
        System.out.print("Starting recording");
        try {
            Thread.sleep(333);
            System.out.print(".");
            Thread.sleep(333);
            System.out.print(".");
            Thread.sleep(333);
            System.out.println(".");
        } catch (InterruptedException e) {
            System.exit(0);
        }
        recorder.recordNumber("X", example::getX);
        recorder.recordNumber("Sin of X", example::getY);

        Timer timer = new Timer();
        recorder.startRecording();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                example.setY(Math.sin(Math.PI * example.getX() / 180));
                example.draw();
                if (example.getX() >= LIMIT) {
                    recorder.stopRecording();
                    timer.cancel();
                    try {
                        if (filename.length() > 0) {
                            recorder.save(filename);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException();
                    }
                    if (filename.length() > 0) {
                        startPlaybackFromFile();
                    } else {
                        startPlayback();
                    }
                }
            }
        }, 0, FREQ);
    }

    @SuppressWarnings("unchecked")
    public static void startPlayback() {
        System.out.print("Starting playback");
        player.addDoublePlayer(
                (List<Double>) recorder.getRecorderByName(DataType.Number, "Sin of X").getRecord(),
                (Double d) -> { example.setY(d); example.draw(); }
                );
        try {
            Thread.sleep(333);
            System.out.print(".");
            Thread.sleep(333);
            System.out.print(".");
            Thread.sleep(333);
            System.out.println(".");
        } catch (InterruptedException e) {
            System.exit(0);
        }
        player.startPlayback();
    }

    public static void startPlaybackFromFile() {
        System.out.print("Reading " + filename + " and starting playback");
        try {
            player.read(filename);
        } catch (IOException e) {
            throw new RuntimeException();
        }
        player.getPlayerByName(DataType.String, "Sin of X").setOutputFunction( s -> { example.setY(Double.parseDouble((String) s)); example.draw(); } );
        // Do nothing with X
        player.getPlayerByName(DataType.String, "X").setOutputFunction( s -> {return;} );
        try {
            Thread.sleep(333);
            System.out.print(".");
            Thread.sleep(333);
            System.out.print(".");
            Thread.sleep(333);
            System.out.println(".");
        } catch (InterruptedException e) {
            System.exit(0);
        }

        player.startPlayback();

    }

    public void draw() {
        int pad = 2 + A + (int) Math.round(y * A);
        System.out.println(
                String.format(
                        "%" + pad + "s",
                        "*"
                )
        );
        x += INC;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setY(double _y) {
        y = _y;
    }
}
