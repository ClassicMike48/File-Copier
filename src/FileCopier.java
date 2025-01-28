import java.io.IOException;
import java.nio.file.*;

/**
 * PhotoCopier is an application that copies and organizes your files based on when the files were created.
 *
 * @author Michael Brudos
 */
public class FileCopier {
    /**
     * The entry point of the application.
     *
     * @param args the input arguments from the command line. args[0] source path - Directory to copy files from. args[1] target path - Directory to place copied files.
     * @throws IOException - if an I/ O error is thrown by a BackupFileVisitor method
     * @see BackupFileVisitor
     */
    /*
        Program Format
        String[] args ->
        args[0] source path - Directory to copy files from.
        args[1] target path - Directory to place copied files.
     */
    public static void main(String[] args) throws IOException {
        //Error check args
        if (args.length != 2) {
            System.out.println("PhotoCopier takes two arguments: ");
            System.out.println("args[0] source path - Directory to copy files from.");
            System.out.println("args[1] target path - Directory to place copied files.");
            System.exit(1);
        }
        //Setup paths
        Path start = null;
        Path target = null;
        try {
            start = Paths.get(args[0]);
        } catch (Exception e) {
            System.out.println("An error occurred while finding the starting directory: " + e);
            System.out.println("Please enter in a new starting directory...");
            System.exit(1);
        }

        try {
            target = Paths.get(args[1]);
        } catch (Exception e) {
            System.out.println("An error occurred while finding the backup directory: " + e);
            System.out.println("Please enter in a new backup directory...");
            System.exit(1);
        }

//        Interactive mode
//        Get folder paths
//        while (true) {
//            try {
//                start = Paths.get("C:\\Users\\bobsn\\Desktop\\PhotoTree");
//            } catch (Exception e) {
//                System.out.println("An error occurred while finding the starting directory: " + e);
//                System.out.println("Please enter a new starting directory...");
//                continue;
//            }
//            try {
//                target = Paths.get("C:\\Users\\bobsn\\Desktop\\Test");
//            } catch (Exception e) {
//                System.out.println("An error occurred while finding the backup directory: " + e);
//                System.out.println("Please enter a new backup directory...");
//                continue;
//            }
//            break;
//        }

        //Folders exist, run the backup logic

        System.out.println("Copying files from " + start + " -> " + target);
        BackupFileVisitor visitor = new BackupFileVisitor(target);
        Files.walkFileTree(start, visitor);
        System.out.println("Yay! You found " + visitor.getFilesFound() + " files!");

    }

}
