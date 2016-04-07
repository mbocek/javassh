package org.javassh;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.sshtools.profile.AuthenticationException;
import com.sshtools.profile.ConnectionManager;
import com.sshtools.profile.ProfileException;
import com.sshtools.profile.ResourceProfile;
import com.sshtools.profile.SchemeHandler;
import com.sshtools.profile.URI;
import com.sshtools.profile.URI.MalformedURIException;
import com.sshtools.terminal.emulation.TerminalEmulation;
import com.sshtools.terminal.emulation.VDUDisplay;
import com.sshtools.terminal.schemes.telnet.TelnetProtocolProvider;
import com.sshtools.terminal.vt.TerminalProtocolTranport;
import com.sshtools.terminal.vt.VirtualTerminal;
import com.sshtools.terminal.vt.VirtualTerminalAdapter;
import com.sshtools.virtualsession.VirtualSession;

public class DirectConnector extends Thread {
	VirtualTerminal vt;

	public interface Callback {
		public void connectionFailed(Throwable error);
	}

//	private VTWindow frame;
	private Callback callback;
	private String[] args;
	private TerminalProtocolTranport transport;

	public DirectConnector(VirtualTerminal vt/*, VTWindow frame*/) {
		this(null, vt, /*frame,*/ null);
	}

	public DirectConnector(String[] args, VirtualTerminal vt, /*VTWindow frame,*/
			Callback callback) {
		this.args = args;
		this.vt = vt;
		//this.frame = frame;
		this.callback = callback;
	}

	public void run() {
		if (vt.getDisplay() == null) {
			return;
		}

		try {
			ResourceProfile profile = new ResourceProfile(new URI(args[0]));
			transport = (TerminalProtocolTranport) profile.createProfileTransport();
			transport.init(vt);
			if (!transport.connect(profile, null)) {
				throw new ProfileException("Cannot connect to " + profile.getURI());
			}
			
			vt.getEmulation().setTerminalType("vt100");
			vt.getEmulation().setLocalEcho(false);
			//vt.getEmulation().setEOL(transport.getDefaultEOL());
			//vt.getEmulation().setReturnNewLine(true);
			vt.addVirtualSessionListener(new VirtualTerminalAdapter() {
				
				@Override
				public void disconnected(VirtualSession session, Throwable exception) {
					if (vt.getEmulation().getRecordingStream() != null) {
						try {
							vt.getEmulation().stopRecording();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

				public void screenResized(VirtualTerminal terminal, int w, int h,
						boolean remote) {
					if (vt.getDisplay().getResizeStrategy() == VDUDisplay.RESIZE_SCREEN) {
//						if (remote) {
//							frame.pack();
//						}
						transport.setScreenSize(w, h);
					}
				}
			});
			
			vt.connect(transport);
			
		} catch (ProfileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}