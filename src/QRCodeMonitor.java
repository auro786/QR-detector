import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.*;

import javazoom.jl.player.Player;

import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

public class QRCodeMonitor {
    private static final int BEEP_INTERVAL_MS = 250;
    public static void main(String[] args) throws AWTException {
        Robot robot = new Robot();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle screenRect = new Rectangle(screenSize);

        QRCodeReader qrCodeReader = new QRCodeReader();

        while (true) {
            BufferedImage screenImage = robot.createScreenCapture(screenRect);
            int[] pixels = screenImage.getRGB(0, 0, screenImage.getWidth(), screenImage.getHeight(), null, 0, screenImage.getWidth());

            LuminanceSource source = new RGBLuminanceSource(screenImage.getWidth(), screenImage.getHeight(), pixels);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            try {
                Result result = qrCodeReader.decode(bitmap);
                System.out.println("QR Code detected: " + result.getText());
                playSound(true); // Call the method to play the sound

            } catch (NotFoundException | ChecksumException | FormatException ignored) {
                playSound(false);
                System.out.println("no QR detected");
            }

            // Introduce a delay to avoid excessive processing
            try {
                Thread.sleep(BEEP_INTERVAL_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void playSound(boolean b) {
        try {
            InputStream inputStream = QRCodeMonitor.class.getResourceAsStream("/final_audio.wav");
            Player player = new Player(inputStream);
            if(b){
                player.play();
            }else{
                player.close();
            }

//            Toolkit.getDefaultToolkit().beep();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    private static void stopCustomSound() {
//            player.close();
//    }
}
