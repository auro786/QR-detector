import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import javax.sound.sampled.*;
import java.awt.*;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class QRCodeMonitorV2 {
    private static final int BEEP_INTERVAL_MS = 250;
    private static Clip clip = null;

    public static void main(String[] args) throws AWTException, IOException, UnsupportedAudioFileException, LineUnavailableException {
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

    private static void playSound(boolean play) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        if (play) {
            if (clip == null) {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(QRCodeMonitor.class.getResourceAsStream("/final_audio.wav"));
                clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
            }
        } else {
            if (clip != null && clip.isOpen()) {
                clip.stop();
                clip.close();
                clip = null;
            }
        }
    }
}
