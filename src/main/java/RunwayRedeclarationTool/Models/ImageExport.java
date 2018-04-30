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

            Logger.Log("Running image exporter.");
            ArrayList<String> file_types = new ArrayList<>(Arrays.asList(ImageIO.getWriterFileSuffixes()));
            RunwayRedeclarationTool.Logger.Logger.Log("Opening file chooser window");
            FileChooser fileChooser = new FileChooser();
            for(String extension : file_types){
                FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(extension, extension);
                fileChooser.getExtensionFilters().add(extensionFilter);
            }

            //Show save file dialog
            File file = fileChooser.showSaveDialog(null);
            if(file == null){
                Logger.Log("Closing file chooser window without saving.");
                return;
            }

            String extension = fileChooser.getSelectedExtensionFilter().getExtensions().get(0);

            file = new File(file.getCanonicalPath() + "." + extension);



            RunwayRedeclarationTool.Logger.Logger.Log("Selected file ["+file.getName() + "].");

            WritableImage writableImage = canvas.snapshot(new SnapshotParameters(), null);
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
            boolean condition = ImageIO.write(renderedImage, extension, file);
            if(condition){
                Logger.Log("Rendering image into file [" + file.getName() + "].");
                PopupNotification.display("Success!", "File " + file.getName() + " was written successfully.");
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
