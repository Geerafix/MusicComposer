import java.io.*;
import java.util.*;
import javax.sound.midi.*;
import com.googlecode.lanterna.*;
import com.googlecode.lanterna.input.*;
import com.googlecode.lanterna.screen.*;
import com.googlecode.lanterna.terminal.*;

public class MusicComposer {
    private static Screen screen = TerminalFacade.createScreen();
    private static int[] notes = {
            0, 24, 25, 26, 27, 28, 29, 30, 31, 32,
            33, 34, 35, 36, 37, 38, 39, 40, 41, 42,
            43, 44, 45, 46, 47, 48, 49, 50, 51, 52,
            53, 54, 55, 56, 57, 58, 59, 60, 61, 62,
            63, 64, 65, 66, 67, 68, 69, 70, 71, 72,
            73, 74, 75, 76, 77, 78, 79, 80, 81, 82,
            83, 84, 85, 86, 87, 88, 89, 90, 91, 92,
            93, 94, 95, 96, 97, 98, 99, 100, 101, 102
    };

    public static void main(String[] args) throws MidiUnavailableException, InterruptedException, IOException {
        screen.startScreen();
        screen.setCursorPosition(null);
        title();
    }

    public static void title() throws MidiUnavailableException, InterruptedException, IOException {
        boolean run = true;

        screen.clear();
        reader(new Scanner(new File("scenes/title.txt")), 10, 0);
        put(80, 28, "by Adam Grzeszczuk");
        screen.refresh();

        while (run) {
            Key key = screen.readInput();
            while (key == null) {
                key = screen.readInput();
            }

            switch (key.getKind()) {
                case ArrowRight:
                    compose();
                    break;
                case ArrowLeft:
                    tracks();
                    break;
                case Escape:
                    screen.stopScreen();
                    break;
                default:
                    break;
            }
        }
    }

    public static void compose() throws MidiUnavailableException, InterruptedException, IOException {
        ArrayList<Note> track = new ArrayList<>();
        StringBuilder filename = new StringBuilder("");
        Synthesizer synth = MidiSystem.getSynthesizer();
        MidiChannel[] channels = synth.getChannels();
        int channel = 0, y = 0, trackX = 75, trackY = 4, position = 0, currentNote = 1, currentDuration = 200, lastChar = 11;
        boolean run = true;

        synth.open();
        screen.clear();
        reader(new Scanner(new File("scenes/compose.txt")), 2, y + 3);
        noteToASCIIArt(noteConv(notes[currentNote]));
        durationToASCIIArt(currentDuration);
        put(2, 1, "Filename: ");
        put(70, 1, "Position: " + (position + 1));
        put(85, 1, "Note count: " + (track.size()));
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
                    clearNote();
                    noteToASCIIArt(noteConv(notes[currentNote]));
                    screen.refresh();
                    break;
                case ArrowUp:
                    if (currentNote == notes.length - 1) {
                        currentNote = 0;
                    } else {
                        ++currentNote;
                    }
                    clearNote();
                    noteToASCIIArt(noteConv(notes[currentNote]));
                    screen.refresh();
                    break;
                case PageUp:
                    if (currentDuration != 5000) {
                        currentDuration += 50;
                    }
                    clearDuration();
                    durationToASCIIArt(currentDuration);
                    screen.refresh();
                    break;
                case PageDown:
                    if (currentDuration != 0) {
                        currentDuration -= 50;
                    }
                    clearDuration();
                    durationToASCIIArt(currentDuration);
                    screen.refresh();
                    break;
                case Tab:
                    if (position == track.size() && track.size() < 50) {
                        track.add(new Note(notes[currentNote], currentDuration));
                        position += 1;
                        currentNote = 1;
                        currentDuration = 200;
                        clearNote();
                        clearDuration();
                        noteToASCIIArt(noteConv(notes[currentNote]));
                        durationToASCIIArt(currentDuration);
                        put(80, 1, Integer.toString(position + 1));
                        put(97, 1, Integer.toString(track.size()));
                    } else if (position < track.size()) {
                        track.set(position, new Note(notes[currentNote], currentDuration));
                    }
                    refreshTrack(trackX, trackY, track);
                    screen.refresh();
                    break;
                case Escape:
                    title();
                    break;
                case Insert:
                    channels[channel].noteOn(notes[currentNote], 100);
                    Thread.sleep(currentDuration);
                    channels[channel].noteOff(notes[currentNote]);
                    break;
                case Home:
                    if (position > 0) {
                        position -= 1;
                        currentNote = track.get(position).getNumber() - 23;
                        currentDuration = track.get(position).getDuration();
                        clearNote();
                        clearDuration();
                        noteToASCIIArt(noteConv(track.get(position).getNumber()));
                        durationToASCIIArt(track.get(position).getDuration());
                    }
                    put(80, 1, "   ");
                    put(80, 1, Integer.toString(position + 1));
                    screen.refresh();
                    break;
                case End:
                    if (position < track.size()) {
                        position += 1;
                    }
                    if (position < track.size()) {
                        currentNote = track.get(position).getNumber() - 23;
                        currentDuration = track.get(position).getDuration();
                        clearNote();
                        clearDuration();
                        noteToASCIIArt(noteConv(track.get(position).getNumber()));
                        durationToASCIIArt(track.get(position).getDuration());
                    }
                    if (position == track.size()) {
                        currentNote = 1;
                        currentDuration = 200;
                        clearNote();
                        clearDuration();
                        noteToASCIIArt(noteConv(notes[currentNote]));
                        durationToASCIIArt(currentDuration);
                    }
                    put(80, 1, "   ");
                    put(80, 1, Integer.toString(position + 1));
                    screen.refresh();
                    break;
                case Delete:
                    if (position < track.size()) {
                        track.remove(position);
                        currentNote = 1;
                        currentDuration = 200;
                        position = track.size();
                        clearNote();
                        clearDuration();
                        noteToASCIIArt(noteConv(notes[currentNote]));
                        durationToASCIIArt(currentDuration);
                        put(97, 1, "   ");
                        put(97, 1, Integer.toString(track.size()));
                        put(80, 1, Integer.toString(position + 1));
                        refreshTrack(trackX, trackY, track);
                        screen.refresh();
                    }
                    break;
                case NormalKey:
                    if (lastChar < 27) {
                        filename.append(key.getCharacter());
                        put(12, 1, filename.toString());
                        lastChar++;
                    }
                    screen.refresh();
                    break;
                case Backspace:
                    if (lastChar > 11) {
                        filename.delete(filename.toString().length() - 1, filename.toString().length());
                        put(lastChar, 1, " ");
                        put(12, 1, filename.toString());
                        lastChar--;
                    }
                    screen.refresh();
                    break;
                case Enter:
                    FileWriter fileWriter = new FileWriter("tracks/" + filename.toString() + ".txt");
                    PrintWriter printWriter = new PrintWriter(fileWriter);
                    for (Note note : track) {
                        printWriter.println(Integer.toString(note.getNumber()));
                        printWriter.println(Integer.toString(note.getDuration()));
                    }
                    printWriter.close();
                    break;
                default:
                    break;
            }
        }
    }

    public static void tracks() throws MidiUnavailableException, InterruptedException, IOException {
        File[] tracks = new File("tracks").listFiles();
        Synthesizer synth = MidiSystem.getSynthesizer();
        MidiChannel[] channels = synth.getChannels();
        int channel = 0, select = 0, y = 0;
        boolean run = true;

        screen.clear();
        synth.open();
        reader(new Scanner(new File("scenes/tracks.txt")), 11, y);

        for (int trackNr = 0; trackNr < tracks.length; trackNr++) {
            screen.putString(14, trackNr + 10,
                    trackNr + 1 + ". " + tracks[trackNr].getName().substring(0, tracks[trackNr].getName().length() - 4),
                    Terminal.Color.WHITE,
                    Terminal.Color.BLACK,
                    ScreenCharacterStyle.Bold);
        }
        screen.putString(12, select + 10, ">",
                Terminal.Color.GREEN, Terminal.Color.BLACK, ScreenCharacterStyle.Bold);
        screen.refresh();

        while (run) {
            Key key = screen.readInput();
            while (key == null) {
                key = screen.readInput();
            }

            switch (key.getKind()) {
                case ArrowRight:
                    title();
                    break;
                case ArrowUp:
                    put(12, select + 10, " ");
                    if (select == 0) {
                        select = tracks.length - 1;
                    } else {
                        --select;
                    }
                    screen.putString(12, select + 10, ">",
                            Terminal.Color.GREEN, Terminal.Color.BLACK, ScreenCharacterStyle.Bold);
                    screen.refresh();
                    break;
                case ArrowDown:
                    put(12, select + 10, " ");
                    if (select == tracks.length - 1) {
                        select = 0;
                    } else {
                        ++select;
                    }
                    screen.putString(12, select + 10, ">",
                            Terminal.Color.GREEN, Terminal.Color.BLACK, ScreenCharacterStyle.Bold);
                    screen.refresh();
                    break;
                case Enter:
                    Scanner loadTrack = new Scanner(new File(tracks[select].toString()));
                    StringBuilder content = new StringBuilder("");

                    while (loadTrack.hasNextLine()) {
                        content.append(loadTrack.nextLine() + "\n");
                    }

                    Scanner trackInterpreter = new Scanner(content.toString());

                    while (trackInterpreter.hasNextLine()) {
                        int note = Integer.parseInt(trackInterpreter.nextLine());
                        int dur = Integer.parseInt(trackInterpreter.nextLine());
                        channels[channel].noteOn(note, 100);
                        Thread.sleep(dur);
                        channels[channel].noteOff(note);
                    }
                    break;
                case Insert:
                    edit(toList(tracks[select].getName()), "tracks/" + tracks[select].getName());
                    break;
                default:
                    break;
            }
        }
    }

    public static void edit(ArrayList<Note> track, String fname)
            throws FileNotFoundException, IOException, MidiUnavailableException, InterruptedException {
        boolean run = true;
        int currentNote = track.get(0).getNumber() - 23, currentDuration = track.get(0).getDuration(), position = 0, y = 0,  trackX = 75, trackY = 4;

        screen.clear();         
        reader(new Scanner(new File("scenes/edit.txt")), 2, y + 3);

        for (int i = 0; i < track.size(); i++) {
            if (trackY == 29) {
                trackX += 13;
                trackY = 4;
            }
            put(trackX, trackY, (i + 1) + ". ");
            put((trackX + 3), trackY, noteConv(track.get(i).getNumber()));
            put((trackX + 7), trackY, Integer.toString(track.get(i).getDuration()));
            trackY += 1;
        }
        trackY = 4;
        trackX = 75;

        noteToASCIIArt(noteConv(track.get(0).getNumber()));
        durationToASCIIArt(track.get(0).getDuration());
        put(2, 1, "Filename: " + fname.substring(7, fname.length() - 4));
        put(70, 1, "Position: " + (position + 1));
        put(85, 1, "Note count: " + (track.size()));
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
                    clearNote();
                    noteToASCIIArt(noteConv(notes[currentNote]));
                    screen.refresh();
                    break;
                case ArrowUp:
                    if (currentNote == notes.length - 1) {
                        currentNote = 0;
                    } else {
                        ++currentNote;
                    }
                    clearNote();
                    noteToASCIIArt(noteConv(notes[currentNote]));
                    screen.refresh();
                    break;
                case PageUp:
                    if (currentDuration != 5000) {
                        currentDuration += 50;
                    }
                    clearDuration();
                    durationToASCIIArt(currentDuration);
                    screen.refresh();
                    break;
                case PageDown:
                    if (currentDuration != 0) {
                        currentDuration -= 50;
                    }
                    clearDuration();
                    durationToASCIIArt(currentDuration);
                    screen.refresh();
                    break;
                case Tab:
                    if (position < track.size()) {
                        track.set(position, new Note(notes[currentNote], currentDuration));
                    }
                    refreshTrack(trackX, trackY, track);
                    screen.refresh();
                    break;
                case Home:
                    if (position > 0) {
                        position -= 1;
                        currentNote = track.get(position).getNumber() - 23;
                        currentDuration = track.get(position).getDuration();
                        clearNote();
                        clearDuration();
                        noteToASCIIArt(noteConv(track.get(position).getNumber()));
                        durationToASCIIArt(track.get(position).getDuration());
                        put(80, 1, "   ");
                        put(80, 1, Integer.toString(position + 1));
                    }
                    screen.refresh();
                    break;
                case End:
                    if (position < track.size() - 1) {
                        position += 1;
                    }
                    if (position < track.size()) {
                        currentNote = track.get(position).getNumber() - 23;
                        currentDuration = track.get(position).getDuration();
                        clearNote();
                        clearDuration();
                        noteToASCIIArt(noteConv(track.get(position).getNumber()));
                        durationToASCIIArt(track.get(position).getDuration());
                    }
                    put(80, 1, "   ");
                    put(80, 1, Integer.toString(position + 1));
                    screen.refresh();
                    break;
                case Enter:
                    PrintWriter toFile = new PrintWriter(fname);
                    for (Note note : track) {
                        toFile.println(Integer.toString(note.getNumber()));
                        toFile.println(Integer.toString(note.getDuration()));
                    }
                    toFile.close();
                    break;
                case Escape:
                    tracks();
                    break;
                default:
                    break;
            }
        }
    }

    public static String noteConv(int noteNumber) {
        String[] noteNames = { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
        int octave = noteNumber / 12, noteInOctave = noteNumber % 12;
        if (octave - 1 < 0) {
            return "-";
        }
        return noteNames[noteInOctave] + (octave - 1);
    }

    private static void noteToASCIIArt(String stream) throws FileNotFoundException {
        String note = String.valueOf(stream);
        Scanner scan;
        int x = 3, y = 12;
        for (char digit : note.toCharArray()) {
            scan = new Scanner(new File("characters/" + digit + ".txt"));
            reader(scan, x, y);
            x = x + 9;
            screen.refresh();
        }
    }

    private static void durationToASCIIArt(int stream) throws FileNotFoundException {
        String note = String.valueOf(stream);
        Scanner scan;
        int x = 34, y = 12;
        for (char digit : note.toCharArray()) {
            scan = new Scanner(new File("characters/" + digit + ".txt"));
            reader(scan, x, y);
            screen.refresh();
            x = x + 9;
        }
    }

    private static void clearNote() {
        int x = 3, y = 12;
        while (y < 17) {
            put(x, y, "                           ");
            y++;
        }
    }

    private static void clearDuration() {
        int x = 34, y = 12;
        while (y < 17) {
            put(x, y, "                                   ");
            y++;
        }
    }

    public static void refreshTrack(int trackX, int  trackY, ArrayList<Note> track) {
        for (int y = 0; y < 25; y++) {
            put(75, y + 4, "                               ");
        }
        for (int i = 0; i < track.size(); i++) {
            if (trackY == 29) {
                trackX += 13;
                trackY = 4;
            }
            put(trackX, trackY, (i + 1) + ". ");
            put((trackX + 3), trackY, noteConv(track.get(i).getNumber()));
            put((trackX + 7), trackY, Integer.toString(track.get(i).getDuration()));
            trackY += 1;
        }
    }

    public static ArrayList<Note> toList(String filename) throws FileNotFoundException {
        ArrayList<Note> track = new ArrayList<>();
        Scanner scan = new Scanner(new File("tracks/" + filename));
        while (scan.hasNext()) {
            track.add(new Note(Integer.parseInt(scan.nextLine()), Integer.parseInt(scan.nextLine())));
        }
        return track;
    }

    public static void reader(Scanner scan, int x, int y) {
        while (scan.hasNext()) {
            String part = scan.nextLine();
            put(x, y, part);
            y++;
        }
    }

    public static void put(int x, int y, String text) {
        screen.putString(x, y, text,
                    Terminal.Color.WHITE, Terminal.Color.BLACK, ScreenCharacterStyle.Bold);
    }
}
