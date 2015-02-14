
import com.techventus.server.voice.Voice;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class ShinVoiceMin {

    public static final Calendar time = Calendar.getInstance();
    public static final Set<String> LOGS = new TreeSet<String>();
    public static PrintStream out;
    public static Voice voice;
    public static FileWriter w;
    public static Map<String, String> cfg = new TreeMap<String, String>();
    public static File cfgfile;

    public static void main(String[] args) {
        while (true) {
            cfgfile = new File(System.getProperty("user.dir") + "\\config.sv");

            try {
                parseFile();
            } catch (Exception e) {
                System.out.println("Either you don't have a config file, or something is currently wrong with it. You have two options:\n"
                        + "1. Create New Config File\n"
                        + "2. Exit Program and Fix Config File");
                Scanner userInput = new Scanner(System.in);
                int choice = Integer.parseInt(userInput.nextLine());
                switch (choice) {
                    case 1:
                        createNewConfigFile();
                        break;
                    case 2:
                        System.exit(0);
                        break;
                }
                System.out.println("Program is now closing. Please try running the program again");
                System.exit(0);
            }
            clearScreen();
            System.out.println("#####################");
            System.out.println("#                   #");
            System.out.println("#   Admin Control   #");
            System.out.println("#       Panel       #");
            System.out.println("#                   #");
            System.out.println("#  1. Run Program   #");
            System.out.println("#  2. Edit Config   #");
            System.out.println("#                   #");
            System.out.println("#####################");
            Scanner userInput = new Scanner(System.in);
            int choice = Integer.parseInt(userInput.nextLine());
            switch (choice) {
                case 1:
                    runProgram();
                    break;
                case 2:
                    editConfig();
                    break;
                default:
                    System.err.println("Invalid choice. Please try again.\nPress any key to continue...");
                    userInput.nextLine();
            }
        }
    }

    public static void createNewConfigFile() {
        Map<String, String> config = new TreeMap<String, String>();
        Scanner userInput = new Scanner(System.in);
        System.out.print("Key (System will generate one if left blank): ");
        String key = userInput.nextLine();
        if (key.equals("")) {
            key = Security.genKey();
        }
        System.out.print("Google Voice Username: ");
        String username = userInput.nextLine();
        System.out.print("Google Voice Password: ");
        String password = userInput.nextLine();
        System.out.print("Door Access Password: ");
        String doorpass = userInput.nextLine();
        System.out.print("Unlock Keyword: ");
        String openpass = userInput.nextLine();
        System.out.print("Lock Keyword: ");
        String closepass = userInput.nextLine();
        System.out.print("Unlock Default Time: ");
        String defaulttime = userInput.nextLine();
        config.put("#KEY", key);
        config.put("#USER", username);
        config.put("#PASS", password);
        config.put("#ENTRY_PASS", doorpass);
        config.put("#UNLOCK_CODE", openpass);
        config.put("#LOCK_CODE", closepass);
        config.put("#UNLOCK_TIME", defaulttime);
        System.out.println("That is all the system needs to generate your config file. \n"
                + "Please verify that the following information is correct.");
        displayConfigFile(config);
        System.out.println("Proceed to write data to new config file? (y/n)");
        String choice = userInput.nextLine();
        if (choice.equals("n")) {
            System.out.println("To fix the error, you will need to proceed through the file generation process again.");
            createNewConfigFile();
            return;
        } else if (choice.equals("y")) {
            try{
              cfgfile.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(cfgfile));
            writer.write("#KEY \"" + key + "\"\n");

            for (String s : config.keySet()) {
                if (!s.equals("#KEY")) {
                    writer.write(s + " \"" + Security.encode(key, config.get(s)) + "\"\n");
                }
            }
            writer.flush();
            writer.close();
            System.out.println("New file creation done!");
            }catch(IOException e){
                System.err.println("Something went wrong during the file creation process.\nThe following debugging message has been included for your convenience\n" + e.getMessage());
            }
        }
    }

    public static void editConfig() {
        while (true) {
            clearScreen();
            System.out.println("#########################################");
            System.out.println("#                                       #");
            System.out.println("#   Configuration File Control Panel    #");
            System.out.println("#                                       #");
            System.out.println("#    1. Display Config File             #");
            System.out.println("#    2. Edit Values                     #");
            System.out.println("#    3. Create New Entry                #");
            System.out.println("#    4. Save Changes to Config File     #");
            System.out.println("#    5. Exit                            #");
            System.out.println("#                                       #");
            System.out.println("#########################################");
            Scanner userInput = new Scanner(System.in);
            int choice = Integer.parseInt(userInput.nextLine());
            switch (choice) {
                case 1:
                    displayConfigFile(cfg);
                    break;
                case 2:
                    editValues();
                    break;
                case 3:
                    createNewEntry();
                    break;
                case 4:
                    saveChangesToConfigFile(cfg);
                    break;
                case 5:
                    return;
                default:
                    System.err.println("Invalid Choice. Please Try again.");
            }
            System.out.println("Press any key to continue...");
            userInput.nextLine();
        }
    }

    public static void saveChangesToConfigFile(Map<String, String> config) {
        try {
            String key = config.get("#KEY");
            if (!Security.verify(key)) {
                System.err.println("Unable to write data to file due to invalid key: " + key);
                return;
            }
            cfgfile.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(cfgfile));
            writer.write("#KEY \"" + key + "\"\n");

            for (String s : config.keySet()) {
                if (!s.equals("#KEY")) {
                    writer.write(s + " \"" + Security.encode(key, config.get(s)) + "\"\n");
                }
            }
            writer.flush();
            writer.close();
            System.out.println("New file creation done!");
        } catch (IOException e) {
            System.err.println("It seems there was an error in creating the new file. Be sure you have the correct permissions and try again.");
        }
    }

    public static void displayConfigFile(Map<String, String> c) {
        for (String s : c.keySet()) {
            System.out.println(s + ": " + c.get(s));
        }
    }

    public static void editValues() {
        System.out.println("Current Entries:");
        for (String s : cfg.keySet()) {
            System.out.println(s);
        }
        System.out.println("What entry would you like to change?");
        Scanner userInput = new Scanner(System.in);
        String search = userInput.nextLine();
        for (String s : cfg.keySet()) {
            if (s.equals(search)) {
                System.out.println("Entry: " + search + " found with value: " + cfg.get(s));
                System.out.println("What would you like to change this value to?");
                String change = userInput.nextLine();
                cfg.put(s, change);
                System.out.println("All done!");
                return;
            }
        }
        System.out.println("Error: Unable to find specified entry: " + search + "\nBe sure to include the \"#\" that prepends most entries.");

    }

    public static void createNewEntry() {
        Scanner userInput = new Scanner(System.in);
        System.out.print("Entry name: ");
        String entry = userInput.nextLine();
        System.out.print("Value: ");
        String value = userInput.nextLine();
        cfg.put(entry, value);
        System.out.println("All done! Entry: " + entry + " added with value: " + value);
    }

    public static void runProgram() {
        try {
            out = System.out;
            String user = cfg.get("#USER");
            String pass = cfg.get("#PASS");
            voice = new Voice(user, pass);
            mainLoop();


        } catch (Exception e) {
            System.err.println("A fatal error has occured within the program.\nSystem is now exiting...");
            System.exit(0);
        }
    }

    public static void clearScreen() {
        for (int i = 0; i < 30; i++) {
            System.out.println();
        }
    }

    public static List<SMS> getSMS(String data) {
        List<String> list = retrieve(data, "div", "gc-message-sms-row");
        List<SMS> SMSlist = new ArrayList<SMS>();
        for (String s : list) {
            SMSlist.add(new SMS(s));
        }
        return SMSlist;
    }

    public static int lineIndex(String[] lines, String line, int index) {
        for (int i = index; i < lines.length; i++) {
            if (lines[i].contains(line)) {
                return i;
            }
        }
        return -1;
    }

    public static int lineIndex(String[] lines, String line) {
        return lineIndex(lines, line, 0);
    }

    private static List<String> retrieve(String data, String tag, String category) {
        int index = 0;
        String[] lines = data.split("\n");
        List<String> list = new ArrayList<String>();
        _while:
        while (lineIndex(lines, "<" + tag + " class=\"" + category + "\">", index) != -1) {
            for (int i = index; i < lines.length; i++) {
                String s = lines[i];
                if (s.equals("<" + tag + " class=\"gc-message-sms-row\">") && lines[i + 1].equals("<span class=\"gc-message-sms-from\">")) {
                    String build = "";
                    for (int j = i; j < lines.length; j++) {
                        if (!lines[j].equals("</" + tag + ">")) {
                            build += lines[j] + "\n";
                        } else {
                            break;
                        }
                    }
                    list.add(build);
                    index = i + 1;
                }
            }
        }
        if (!list.isEmpty()) {
            return list;
        }
        throw new RuntimeException("Tag: " + tag + " or Class: " + category + " Not Found.");
    }

    public static void mainLoop() throws Exception {
        int cycles = 0;
        List<SMS> messages = getSMS(trimSpace(voice.getUnreadSMS()));
        while (true) {
            cycles++;
            out.println("Cycle: " + cycles);

            List<SMS> nmessages = getSMS(trimSpace(voice.getUnreadSMS()));
            List<SMS> newmessages = new ArrayList<SMS>();
            if (!nmessages.equals(messages)) {
                for (int i = 0; i < nmessages.size(); i++) {
                    if (!messages.contains(nmessages.get(i))) {
                        newmessages.add(nmessages.get(i));
                    }
                }
                for (int i = 0; i < newmessages.size(); i++) {
                    out.println("Message Received: " + newmessages.get(i).text);
                    parseSMS(newmessages.get(i));
                }
                messages = nmessages;
            }
            Thread.sleep(5000);
            if (cycles == 120) {
                voice.login();
                out.println("Relogged at " + getFullTime());
            }
        }
    }

    public static void parseSMS(SMS sms) {
        if (sms.text.contains(cfg.get("#ENTRY_PASS"))) {
            if (sms.text.contains(cfg.get("#UNLOCK_CODE"))) {
                writeToPin(0, 1, Integer.parseInt(cfg.get("#UNLOCK_TIME")));
            }
            if (sms.text.contains(cfg.get("#LOCK_CODE"))) {
                writeToPin(0, 0, Integer.parseInt(cfg.get("#UNLOCK_TIME")));
            }
        }
    }

    public static void writeToPin(int pin, int state, int time) {
        out.println("Code Received: " + pin + " " + state + " " + time);
        try {
            if (pin == 0) {
                if (state == 1) {
                    System.out.println("Signal Sent to Unlock");
                    Thread.sleep(time * 1000);
                }
                if (state == 0) {
                    System.out.println("Signal Sent to Lock");
                    Thread.sleep(time * 1000);
                }
            }
        } catch (Exception e) {
        }
    }

    public static void parseFile() throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(cfgfile));
        ArrayList<String> params = new ArrayList<String>();
        String line;
        for (line = reader.readLine(); line != null; line = reader.readLine()) {
            params.add(line);
        }
        for (int i = 0; i < params.size(); i++) {
            String[] tmp = params.get(i).split(" ");
            String key = tmp[0];
            String value = tmp[1].substring(1, tmp[1].length() - 1);
            cfg.put(key, value);
        }
        if (!cfg.containsKey("#KEY")) {
            System.err.println("Invalid Config File. Must contain value \"#KEY\"");
            System.exit(0);
        }
        String ke = cfg.get("#KEY");
        if (!Security.verify(ke)) {
            System.err.println("You seem to have an invalid key: " + ke);
            throw new Exception();
        }
        System.out.println(cfg);
        for (String k : cfg.keySet()) {
            if (!k.equals("#KEY")) {
                cfg.put(k, Security.decode(ke, cfg.get(k)));
            }
        }
        System.out.println(cfg);

    }

    public static boolean processTimeLine(String s) {
        s = s.replaceAll(" {2,}", "");
        String[] params = s.split(":");
        int hour = Integer.parseInt(params[0]);
        if (hour == getHour()) {
            int minute = Integer.parseInt(params[1].substring(0, 2), 10);
            if (minute >= getMinute() - 2 && minute <= getMinute() + 2) {
                return true;
            }
        }
        return false;
    }

    public static String trimSpace(String value) {
        String[] lines = value.split("\n");
        String build = "";
        for (String s : lines) {
            if (!s.matches("\\s+")) {
                int spi = 0;
                while (spi != s.length() && (s.charAt(spi) + "").matches("\\s")) {
                    spi++;
                }
                if (spi != s.length()) {
                    build += s.substring(spi) + "\n";
                }
            }
        }

        return build;
    }

    public static String clearTags(String value) {
        int oc = 0;
        int si = 0;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '<') {
                oc++;
                if (oc == 1) {
                    si = i;
                }
            }
            if (c == '>') {
                oc--;
                if (oc == 0) {
                    value = value.substring(0, si) + value.substring(i + 1);
                    i = si;
                } else if (oc == -1) {
                    oc = 0;
                }
            }
        }
        return value;
    }

    public static String getFullTime() {
        String t = (time.get(Calendar.MONTH) + 1) + "/" + time.get(Calendar.DAY_OF_MONTH) + " " + getHourMinuteTime();
        return t;
    }

    public static String getHourMinuteTime() {
        String t = String.format("%d:%02d %s", time.get(Calendar.HOUR), time.get(Calendar.MINUTE), (time.get(Calendar.AM_PM) == 1) ? "PM" : "AM");
        return t;
    }

    public static int getHour() {
        return time.get(Calendar.HOUR);
    }

    public static int getMinute() {
        return time.get(Calendar.MINUTE);


    }
}
