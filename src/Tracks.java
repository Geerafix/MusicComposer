import java.io.*;
import java.util.*;
import javax.sound.midi.*;
import com.googlecode.lanterna.input.*;
import com.googlecode.lanterna.screen.*;
import com.googlecode.lanterna.terminal.*;

public class Tracks {
    private Screen screen;
    private ScreenManipulate sm;
    private boolean[] threadRun = { false };
    private Thread thread;

    public Tracks(Screen screen, ScreenManipulate sm) {
        this.screen = screen;
        this.sm = sm;
    }

    public void tracks() throws MidiUnavailableException, InterruptedException, IOException {
        List<File> tracksList = Arrays.asList(new File("tracks").listFiles());
        Synthesizer synth = MidiSystem.getSynthesizer();
        MidiChannel[] channels = synth.getChannels();
        int channel = 0, select = 0, y = 0;
        boolean run = true;

        screen.clear();
        synth.open();
        sm.readerYellow(new Scanner(new File("scenes/tracks.txt")), 11, y);
        sm.readerWhite(new Scanner(new File("scenes/instructionTracks.txt")), 65, y);
        sm.displayTracks(tracksList);
        if (tracksList.size() > 0) {
            screen.putString(12, select + 10, ">",
                    Terminal.Color.GREEN, Terminal.Color.BLACK, ScreenCharacterStyle.Bold);
        }
        screen.refresh();

        while (run) {
            Key key = screen.readInput();
            while (key == null) {
                key = screen.readInput();
            }

            switch (key.getKind()) {
                case ArrowRight:
                    if (thread != null) {
                        thread.interrupt();
                        synth.close();
                    }
                    Title title = new Title(screen, sm);
                    title.title();
                    break;
                case ArrowUp:
                    sm.putWhite(12, select + 10, " ");
                    if (select == 0) {
                        select = tracksList.size() - 1;
                    } else {
                        --select;
                    }
                    if (tracksList.size() > 0) {
                        screen.putString(12, select + 10, ">",
                                Terminal.Color.GREEN, Terminal.Color.BLACK, ScreenCharacterStyle.Bold);
                    }
                    screen.refresh();
                    break;
                case ArrowDown:
                    sm.putWhite(12, select + 10, " ");
                    if (select == tracksList.size() - 1) {
                        select = 0;
                    } else {
                        ++select;
                    }
                    if (tracksList.size() > 0) {
                        screen.putString(12, select + 10, ">",
                                Terminal.Color.GREEN, Terminal.Color.BLACK, ScreenCharacterStyle.Bold);
                    }
                    screen.refresh();
                    break;
                case Enter:
                    if (threadRun[0] == false && !tracksList.isEmpty()) {
                        threadRun[0] = true;
                        Scanner loadTrack = new Scanner(new File(tracksList.get(select).toString()));
                        StringBuilder content = new StringBuilder("");

                        while (loadTrack.hasNextLine()) {
                            content.append(loadTrack.nextLine() + "\n");
                        }

                        Scanner trackInterpreter = new Scanner(content.toString());
                        thread = new Thread(() -> {
                            try {
                                while (trackInterpreter.hasNextLine()) {
                                    int note = Integer.parseInt(trackInterpreter.nextLine());
                                    int dur = Integer.parseInt(trackInterpreter.nextLine());
                                    if (note == 0) {
                                        Thread.sleep(dur);
                                    } else {
                                        channels[channel].noteOn(note, 100);
                                        Thread.sleep(dur);
                                        channels[channel].noteOff(note);
                                    }
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } finally {
                                threadRun[0] = false;
                            }
                        });
                        thread.start();
                    }
                    break;
                case Insert:
                    if (!tracksList.isEmpty()) {
                        Edit edit = new Edit(screen, sm);
                        if (thread != null) {
                            thread.interrupt();
                            synth.close();
                        }
                        edit.edit(toList(tracksList.get(select).getName()),
                                "tracks/" + tracksList.get(select).getName());
                    }
                    break;
                case Delete:
                    if (!tracksList.isEmpty()) {
                        tracksList.get(select).delete();
                        tracksList = Arrays.asList(new File("tracks").listFiles());
                        sm.refreshTracks(tracksList);
                        sm.putWhite(12, select + 10, " ");
                        if (select > 0) {
                            select -= 1;
                        }
                        if (tracksList.size() > 0) {
                            screen.putString(12, select + 10, ">",
                                    Terminal.Color.GREEN, Terminal.Color.BLACK, ScreenCharacterStyle.Bold);
                        } else {
                            sm.putWhite(12, select + 10, " ");
                        }
                        screen.refresh();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private ArrayList<Note> toList(String filename) throws FileNotFoundException {
        ArrayList<Note> track = new ArrayList<>();
        Scanner scan = new Scanner(new File("tracks/" + filename));
        while (scan.hasNext()) {
            track.add(new Note(Integer.parseInt(scan.nextLine()), Integer.parseInt(scan.nextLine())));
        }
        return track;
    }
}
