import java.io.*;
import java.util.*;
import com.googlecode.lanterna.screen.*;
import com.googlecode.lanterna.terminal.*;

public class ScreenManipulate {
    private Screen screen;

    public ScreenManipulate(Screen screen) {
        this.screen = screen;
    }

    public String noteConv(int noteNumber) {
        String[] noteNames = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
        int octave = noteNumber / 12, noteInOctave = noteNumber % 12;
        if (octave - 1 < 0) {
            return "-";
        }
        return noteNames[noteInOctave] + (octave - 1);
    }

    public void noteToASCIIArt(String stream) throws FileNotFoundException {
        String note = String.valueOf(stream);
        Scanner scan;
        int x = 3, y = 12;
        for (char digit : note.toCharArray()) {
            scan = new Scanner(new File("characters/" + digit + ".txt"));
            readerWhite(scan, x, y);
            x = x + 9;
            screen.refresh();
        }
    }

    public void durationToASCIIArt(int stream) throws FileNotFoundException {
        String note = String.valueOf(stream);
        Scanner scan;
        int x = 34, y = 12;
        for (char digit : note.toCharArray()) {
            scan = new Scanner(new File("characters/" + digit + ".txt"));
            readerWhite(scan, x, y);
            screen.refresh();
            x = x + 9;
        }
    }

    public void clearNote() {
        int x = 3, y = 12;
        while (y < 17) {
            putWhite(x, y, "                           ");
            y++;
        }
    }

    public void clearDuration() {
        int x = 34, y = 12;
        while (y < 17) {
            putWhite(x, y, "                                   ");
            y++;
        }
    }

    public void displayTracks(List<File> tracks) {
        for (int trackNr = 0; trackNr < tracks.size(); trackNr++) {
            putWhite(14, trackNr + 10, trackNr + 1 + ". "
                    + tracks.get(trackNr).getName().substring(0, tracks.get(trackNr).getName().length() - 4));
        }
    }

    public void refreshTrack(int trackX, int trackY, ArrayList<Note> track) {
        for (int y = 0; y < 30; y++) {
            putWhite(75, y + 4, "                               ");
        }
        for (int i = 0; i < track.size(); i++) {
            if (trackY == 34) {
                trackX += 13;
                trackY = 4;
            }
            putWhite(trackX, trackY, (i + 1) + ". ");
            putWhite((trackX + 3), trackY, noteConv(track.get(i).getNumber()));
            putWhite((trackX + 7), trackY, Integer.toString(track.get(i).getDuration()));
            trackY += 1;
        }
    }

    public void refreshTracks(List<File> tracks) {
        for (int i = 0; i < tracks.size() + 1; i++) {
            putWhite(12, i + 10, "                     ");
        }
        displayTracks(tracks);
    }

    public void readerYellow(Scanner scan, int x, int y) {
        while (scan.hasNext()) {
            String part = scan.nextLine();
            putYellow(x, y, part);
            y++;
        }
    }

    public void readerWhite(Scanner scan, int x, int y) {
        while (scan.hasNext()) {
            String part = scan.nextLine();
            putWhite(x, y, part);
            y++;
        }
    }

    public void putWhite(int x, int y, String text) {
        screen.putString(x, y, text,
                Terminal.Color.WHITE, Terminal.Color.BLACK, ScreenCharacterStyle.Bold);
    }

    public void putBlue(int x, int y, String text) {
        screen.putString(x, y, text,
                Terminal.Color.BLUE, Terminal.Color.BLACK, ScreenCharacterStyle.Bold);
    }

    public void putYellow(int x, int y, String text) {
        screen.putString(x, y, text,
                Terminal.Color.YELLOW, Terminal.Color.BLACK, ScreenCharacterStyle.Bold);
    }

    public void selectNoteUp(int x, int y) throws FileNotFoundException, InterruptedException {
        readerYellow(new Scanner(new File("scenes/glowingUp.txt")), x, y);
        screen.refresh();
        Thread.sleep(25);
        readerYellow(new Scanner(new File("scenes/classicUp.txt")), x, y);
    }

    public void selectNoteDown(int x, int y) throws FileNotFoundException, InterruptedException {
        readerYellow(new Scanner(new File("scenes/glowingDown.txt")), x, y);
        screen.refresh();
        Thread.sleep(30);
        readerYellow(new Scanner(new File("scenes/classicDown.txt")), x, y);
    }

    public void saveThread() {
        Thread thread = new Thread(() -> {
            screen.putString(1, 1, "●", Terminal.Color.GREEN, Terminal.Color.BLACK,
                    ScreenCharacterStyle.Bold);
            screen.refresh();
            try {
                Thread.sleep(700);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            putWhite(1, 1, " ");
            screen.refresh();
        });
        thread.start();
    }

    public void alert(boolean bool) {
        if (bool == true) {
            Thread thread = new Thread(() -> {
                screen.putString(36, 1, "Add some notes (min. 3)", Terminal.Color.RED, Terminal.Color.BLACK,
                        ScreenCharacterStyle.Bold);
                screen.refresh();
                try {
                    Thread.sleep(2800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                putWhite(36, 1, "                       ");
                screen.refresh();
            });
            thread.start();
        } else if (bool == false) {
            Thread thread = new Thread(() -> {
            screen.putString(39, 1, "Delete some track", Terminal.Color.RED, Terminal.Color.BLACK,
                    ScreenCharacterStyle.Bold);
            screen.refresh();
            try {
                Thread.sleep(2800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            putWhite(39, 1, "                 ");
            screen.refresh();
        });
        thread.start();
        }
    }
}