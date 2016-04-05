package org.javassh.javassh;

import com.sshtools.terminal.emulation.VDUDisplay;
import com.sshtools.terminal.vt.swing.SwingScrollBar;
import com.sshtools.terminal.vt.swing.SwingVirtualTerminal;
import com.sshtools.virtualsession.ui.swing.SwingVirtualSessionManager;
import java.awt.BorderLayout;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class FXMLController implements Initializable {

    @FXML
    private Pane pane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        final SwingNode swingNode = new SwingNode();
        createAndSetSwingContent(swingNode);

        pane.getChildren().add(swingNode); // Adding swing node
    }

    private void createAndSetSwingContent(final SwingNode swingNode) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SwingVirtualSessionManager terminal
                        = new SwingVirtualSessionManager();
                SwingVirtualTerminal vt1 = new SwingVirtualTerminal();
                vt1.getDisplay().setResizeStrategy(VDUDisplay.RESIZE_SCREEN);
                vt1.addVirtualTerminalComponent(
                        new SwingScrollBar(),
                        BorderLayout.EAST, 0);
                terminal.addVirtualSession(vt1);

                swingNode.setContent(terminal);
            }
        });
    }
}
