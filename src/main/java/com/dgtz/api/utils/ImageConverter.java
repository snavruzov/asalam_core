package com.dgtz.api.utils;

import com.dgtz.api.enums.EnumErrors;
import com.dgtz.mcache.api.factory.RMemoryAPI;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import net.coobird.thumbnailator.resizers.configurations.Antialiasing;
import net.coobird.thumbnailator.resizers.configurations.Rendering;
import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created with IntelliJ IDEA.
 * User: root
 * Date: 12/16/13
 * Time: 7:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImageConverter {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ImageConverter.class);
    private static final int[] RGB_MASKS = {0xFF0000, 0xFF00, 0xFF};
    private static final ColorModel RGB_OPAQUE =
            new DirectColorModel(32, RGB_MASKS[0], RGB_MASKS[1], RGB_MASKS[2]);

    public ImageConverter() {
    }

    /*AVATAR*/
    public static EnumErrors convertAvaToJpg(InputStream input, File output, int width, int height) {

        EnumErrors errors = EnumErrors.NO_ERRORS;


        try {

            //read image file
            BufferedImage bufferedImage = ImageIO.read(input);

            // create a blank, RGB, same width and height, and a white background
            BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(),
                    bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);

            // write to jpeg file
            Thumbnails.of(newBufferedImage)
                    .crop(Positions.CENTER)
                    .size(width, height)
                    .outputQuality(0.9)
                    .outputFormat("jpg")
                    .antialiasing(Antialiasing.ON)
                    .rendering(Rendering.QUALITY)
                    .toFile(output);
            //ImageIO.write(newBufferedImage, "jpg", output);

            log.debug("===============Done Avatar Copying!===================");

        } catch (IOException e) {
            errors = EnumErrors.UNKNOWN_ERROR;
            log.error("ERROR IN MAIN API ", e);
        }

        return errors;
    }

    /*AVATAR*/
    public static String avaResizer(String avatar, int size) {

        String fileName = avatar + "M.jpg";
        if (size < 100) {
            fileName = avatar + "S.jpg";
        }

        try {

            Thumbnails.of(avatar)
                    .size(size, size)
                    .outputQuality(0.9)
                    .outputFormat("jpg")
                    .antialiasing(Antialiasing.ON)
                    .rendering(Rendering.QUALITY)
                    .toFile(new File(fileName));
            log.debug("===============Done Avatar Resizing!===================");

        } catch (IOException e) {
            log.error("ERROR IN AVA RESIZE API ", e);
        }

        return fileName;
    }

    public static EnumErrors convertAvaToJpg(InputStream input, int width, int height, File... output) {

        EnumErrors errors = EnumErrors.NO_ERRORS;


        try {

            //read image file
            BufferedImage bufferedImage = ImageIO.read(input);

            // create a blank, RGB, same width and height, and a white background
            BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(),
                    bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);

            // write to jpeg file
            Thumbnails.of(newBufferedImage)
                    .crop(Positions.CENTER)
                    .size(width, height)
                    .outputQuality(1)
                    .outputFormat("jpg")
                    .antialiasing(Antialiasing.ON)
                    .rendering(Rendering.QUALITY)
                    .toFile(output[0]);

            Thumbnails.of(newBufferedImage)
                    .crop(Positions.CENTER)
                    .size(100, 100)
                    .outputQuality(1)
                    .outputFormat("jpg")
                    .antialiasing(Antialiasing.ON)
                    .rendering(Rendering.QUALITY)
                    .toFile(output[1]);

            Thumbnails.of(newBufferedImage)
                    .crop(Positions.CENTER)
                    .size(50, 50)
                    .outputQuality(1)
                    .outputFormat("jpg")
                    .antialiasing(Antialiasing.ON)
                    .rendering(Rendering.QUALITY)
                    .toFile(output[2]);
            //ImageIO.write(newBufferedImage, "jpg", output);

            log.debug("===============Done Avatar Copying!===================");

        } catch (IOException e) {
            errors = EnumErrors.UNKNOWN_ERROR;
            log.error("ERROR IN MAIN API ", e);
        }

        return errors;
    }

    public static EnumErrors convertToThumb(InputStream input, String output) {

        EnumErrors errors = EnumErrors.NO_ERRORS;
        String temp = "/opt/dump/000000";

        try {
            temp = "/opt/dump/" + RMemoryAPI.getInstance().currentTimeMillis() + "_1.jpg";
            FileUtils.copyInputStreamToFile(input, new File(temp));
            int done = ShellCommands.thumbnailer(temp, output);

            if (done == 1)
                log.debug("===============Done Thumbnail Copying!===================");

        } catch (Exception e) {
            errors = EnumErrors.UNKNOWN_ERROR;
            log.error("ERROR IN MAIN API ", e);
        } finally {
            try {
                Files.deleteIfExists(Paths.get(temp));
            } catch (IOException e) {
                log.error("ERROR IN MAIN API, CANNOT DELETE ", e);
            }

        }

        return errors;
    }

    public EnumErrors convertToJpg(String origin, String out) {

        EnumErrors errors = EnumErrors.NO_ERRORS;

        try {

            int doneOrig = ShellCommands.imageBuilder(origin, out);

        } catch (Exception e) {
            errors = EnumErrors.UNKNOWN_ERROR;
            log.error("ERROR IN MAIN API ", e);
        }

        return errors;
    }

    public EnumErrors convertToThumb(String inParam, String outParam, int rotation) {

        EnumErrors errors = EnumErrors.NO_ERRORS;

        try {

            log.debug("INPUT_2: {}", inParam);
            Thumbnails.of(inParam)
                    .rendering(Rendering.QUALITY)
                    .width(250)
                    .rotate(rotation)
                    .toFile(inParam);

            ShellCommands.thumbnailer(inParam, outParam);


        } catch (Exception e) {
            //errors = EnumErrors.UNKNOWN_ERROR;
            log.error("ERROR IN MAIN API ", e);
        }

        return errors;
    }

    public static EnumErrors convertToThumb(String inParam, String outParam) {

        EnumErrors errors = EnumErrors.NO_ERRORS;
        try {

            log.debug("THUMBNAIL GENERATOR", inParam);
            int done = ShellCommands.thumbnailer(inParam, outParam);
            if (done == 1)
                log.debug("===============Done Thumbnail Copying!===================");

        } catch (Exception e) {
            errors = EnumErrors.UNKNOWN_ERROR;
            e.printStackTrace();
            log.error("ERROR IN MAIN API ", e);
        }

        return errors;
    }

    private int jpegCorrector(File input) {

        int width = 0;
        try {

            Image img = Toolkit.getDefaultToolkit().createImage(input.getAbsolutePath());

            PixelGrabber pg = new PixelGrabber(img, 0, 0, -1, -1, true);
            pg.grabPixels();
            width = pg.getWidth();
            int height = pg.getHeight();

            DataBuffer buffer = new DataBufferInt((int[]) pg.getPixels(), pg.getWidth() * pg.getHeight());
            WritableRaster raster = Raster.createPackedRaster(buffer, width, height, width, RGB_MASKS, null);
            BufferedImage bi = new BufferedImage(RGB_OPAQUE, raster, false, null);

            ImageIO.write(bi, "jpg", input);

        } catch (IOException | InterruptedException e) {
            log.error("ERROR IN MAIN API ", e);
        }

        return width;
    }

    public static int[] imageDimension(String filename) throws IOException {
        BufferedImage img = ImageIO.read(new File(filename));
        int width = img.getWidth();
        int height = img.getHeight();
        img.flush();

        return new int[]{width, height};
    }

    public static int[] imageDimension(File file) throws IOException {
        BufferedImage img = ImageIO.read(file);
        int width = img.getWidth();
        int height = img.getHeight();
        img.flush();

        return new int[]{width, height};
    }
}
