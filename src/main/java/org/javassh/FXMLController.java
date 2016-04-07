package org.javassh;

import java.awt.BorderLayout;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.sshtools.profile.ConnectionManager;
import com.sshtools.profile.SchemeHandler;
import com.sshtools.terminal.emulation.VDUDisplay;
import com.sshtools.terminal.schemes.bsd.RCommandSchemeHandler;
import com.sshtools.terminal.schemes.bsd.RExecSchemeHandler;
import com.sshtools.terminal.schemes.bsd.RLoginSchemeHandler;
import com.sshtools.terminal.schemes.socket.SocketSchemeHandler;
import com.sshtools.terminal.schemes.ssh.SshSchemeHandler;
import com.sshtools.terminal.schemes.telnet.TelnetSchemeHandler;
import com.sshtools.terminal.vt.swing.SwingDataLights;
import com.sshtools.terminal.vt.swing.SwingScrollBar;
import com.sshtools.terminal.vt.swing.SwingStatusScreenSizeMonitor;
import com.sshtools.terminal.vt.swing.SwingTerminalStatusBar;
import com.sshtools.terminal.vt.swing.SwingVirtualTerminal;
import com.sshtools.virtualsession.status.swing.SwingStatusConnectionMonitor;
import com.sshtools.virtualsession.status.swing.SwingStatusLabel;
import com.sshtools.virtualsession.ui.swing.SwingVirtualSessionManager;

import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class FXMLController implements Initializable {

    @FXML
    private Pane terminalPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        final SwingNode terminalNode = new SwingNode();
        createTerminal(terminalNode);
        terminalPane.getChildren().add(terminalNode); // Adding swing node
    }

    private void createTerminal(final SwingNode swingNode) {
    	// set it as to follow resize
        AnchorPane.setTopAnchor(swingNode, 0.0);
        AnchorPane.setBottomAnchor(swingNode, 0.0);
        AnchorPane.setRightAnchor(swingNode, 0.0);
        AnchorPane.setLeftAnchor(swingNode, 0.0);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
        		// Register all supported schemes
        		ConnectionManager manager = ConnectionManager.getInstance();
        		manager.registerSchemeHandler(new SocketSchemeHandler());
        		manager.registerSchemeHandler(new TelnetSchemeHandler());
        		manager.registerSchemeHandler(new RLoginSchemeHandler());
        		manager.registerSchemeHandler(new RExecSchemeHandler());
        		manager.registerSchemeHandler(new RCommandSchemeHandler());
        		manager.registerSchemeHandler(new SshSchemeHandler());
        		            	
				SwingVirtualSessionManager terminal = new SwingVirtualSessionManager();
				
				// Create the status bar and all of the status elements
				SwingTerminalStatusBar statusBar = new SwingTerminalStatusBar();
				SwingStatusConnectionMonitor monitor = new SwingStatusConnectionMonitor(terminal);
				statusBar.addElement(monitor);
				SwingStatusLabel about = new SwingStatusLabel("SwingPassword connect example", 1.0);
				statusBar.addElement(about);
				SwingStatusScreenSizeMonitor size = new SwingStatusScreenSizeMonitor(terminal);
				statusBar.addElement(size);
				statusBar.addElement(new SwingDataLights(terminal));				
				
				// Build the main panel
				JPanel main = new JPanel(new BorderLayout());
				main.add(terminal, BorderLayout.CENTER);
				main.add(statusBar, BorderLayout.SOUTH);

				SwingVirtualTerminal vt1 = new SwingVirtualTerminal();
				vt1.getDisplay().setResizeStrategy(VDUDisplay.RESIZE_SCREEN);
				vt1.addVirtualTerminalComponent(new SwingScrollBar(), BorderLayout.EAST, 0);
				terminal.addVirtualSession(vt1);
				DirectConnector v1 = new DirectConnector(new String[] {"ssh://user@host"}, vt1, null);
				v1.start();
				
				swingNode.setContent(main);
            }
        });
    }
}
