import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    private static final String SOURCE_IMAGE = "source.jpg";
    private static final String OUTPUT_IMAGE = "output.jpg";

    public static void main(String[] args) throws IOException {
        BufferedImage sourceImage = ImageIO.read(new File(SOURCE_IMAGE));
        BufferedImage outputImage = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_INT_RGB);

        int width = sourceImage.getWidth();
        int height = sourceImage.getHeight();

        // Create and start 4 threads
        Thread[] threads = new Thread[4];
        for (int i = 0; i < 4; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                int leftCorner = (threadIndex % 2) * (width / 2);
                int topCorner = (threadIndex / 2) * (height / 2);
                recolorImage(sourceImage, outputImage, leftCorner, topCorner, width / 2, height / 2);
            });
            threads[i].start();
        }

        // Wait for all threads to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Save output image
        ImageIO.write(outputImage, "jpg", new File(OUTPUT_IMAGE));
    }

    public static void recolorImage(BufferedImage sourceImage, BufferedImage outputImage, int leftCorner, int topCorner, int width, int height) {
        for (int x = leftCorner; x < leftCorner + width; x++) {
            for (int y = topCorner; y < topCorner + height; y++) {
                recolorPixel(sourceImage, outputImage, x, y);
            }
        }
    }

    public static void recolorPixel(BufferedImage sourceImage, BufferedImage outputImage, int x, int y) {
        int rgb = sourceImage.getRGB(x, y);

        int blue = rgb & 0x000000FF;
        int green = (rgb & 0x0000FF00) >> 8;
        int red = (rgb & 0x00FF0000) >> 16;

        int newBlue;
        int newGreen;
        int newRed;
        if (Math.abs(blue - green) < 30 && Math.abs(blue - red) < 30 && Math.abs(green - red) < 30) {
            newBlue = 255 - blue;
            newGreen = 255 - green;
            newRed = 255 - red;
        } else {
            newBlue = blue;
            newGreen = green;
            newRed = red;
        }

        int newRgb = 0;
        newRgb |= newBlue;
        newRgb |= (newGreen << 8);
        newRgb |= (newRed << 16);

        outputImage.getRaster().setDataElements(x, y, outputImage.getColorModel().getDataElements(newRgb, null));
    }
}