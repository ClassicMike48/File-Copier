import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Calendar;
import java.util.Locale;


import static java.nio.file.FileVisitResult.*;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


/**
 * The type Backup file visitor.
 *
 * @author Michael Brudos
 */
public class BackupFileVisitor implements FileVisitor<Path> {
    private final Path dest; // Directory path where to copy files from source
    /**
     * Current number of files found by BackupFileVisitor
     */
    int filesFound;

    /**
     * Instantiates a new Backup file visitor.
     *
     * @param dest the dest
     */
    public BackupFileVisitor(Path dest) {
        this.dest = dest;
        filesFound = 0;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        System.out.println("File Found: " + file.getFileName());

        //Get when the file was created
        FileTime time = attrs.lastModifiedTime();

        //Setup name for the folder(s)
        Calendar cal = new Calendar.Builder().setInstant(time.toMillis()).setCalendarType("gregorian").build();
        String month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
        String year = cal.get(Calendar.YEAR) + "";

        //Create parent folders, if necessary
        Path fullDestination = dest.resolve(year).resolve(month);

        try {
            boolean folderConstructionSuccessful = fullDestination.toFile().mkdirs();
        } catch (SecurityException e) {
            System.out.println("A security exception occurred: " + e);
        }

        //Place the file in the folder tree
        fullDestination = fullDestination.resolve(file.getFileName());

        System.out.printf("Date: %s %s\n", month, year);
        System.out.println("Copying to: " + fullDestination.toString());

        //Complete action
        copyFile(file, fullDestination, true, true);

        filesFound++;

        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        System.out.println("Error with: " + file.getFileName());
        return CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {

        return CONTINUE;
    }

    /**
     * Return the current number of files found by BackUpFileVisitor.
     *
     * @return the current number of files found
     */
    public int getFilesFound() {
        return filesFound;
    }


    //Source: https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/essential/io/examples/Copy.java
    /*
     * Copyright (c) 2008, 2010, Oracle and/or its affiliates. All rights reserved.
     *
     * Redistribution and use in source and binary forms, with or without
     * modification, are permitted provided that the following conditions
     * are met:
     *
     *   - Redistributions of source code must retain the above copyright
     *     notice, this list of conditions and the following disclaimer.
     *
     *   - Redistributions in binary form must reproduce the above copyright
     *     notice, this list of conditions and the following disclaimer in the
     *     documentation and/or other materials provided with the distribution.
     *
     *   - Neither the name of Oracle nor the names of its
     *     contributors may be used to endorse or promote products derived
     *     from this software without specific prior written permission.
     *
     * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
     * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
     * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
     * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
     * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
     * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
     * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
     * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
     * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
     * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
     * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
     */

    /**
     * Copy source file to target location. If {@code prompt} is true then
     * prompt user to overwrite target if it exists. The {@code preserve}
     * parameter determines if file attributes should be copied/preserved.
     *
     * @param source   the source file
     * @param target   the target file
     * @param prompt   whether to prompt user, see above
     * @param preserve whether to preserve file attributes in copied file
     */
    static void copyFile(Path source, Path target, boolean prompt, boolean preserve) {
        CopyOption[] options = (preserve) ?
                new CopyOption[]{COPY_ATTRIBUTES, REPLACE_EXISTING} :
                new CopyOption[]{REPLACE_EXISTING};
        if (!prompt || Files.notExists(target) || okayToOverwrite(target)) {
            try {
                Files.copy(source, target, options);
            } catch (IOException x) {
                System.err.format("Unable to copy: %s: %s%n", source, x);
            }
        }
    }

    /**
     * Returns {@code true} if okay to overwrite a  file ("cp -i")
     * Hold Enter for mass-deny
     *
     * @param file the file in question
     * @return true to overwrite file in the target destination, false otherwise.
     */
    static boolean okayToOverwrite(Path file) {
        String answer = System.console().readLine("overwrite %s (yes/no)? ", file);
        return (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes"));
    }
}
