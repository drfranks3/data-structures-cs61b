import java.util.Scanner;
import java.util.Arrays;
import java.util.ArrayList;

import java.io.File;
import java.io.IOException;

import java.io.PrintWriter;
import java.io.FileWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.UnsupportedEncodingException;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.NoSuchFileException;

public class Gitlet {

    private static final String BASE = ".gitlet/";
    private static final String CURRENT = BASE + "current.txt";
    private static final String MARKED = BASE + "marked.txt";
    private static final String TRACKED = BASE + "tracked.txt";
    private static final String STAGED = BASE + "staged.txt";
    private static final String GLOBAL_LOG = BASE + "log.txt";
    private static final String TEMP = BASE + "temp.txt";

    private static final int SHUFFLE = 5000000;
    private static final int XFF = 0xff;
    private static final int X10 = 0x10;
    private static final int HASH_SIZE = 33;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }
        
        String command = args[0];

        String[] complex = {"init", "log", "global-log", "status"};
        if (args.length == 1 && !Arrays.asList(complex).contains(command)) {
            System.out.println("Did not enter enough arguments.");
            return;
        }

        switch (command) {
            case "init": 
                init();
                return;
            case "add":
                add(args[1]);
                break;  
            case "commit":
                commit(args[1]);
                break;
            case "rm":
                remove(args[1]);
                break;
            case "log":
                log();
                break;
            case "global-log":
                global();
                break;
            case "find":
                find(args[1]);
                break;
            case "status":
                status();
                break;
            case "checkout":
                checkout(args);
                break;                
            case "branch":
                branch(args[1]);
                break;
            case "rm-branch":
                rmbranch(args[1]);
                break;
            case "reset":
                reset(args[1]);
                break;
            case "merge":
                merge(args[1]);
                break;
            case "rebase":
                rebase(args[1]);
                break;
            case "i-rebase":
                break;
            default:
                System.out.println("Unrecognized command.");  
                break;
        }
    }

    /* = * = * = & Helper Functions & = * = * = */
    private static void write(File file, String contents) {
        write(file, contents, true);
    }

    private static void write(File file, String contents, boolean append) {
        try {
            PrintWriter print = new PrintWriter(new FileWriter(file, append));
            print.println(contents.trim());
            print.close();
        } catch (IOException e) {
            abort("IO Exception encountered during write: " + e);
            return;
        }
    }

    private static BufferedReader read(File file) {
        try {
            return new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            abort("File (" + file + ") not found.");
        }
        return null;
    }

    private static void copy(File from, File to) {
        copy(from, to, false);
    }

    private static void copy(File from, File to, boolean override) {
        try {
            Files.copy(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (NoSuchFileException e) {
            /* Within a directory (ugh) */
            if (override) {
                abort("An error occurred during the overridden copy.");
                return;
            }
            String absolute = to.getPath();
            File directory = new File(absolute.substring(0, absolute.lastIndexOf(File.separator)));
            directory.mkdir();
            copy(from, to, true);
        } catch (IOException e) {
            abort("IO Exception encountered during copy: " + e);
            return;
        }
    }

    /** Conversion to md5:
      * stackoverflow.com/questions/415953/how-can-i-generate-an-md5-hash
      *
      * Conversion md5 --> String
      * stackoverflow.com/questions/3752981/convert-md5-array-to-string-java
      */

    private static String md5(String input) {
        byte[] bytes;
        MessageDigest md;
        try {
            bytes = input.getBytes("UTF-8");
            md = MessageDigest.getInstance("MD5");
        } catch (UnsupportedEncodingException e) {
            System.out.println("UTF-8 Encoding Not Found. Cannot use md5.");
            return null;
        } catch (NoSuchAlgorithmException e) {
            System.out.println("MD5 is not a valid algorithm. Cannot use md5.");
            return null;
        }

        byte[] digest = md.digest(bytes);

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < digest.length; i++) {
            if ((XFF & digest[i]) < X10) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(XFF & digest[i]));
        }
        return sb.toString();
    }

    private static String current() {
        try {
            return read(new File(CURRENT)).readLine();
        } catch (IOException e) {
            abort("IO Exception encountered - current.txt error.");
        }
        return null;
    }

    private static String headCommit() {
        return headCommit(current());
    }

    private static String headCommit(String branch) {
        try {
            File head = new File(".gitlet/BRANCHES/" + branch + "/head.txt");
            return read(head) == null ? "null" : read(head).readLine();
        } catch (IOException e) {
            abort("IO Exception encountered while obtaining head commit ID.");
        }
        return null;
    }

    private static void abort(String m) {
        System.out.println(m);
    }

    private static boolean warning() {
        System.out.print("Warning: The command you entered may alter");
        System.out.print("the files in your working directory. ");
        System.out.print("Uncommitted changes may be lost. ");
        System.out.print("Are you sure you want to continue? (yes/no)");
        
        Scanner in = new Scanner(System.in);
        String s = in.nextLine();
        return s.equals("yes");
    }

    private static boolean search(String s, File file) {
        BufferedReader reader;
        String line;
        try {
            reader = read(file);
            while ((line = reader.readLine()) != null) {
                if (line.equals(s)) {
                    return true;
                }
            }
        } catch (IOException e) {
            abort("IOException encountered during remove files inner loop.");
        }
        return false;
    }

    private static void init() {
        /* Creates a new gitlet version control system in the current directory.
         * This system will automatically start with one commit: a commit that contains
         * no files and has the commit message initial commit. */

        File gitlet = new File(".gitlet");
        if (!gitlet.isDirectory()) {

            String[] setup = {".gitlet/commits", ".gitlet/data", ".gitlet/BRANCHES"};

            for (String fileName : setup) {
                File file = new File(fileName);
                file.mkdirs();
            }

            File current = new File(".gitlet/current.txt");
            write(current, "master", false);

            String[] prep = {GLOBAL_LOG, STAGED, MARKED, TRACKED};
            for (String fileName : prep) {
                write(new File(fileName), "");
            }

            branch("master");
            commit("initial commit", true);

        } else {
            abort("A gitlet version control system already exists in the current directory.");
            return;
        }
    }

    private static void add(String f) {
        /* Indicates you want the file to be included in the upcoming commit as having been changed.
         * Adding a file is also called staging the file. If the file had been marked for removal,
         * instead just unmark it. */

        File file = new File(f);
        if (file.exists()) {
            File committed = new File(".gitlet/data/" + headCommit() + "/" + f);
            if (committed.exists()) {
                try {
                    BufferedReader old = read(committed);
                    BufferedReader working = read(file);
                    String o, w;
                    boolean same = true;
                    while (((o = old.readLine()) != null) && ((w = working.readLine()) != null)) {
                        if (!o.equals(w)) {
                            same = false;
                            break;
                        }
                    }
                    if (same) {
                        abort("File has not been modified since the last commit.");
                        return;
                    }
                } catch (IOException e) {
                    abort("IOException encountered during commit - global logs.");
                    return;
                }
            }

            File staged = new File(STAGED);
            File removed = new File(MARKED);
            File tracked = new File(TRACKED);
            File temp = new File(TEMP);

            BufferedReader reader;
            String line;

            boolean inStaged = search(f, staged),
                    inTracked = search(f, tracked);

            if (!inStaged) {
                write(staged, f);
            }

            if (!inTracked) {
                write(tracked, f);
            }

            try {
                reader = read(removed);
                while ((line = reader.readLine()) != null) {
                    if (!line.equals(f)) {
                        write(temp, line);
                    }
                }
            } catch (IOException e) {
                abort("IOException encountered during remove files inner loop.");
                return;
            }

            copy(temp, removed);
            temp.delete();

        } else {
            abort("File does not exist.");
            return;
        }
    }

    private static void commit(String message) {
        commit(message, false);
    }

    private static void commit(String message, boolean override) {
        if (message == null || message.isEmpty()) {
            abort("Please enter a commit message.");
            return;
        }

        File staged = new File(STAGED);
        File marked = new File(MARKED);
        if (staged.length() == 1 && marked.length() == 1 && !override) {
            abort("No changes added to the commit.");
            return;
        }

        String unixTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String commitID = md5(String.valueOf(Math.random() * SHUFFLE));
        String currentBranch = ".gitlet/BRANCHES/" + current();

        BufferedReader br;
        String line;

        if (!override) {
            try {
                File commitDirectory = new File(".gitlet/data/" + commitID);
                commitDirectory.mkdir();

                br = read(staged);
                while ((line = br.readLine()) != null) {
                    /* Loop through staged files */
                    File working = new File(line);
                    File committed = new File(".gitlet/data/" + commitID + "/" + working.getPath());
                    copy(working, committed);
                }
            } catch (IOException e) {
                abort("IO Exception encountered during commit - staged files.");
                return;
            }
            write(staged, "", false);
        }

        prependLog(commitID, unixTime, message);

        File head = new File(".gitlet/BRANCHES/" + current() + "/head.txt");
        line = headCommit();
        write(head, commitID, false);

        File commit = new File(".gitlet/commits/" + commitID + ".txt");
        write(commit, commitID + ":" + line);
        write(commit, unixTime);
        write(commit, message);

        // loop through files in marked and remove from tracked
        File removed = new File(MARKED);
        File tracked = new File(TRACKED);
        File temp = new File(TEMP);
        ArrayList<String> files = new ArrayList<String>();

        try {
            br = read(removed);
            while ((line = br.readLine()) != null) {
                if (!line.isEmpty()) {
                    files.add(line);
                }
            }
            br = read(tracked);
            while ((line = br.readLine()) != null) {
                if (!files.contains(line)) {
                    write(temp, line);
                }
            }
        } catch (IOException e) {
            abort("IO Exception encountered during commit - removing files.");
        }
        write(removed, "", false);
        copy(temp, tracked);
        temp.delete();
    }

    private static void recursiveLog(String startCommit) {
        File head = new File(".gitlet/commits/" + startCommit + ".txt");
        BufferedReader br = read(head);
        String line;
        try {
            if ((line = br.readLine()) != null) {
                String newCommit = line.substring(0, HASH_SIZE);
                String oldCommit = line.substring(HASH_SIZE);
                String date, format, message;

                try {
                    date = br.readLine();
                    message = br.readLine();
                    System.out.println("====");
                    System.out.println("Commit " + startCommit + ".");
                    System.out.println(date);
                    System.out.println(message + "\n");
                } catch (IOException e) {
                    abort("IOException encountered during log traversal.");
                    return;
                }

                if (oldCommit == null || oldCommit.isEmpty()) {
                    return;
                } else {
                    recursiveLog(oldCommit);
                }

            }
        } catch (IOException e) {
            abort("IO Exception encountered during recursiveLog.");
            return;
        }        
    }

    private static void prependLog(String commit, String time, String message) {
        BufferedReader br;
        String line;

        File temp = new File(TEMP);
        write(temp, commit);
        write(temp, time);
        write(temp, message);

        File temp2 = new File(".gitlet/BRANCHES/" + current() + "/temp.txt");
        copy(temp, temp2);

        /** Reading Files Line-by-Line (StackOverflow)
          * /questions/5868369/how-to-read-a-large-text-file-line-by-line-using-java
          */

        File global = new File(GLOBAL_LOG);
        File branchLog = new File(".gitlet/BRANCHES/" + current() + "/log.txt");

        try {
            br = read(global);
            while ((line = br.readLine()) != null) {
                write(temp, line);
            }
        } catch (IOException e) {
            abort("IOException encountered during prependLog - global logs.");
            return;
        }

        global.delete();
        temp.renameTo(global);

        try {
            br = read(branchLog);
            while ((line = br.readLine()) != null) {
                write(temp2, line);
            }
        } catch (IOException e) {
            abort("IO Exception encountered during prependLog - branch logs.");
            return;
        }

        branchLog.delete();
        temp2.renameTo(branchLog);        
    }

    private static void log() {
        recursiveLog(headCommit());
    }

    private static void global() {
        File masterLog = new File(GLOBAL_LOG);
        BufferedReader br = read(masterLog);
        String line;
        try {
            while ((line = br.readLine()) != null && !line.isEmpty()) {
                String date, format, message;
                try {
                    date = br.readLine();
                    message = br.readLine();
                    System.out.println("====");
                    System.out.println("Commit " + line + ".");
                    System.out.println(date);
                    System.out.println(message + "\n");
                } catch (IOException e) {
                    abort("IO Exception encountered during log traversal.");
                    return;
                }
            }
        } catch (IOException e) {
            abort("IO Exception encountered during global-log.");
            return;
        }        
    }

    private static void recursiveCheckout(File directory, int offset) {
        /** Scanning directories and matching filenames (StackOverflow)
          * /questions/794381/how-to-find-files-that-match-a-wildcard-string-in-java
          */

        File[] listOfFiles = directory.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    File oldStore = new File(directory.getPath() + "/" + file.getName());
                    File currentStore = new File(oldStore.getPath().substring(offset));
                    copy(oldStore, currentStore);
                } else if (file.isDirectory()) {
                    recursiveCheckout(file, offset);
                }
            }
        }
    }        

    private static void checkout(String[] args) {
        if (!warning()) {
            return;
        }
        String commit, name;
        if (args.length == 2) {
            commit = headCommit();
            name = args[1];
        } else if (args.length == 3) {
            commit = args[1];
            name = args[2];
        } else {
            abort("Invalid number of parameters supplied to checkout.");
            return;
        }

        File branchTest = new File(".gitlet/BRANCHES/" + name);
        File fileTest = new File(name);
        if (branchTest.isDirectory()) {
            if (name.equals(current())) {
                abort("No need to checkout the current branch.");
                return;
            }

            String base = ".gitlet/data/" + headCommit(name) + "/";
            File data = new File(base);

            recursiveCheckout(data, base.length());

            File current = new File(CURRENT);
            write(current, name, false);

        } else if (fileTest.isFile()) {
            File oldStore = new File(".gitlet/data/" + commit + "/" + name);
            if (!oldStore.isFile()) {
                //
            } else {
                copy(oldStore, fileTest);
            }
        } else {
            abort("File does not exist in the most recent commit, or no such branch exists.");
            return;
        }
    }

    private static void branch(String name) {
        File branch = new File(".gitlet/BRANCHES/" + name);
        if (branch.isDirectory()) {
            abort("A branch with that name already exists.");
            return;
        } else {
            branch.mkdir();

            File newHead = new File(".gitlet/BRANCHES/" + name + "/head.txt");
            File newLog = new File(".gitlet/BRANCHES/" + name + "/log.txt");

            if (name.equals("master")) {
                write(newHead, "");
                write(newLog, "");
            } else {
                File currentHead = new File(".gitlet/BRANCHES/" + current() + "/head.txt");
                copy(currentHead, newHead);

                File currentLog = new File(".gitlet/BRANCHES/" + current() + "/log.txt");
                copy(currentLog, newLog);
            }
        }
    }

    private static void status() {
        System.out.println("=== Branches ===");

        File branches = new File(".gitlet/BRANCHES/");
        File[] listOfFiles = branches.listFiles();
        for (File file : listOfFiles) {
            String name = file.getName();
            if (name.equals(current())) {
                System.out.print("*");
            }
            System.out.println(name);
        }

        BufferedReader br;
        String line;

        System.out.print("\n=== Staged Files ===\n");

        try {
            br = read(new File(STAGED));
            while ((line = br.readLine()) != null) {
                if (!line.isEmpty()) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            abort("IOException encountered during status - staged files.");
            return;
        }
 
        System.out.println("\n=== Files Marked for Removal ===");
        try {
            br = read(new File(MARKED));
            while ((line = br.readLine()) != null) {
                if (!line.isEmpty()) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            abort("IOException encountered during status - marked files.");
            return;
        }
    }

    private static void reset(String commitID) {
        if (!warning()) {
            return;
        }

        String base = ".gitlet/data/" + commitID + "/";
        File commit = new File(base);

        if (!commit.isDirectory()) {
            abort("No commit with that id exists.");
            return;
        } else {
            recursiveCheckout(commit, base.length());  

            File currentHead = new File(".gitlet/BRANCHES/" + current() + "/head.txt");
            write(currentHead, commitID, false);      
        }
    }

    private static void find(String find) {
        File masterLog = new File(GLOBAL_LOG);
        BufferedReader br = read(masterLog);
        String line;
        try {
            while ((line = br.readLine()) != null && !line.isEmpty()) {
                String message;
                try {
                    br.readLine();
                    message = br.readLine();
                    if (message.equals(find)) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    abort("IOException encountered during log traversal.");
                    return;
                }
            }
        } catch (IOException e) {
            abort("IO Exception encountered during global-log.");
            return;
        }
    }

    private static void rmbranch(String name) {
        File branch = new File(".gitlet/BRANCHES/" + name);
        if (!branch.isDirectory()) {
            abort("A branch with that name does not exist.");
            return;
        } else if (name.equals(current())) {
            abort("Cannot remove the current branch.");
            return;
        } else {
            File branchHead = new File(".gitlet/BRANCHES/" + name + "/head.txt");
            branchHead.delete();
        }
    }

    private static void remove(String file) {
        BufferedReader trackedReader, removeReader, stagedReader;
        String tl, rl, sl;
        try {
            File tracked = new File(TRACKED);
            trackedReader = read(tracked);
            while ((tl = trackedReader.readLine()) != null) {
                if (tl.equals(file)) {
                    File remove = new File(MARKED);
                    File staged = new File(STAGED);
                    File temp = new File(TEMP);
                    try {
                        removeReader = read(remove);
                        while ((rl = removeReader.readLine()) != null) {
                            if (file.equals(rl)) {
                                return;
                            }
                        }
                        write(remove, file);  

                        stagedReader = read(staged);
                        while ((sl = stagedReader.readLine()) != null) {
                            if (!file.equals(sl)) {
                                write(temp, sl);
                            }
                        }
                        copy(temp, staged);
                        temp.delete();
                    } catch (IOException e) {
                        abort("IOException encountered during remove files inner loop.");
                        return;
                    }
                    return;
                }
            }
        } catch (IOException e) {
            abort("IOException encountered during remove files.");
            return;
        }
        abort("No reason to remove the file.");
    }

    private static void merge(String branch) {
        if (!warning()) {
            return;
        }
    }  

    private static void rebase(String b) {
        if (!warning()) {
            return;
        }
        File branch = new File(".gitlet/BRANCHES/" + b);
        if (!branch.isDirectory()) {
            abort("A branch with that name does not exist.");
            return;
        } else if (branch.equals(current())) {
            abort("Cannot rebase a branch onto itself.");
            return;
        } else {
            return;
        }
    }

}
