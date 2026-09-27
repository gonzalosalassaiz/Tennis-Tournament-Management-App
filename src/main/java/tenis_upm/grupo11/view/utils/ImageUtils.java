package tenis_upm.grupo11.view.utils;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * Utilities class for images and icons
 */
public class ImageUtils {

    /**
     * Loads an image from resources and scales it to the specific size.
     *
     * @param imageName the filename of the image
     * @param width the desired width for the scaled image
     * @param height the desired height for the scaled image
     * @return an ImageIcon with the scaled image
     */
    public static ImageIcon loadScaledIcon(String imageName, int width, int height) {
        Image img = new ImageIcon(Objects.requireNonNull(
                ImageUtils.class.getClassLoader().getResource(String.format("icons/%s", imageName)))).getImage();
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }
}
