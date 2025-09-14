package newpix;

import newpix.views.AuthenticationFrame;
import javax.swing.SwingUtilities;

public class ClienteMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AuthenticationFrame authFrame = new AuthenticationFrame();
            authFrame.setVisible(true);
        });
    }
}