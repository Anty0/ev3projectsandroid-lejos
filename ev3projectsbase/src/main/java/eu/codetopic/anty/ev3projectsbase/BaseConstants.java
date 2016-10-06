package eu.codetopic.anty.ev3projectsbase;

import java.io.IOException;
import java.net.Socket;

import lipermi.exception.LipeRMIException;
import lipermi.handler.CallHandler;
import lipermi.net.Client;
import lipermi.net.IServerListener;
import lipermi.net.Server;

public final class BaseConstants {

    public static final int SELF_VERSION_CODE = 1;
    public static final CallHandler CALL_HANDLER = new CallHandler();
    public static final int TARGET_PORT = 4455;

    private BaseConstants() {
    }

    /**
     * don't forget to close Server
     */
    public static Server startServer(RMIHardware rmiHardware, RMIModes rmiModes) throws IOException, LipeRMIException {
        CALL_HANDLER.registerGlobal(RMIHardware.class, rmiHardware);
        CALL_HANDLER.registerGlobal(RMIModes.class, rmiModes);
        CALL_HANDLER.registerGlobal(RMIVersion.class, new RMIVersionImpl());
        Server server = new Server();
        server.addServerListener(new IServerListener() {
            int count = 0;

            @Override
            public synchronized void clientConnected(Socket socket) {
                if (count > 0) {
                    System.err.println("Caught request to connect more then one client to server" +
                            " that allows only one client connection. Disconnecting client...");
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                count++;
            }

            @Override
            public synchronized void clientDisconnected(Socket socket) {
                count--;
            }
        });
        server.bind(TARGET_PORT, CALL_HANDLER);
        return server;
    }

    /**
     * don't forget to close Client
     */
    public static Client startClient(String targetAddress) throws IOException {
        return new Client(targetAddress, TARGET_PORT, CALL_HANDLER);
    }

}
