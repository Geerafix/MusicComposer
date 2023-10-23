import java.io.*;
import java.util.*;
import javax.sound.midi.*;
import com.googlecode.lanterna.input.*;
import com.googlecode.lanterna.screen.*;

public class Compose {
    private Screen screen;
    private ScreenManipulate sm;
    private int[] notes = {
            0, 24, 25, 26, 27, 28, 29, 30, 31, 32,
            33, 34, 35, 36, 37, 38, 39, 40, 41, 42,
            43, 44, 45, 46, 47, 48, 49, 50, 51, 52,
            53, 54, 55, 56, 57, 58, 59, 60, 61, 62,
            63, 64, 65, 66, 67, 68, 69, 70, 71, 72,
            73, 74, 75, 76, 77, 78, 79, 80, 81, 82,
            83, 84, 85, 86, 87, 88, 89, 90, 91, 92,
            93, 94, 95, 96, 97, 98, 99, 100, 101, 102
    };

    public Compose(Screen screen, ScreenManipulate sm) {
        this.screen = screen;
        this.sm = sm;
    }

    public void compose() throws MidiUnavailableException, InterruptedException, IOException {
        ArrayList<Note> track = new ArrayList<>();
        StringBuilder filename = new StringBuilder("");
        Synthesizer synth = MidiSystem.getSynthesizer();
        MidiChannel[] channels = synth.getChannels();
        int channel = 0, y = 0, trackX = 75, trackY = 4, position = 0, currentNote = 1, currentDuration = 500, lastChar = 11;
        boolean run = true;

        synth.open();
        screen.clear();
        sm.readerYellow(new Scanner(new File("scenes/compose.txt")), 2, y + 3);
        sm.readerWhite(new Scanner(new File("scenes/instructionCompose.txt")), 2, y + 3);
        sm.noteToASCIIArt(sm.noteConv(notes[currentNote]));
        sm.durationToASCIIArt(currentDuration);
        sm.putWhite(2, 1, "Filename: ");
        sm.putWhite(70, 1, "Position: " + (position + 1));
        sm.putWhite(85, 1, "Note count: " + (track.size()));
        screen.refresh();

        while (run) {
            Key key = screen.readInput();
            while (key == null) {
                key = screen.readInput();
            }

            switch (key.getKind()) {
                case ArrowDown:
                    if (currentNote == 0) {
                        currentNote = notes.length - 1;
                    } else {
                        --currentNote;
                    }
                    sm.clearNote();
                    sm.noteToASCIIArt(sm.noteConv(notes[currentNote]));
                    sm.selectNoteDown(2, 18);
                    screen.refresh();
                    break;
                case ArrowUp:
                    if (currentNote == notes.length - 1) {
                        currentNote = 0;
                    } else {
                        ++currentNote;
                    }
                    sm.clearNote();
                    sm.noteToASCIIArt(sm.noteConv(notes[currentNote]));
                    sm.selectNoteUp(2, 4);
                    screen.refresh();
                    break;
                case PageUp:
                    if (currentDuration != 5000) {
                        currentDuration += 50;
                    }
                    sm.clearDuration();
                    sm.durationToASCIIArt(currentDuration);
                    sm.selectNoteUp(33, 4);
                    screen.refresh();
                    break;
                case PageDown:
                    if (currentDuration != 0) {
                        currentDuration -= 50;
                    }
                    sm.clearDuration();
                    sm.durationToASCIIArt(currentDuration);
                    sm.selectNoteDown(33, 18);
                    screen.refresh();
                    break;
                case Tab:
                    if (position == track.size() && track.size() < 60) {
                        track.add(new Note(notes[currentNote], currentDuration));
                        position += 1;
                        currentNote = 1;
                        currentDuration = 500;
                        sm.clearNote();
                        sm.clearDuration();
                        sm.noteToASCIIArt(sm.noteConv(notes[currentNote]));
                        sm.durationToASCIIArt(currentDuration);
                        sm.putWhite(80, 1, Integer.toString(position + 1));
                        sm.putWhite(97, 1, Integer.toString(track.size()));
                    } else if (position < track.size()) {
                        track.set(position, new Note(notes[currentNote], currentDuration));
                    }
                    sm.refreshTrack(trackX, trackY, track);
                    screen.refresh();
                    break;
                case Escape:
                    Title title = new Title(screen, sm);
                    title.title();
                    break;
                case Insert:
                    if (currentNote > 0) {
                        channels[channel].noteOn(notes[currentNote], 100);
                        Thread.sleep(currentDuration);
                        channels[channel].noteOff(notes[currentNote]);
                    } else {
                        Thread.sleep(currentDuration);
                    }
                    break;
                case Home:
                    if (position > 0) {
                        position -= 1;
                        currentNote = track.get(position).getNumber() - 23;
                        currentDuration = track.get(position).getDuration();
                        sm.clearNote();
                        sm.clearDuration();
                        sm.noteToASCIIArt(sm.noteConv(track.get(position).getNumber()));
                        sm.durationToASCIIArt(track.get(position).getDuration());
                    }
                    sm.putWhite(80, 1, "   ");
                    sm.putWhite(80, 1, Integer.toString(position + 1));
                    screen.refresh();
                    break;
                case End:
                    if (position < track.size()) {
                        position += 1;
                    }
                    if (position < track.size()) {
                        currentNote = track.get(position).getNumber() - 23;
                        currentDuration = track.get(position).getDuration();
                        sm.clearNote();
                        sm.clearDuration();
                        sm.noteToASCIIArt(sm.noteConv(track.get(position).getNumber()));
                        sm.durationToASCIIArt(track.get(position).getDuration());
                    }
                    if (position == track.size()) {
                        currentNote = 1;
                        currentDuration = 500;
                        sm.clearNote();
                        sm.clearDuration();
                        sm.noteToASCIIArt(sm.noteConv(notes[currentNote]));
                        sm.durationToASCIIArt(currentDuration);
                    }
                    sm.putWhite(80, 1, "   ");
                    sm.putWhite(80, 1, Integer.toString(position + 1));
                    screen.refresh();
                    break;
                case Delete:
                    if (position < track.size()) {
                        track.remove(position);
                        currentNote = 1;
                        currentDuration = 500;
                        position = track.size();
                        sm.clearNote();
                        sm.clearDuration();
                        sm.noteToASCIIArt(sm.noteConv(notes[currentNote]));
                        sm.durationToASCIIArt(currentDuration);
                        sm.putWhite(97, 1, "   ");
                        sm.putWhite(97, 1, Integer.toString(track.size()));
                        sm.putWhite(80, 1, Integer.toString(position + 1));
                        sm.refreshTrack(trackX, trackY, track);
                        screen.refresh();
                    }
                    break;
                case NormalKey:
                    if (lastChar < 27) {
                        filename.append(key.getCharacter());
                        sm.putWhite(12, 1, filename.toString());
                        lastChar++;
                    }
                    screen.refresh();
                    break;
                case Backspace:
                    if (lastChar > 11) {
                        filename.delete(filename.toString().length() - 1, filename.toString().length());
                        sm.putWhite(lastChar, 1, " ");
                        sm.putWhite(12, 1, filename.toString());
                        lastChar--;
                    }
                    screen.refresh();
                    break;
                case Enter:
                    if (Arrays.asList(new File("tracks").listFiles()).size() < 19 && track.size() > 2) {
                        sm.saveThread();
                        FileWriter fileWriter = new FileWriter("tracks/" + filename.toString() + ".txt");
                        PrintWriter printWriter = new PrintWriter(fileWriter);
                        for (Note note : track) {
                            printWriter.println(Integer.toString(note.getNumber()));
                            printWriter.println(Integer.toString(note.getDuration()));
                        }
                        printWriter.close();
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
