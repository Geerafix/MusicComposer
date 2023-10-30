import java.io.*;
import java.util.*;
import javax.sound.midi.*;
import com.googlecode.lanterna.input.*;
import com.googlecode.lanterna.screen.*;

public class Title {
    private Screen screen;
    private ScreenManipulate sm;
    public Title(Screen screen, ScreenManipulate sm) {
        this.screen = screen;
        this.sm = sm;
    }

    public void title() throws MidiUnavailableException, InterruptedException, IOException {
        boolean run = true;

        screen.clear();
        sm.readerYellow(new Scanner(new File("scenes/title.txt")), 10, 0);
        sm.putBlue(80, 34, "by Adam Grzeszczuk");
        screen.refresh();

        while (run) {
            Key key = screen.readInput();
            while (key == null) {
                key = screen.readInput();
            }

            switch (key.getKind()) {
                case ArrowRight:
                    Compose compose = new Compose(screen, sm);
                    compose.compose();
                    break;
                case ArrowLeft:
                    Tracks tracks = new Tracks(screen, sm);
                    tracks.tracks();
                    break;
                case Escape:
                    screen.stopScreen();
                    System.exit(0);
                    break;
                default:
                    break;
            }
        }
    }
}
