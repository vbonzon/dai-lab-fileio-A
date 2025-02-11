package ch.heig.dai.lab.fileio;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

// *** TODO: Change this to import your own package ***
import ch.heig.dai.lab.fileio.valbonzon.*;

public class Main {
    // *** TODO: Change this to your own name ***
    private static final String newName = "Val";

    /**
     * Main method to transform files in a folder.
     * Create the necessary objects (FileExplorer, EncodingSelector, FileReaderWriter, Transformer).
     * In an infinite loop, get a new file from the FileExplorer, determine its encoding with the EncodingSelector,
     * read the file with the FileReaderWriter, transform the content with the Transformer, write the result with the
     * FileReaderWriter.
     * 
     * Result files are written in the same folder as the input files, and encoded with UTF8.
     *
     * File name of the result file:
     * an input file "myfile.utf16le" will be written as "myfile.utf16le.processed",
     * i.e., with a suffixe ".processed".
     */
    public static void main(String[] args) {
        // Read command line arguments
        if (args.length != 2 || !new File(args[0]).isDirectory()) {
            System.out.println("You need to provide two command line arguments: an existing folder and the number of words per line.");
            System.exit(1);
        }
        String folder = args[0];
        int wordsPerLine = Integer.parseInt(args[1]);
        
        System.out.println("Application started, reading folder " + folder + "...");

        FileExplorer FE = new FileExplorer(folder);
        FileReaderWriter FRW = new FileReaderWriter();
        EncodingSelector ES = new EncodingSelector();
        Transformer TF = new Transformer(newName, wordsPerLine);

        String newExtension = "processed";

        while (true) {
            try {
                //Get all files in the folder
                File file = FE.getNewFile();
                if(file == null)
                    break;
                
                if(file.isDirectory()){
                    continue;
                }
                
                //Move to the next file if no encoding can be read 
                Charset encoding = ES.getEncoding(file);
                if(encoding == null){
                    continue;
                }
                System.out.println("Reading of " + file.getName());
                String content = FRW.readFile(file, encoding);
                
                //Transformations of the content if file is not blank
                if(content != null){

                    content = TF.replaceChuck(content);
                    content = TF.capitalizeWords(content);
                    content = TF.wrapAndNumberLines(content);
                }
                
                //Writing the new content in a new file
                String nameWOExtention = file.getName().substring(0, file.getName().lastIndexOf('.'));
                String path = folder + "\\" + nameWOExtention + "." + newExtension;
                File outputFile = new File(path);

                FRW.writeFile(outputFile, content, encoding);
                System.out.println(outputFile.getName() + " written\n");
                

            } catch (Exception e) {
                System.out.println("Exception: " + e);
            }
        }
        System.out.println("All files have been written");
    }
}
