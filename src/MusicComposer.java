import java.io.*;
import javax.sound.midi.*;
import com.googlecode.lanterna.*;
import com.googlecode.lanterna.screen.*;
import com.googlecode.lanterna.terminal.swing.SwingTerminal;

public class MusicComposer {
    public static void main(String[] args) throws MidiUnavailableException, InterruptedException, IOException {
        Screen screen = TerminalFacade.createScreen(new SwingTerminal(100, 36));
        screen.startScreen();
        screen.setCursorPosition(null);
        Title title = new Title(screen, new ScreenManipulate(screen));
        title.title();
    }
}
