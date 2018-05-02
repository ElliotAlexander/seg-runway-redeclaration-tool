package RunwayRedeclarationTool.Models;

import RunwayRedeclarationTool.Logger.Logger;
import RunwayRedeclarationTool.View.PopupNotification;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ImageExport {


    public void export(Canvas canvas){
        try {
            //
            Logger.Log("Running image exporter.");

            // Build a list of all supported file types by Image.IO
            ArrayList<String> file_types = new ArrayList<>(Arrays.asList(ImageIO.getWriterFileSuffixes()));
            RunwayRedeclarationTool.Logger.Logger.Log("Opening file chooser window");

            // conffigure a file chooser window - only display supported encoding formats.
            FileChooser fileChooser = new FileChooser();
            for(String extension : file_types){
                // File Extension filter only allows supported file types.
                FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(extension, extension);
                fileChooser.getExtensionFilters().add(extensionFilter);
            }

            //Show save file dialog
            File file = fileChooser.showSaveDialog(null);
            if(file == null){
                // The user closed the window before selcting a file.
                Logger.Log("Closing file chooser window without saving.");
                return;
            }

            //Get the selected file extension (i.e. .png, .bmp).
            String extension = fileChooser.getSelectedExtensionFilter().getExtensions().get(0);

            // Build a new file object with the required extension.
            file = new File(file.getCanonicalPath() + "." + extension);
            RunwayRedeclarationTool.Logger.Logger.Log("Selected file ["+file.getName() + "].");

            // Write the image
            WritableImage writableImage = canvas.snapshot(new SnapshotParameters(), null);
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);

            // Check the image rendered correctly
            boolean condition = ImageIO.write(renderedImage, extension, file);

            // inform the user if the image is not displayed correctly.
            if(condition){
                Logger.Log("Rendering image into file [" + file.getName() + "].");
                PopupNotification.display("Success - File written successfully", "File Name:" + file.getName() + ".");

                // Imform the user of any errors.
            } else {
                Logger.Log("No appropriate writer could be found to write " + file.getName());
                PopupNotification.error("Error - Appropriate writer not found!", "No writer could be found for format " + extension + ", try using another file format.");
                return;
            }

        } catch (IOException ex) {
            Logger.Log(ex.getMessage());
        }
    }
}
