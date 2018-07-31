package org.apache.commons.vfs2.provider.ftps;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Locale;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.SSLSocket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPSClient;

/**
 * Some FTPS servers use a combined data/control port and we need to use the same session
 * This is a modified version of Cyberduck's FTPSClient that reuses the SSL/TLS socket
 * From: http://stackoverflow.com/questions/32398754/how-to-connect-to-ftps-server-with-data-connection-using-same-tls-session
 * Original: https://trac.cyberduck.io/browser/trunk/ftp/src/main/java/ch/cyberduck/core/ftp/FTPClient.java
 * */

public class FTPSClientForCeligoFTPBridge extends FTPSClient {
	private final Log log = LogFactory.getLog(FTPSClientForCeligoFTPBridge.class);
	
	private boolean isRequireSocketReUse =  false;
	
	 /*
      * All Constructors just call the FTPSClient implementation
      */
    public FTPSClientForCeligoFTPBridge() {
        super();
    }
    public FTPSClientForCeligoFTPBridge(boolean isImplicit) {
        super(isImplicit);
    }

    public FTPSClientForCeligoFTPBridge(String protocol) {
    	super(protocol);
    }

    public FTPSClientForCeligoFTPBridge(String protocol, boolean isImplicit) {
        super(protocol, isImplicit);
    }

    public FTPSClientForCeligoFTPBridge(boolean isImplicit, SSLContext context) {
        super(isImplicit, context);
    }

    public FTPSClientForCeligoFTPBridge(SSLContext context) {
        super(context);
    }
	
	@Override
	protected void _prepareDataSocket_(final Socket socket) throws IOException {
	    
		// Return immediately if we don't need to reuse the socket
		if(!isRequireSocketReUse) {
	    	return;
	    }
	    
        if(socket instanceof SSLSocket) {
            // Control socket is SSL
            final SSLSession session = ((SSLSocket) _socket_).getSession();
            if(session.isValid()) {
                final SSLSessionContext context = session.getSessionContext();
                try {
                    final Field sessionHostPortCache = context.getClass().getDeclaredField("sessionHostPortCache");
                    sessionHostPortCache.setAccessible(true);
                    final Object cache = sessionHostPortCache.get(context);
                    final Method method = cache.getClass().getDeclaredMethod("put", Object.class, Object.class);
                    method.setAccessible(true);
                    method.invoke(cache, String.format("%s:%s", socket.getInetAddress().getHostName(),
                            String.valueOf(socket.getPort())).toLowerCase(Locale.ROOT), session);
                    method.invoke(cache, String.format("%s:%s", socket.getInetAddress().getHostAddress(),
                            String.valueOf(socket.getPort())).toLowerCase(Locale.ROOT), session);
                }
                catch(NoSuchFieldException e) {
                    // Not running in expected JRE
                    log.warn("No field sessionHostPortCache in SSLSessionContext", e);
                }
                catch(Exception e) {
                    // Not running in expected JRE
                    log.warn(e.getMessage());
                }
            }
            else {
                log.warn(String.format("SSL session %s for socket %s is not rejoinable", session, socket));
            }
        }
	}

	public void setRequireSocketReuse(boolean isRequireSocketReuse) {
		this.isRequireSocketReUse = isRequireSocketReuse;
	}
}
