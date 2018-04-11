package RunwayRedeclarationTool.Models;

import RunwayRedeclarationTool.Logger.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

public class ImageExport {


    public void export(Canvas canvas){
        try {
            RunwayRedeclarationTool.Logger.Logger.Log("Opening file chooser window");
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
            fileChooser.getExtensionFilters().add(extFilter);
            //Show save file dialog
            File file = fileChooser.showSaveDialog(null);
            RunwayRedeclarationTool.Logger.Logger.Log("Selected file ["+file.getName() + "].");

            WritableImage writableImage = canvas.snapshot(new SnapshotParameters(), null);
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
            ImageIO.write(renderedImage, "png", file);
            Logger.Log("Rendering image into file [" + file.getName() + "].");
        } catch (IOException ex) {
            Logger.Log(ex.getMessage());
        }
    }
}
