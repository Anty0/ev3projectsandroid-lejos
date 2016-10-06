package eu.codetopic.anty.ev3projectsbase;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;

import lipermi.net.Client;
import lipermi.net.IClientListener;

public class ClientConnection {

    private static final String LOG_TAG = "ClientConnection";

    private static final ArrayList<ConnectionChangeListener> changeListeners = new ArrayList<>();
    private static Connection CONNECTION = null;
    private static final IClientListener CLIENT_LISTENER = new IClientListener() {
        @Override
        public void disconnected() {
            forceDisconnect();
        }
    };

    public static boolean addOnChangeListener(ConnectionChangeListener listener) {
        synchronized (changeListeners) {
            return changeListeners.add(listener);
        }
    }

    public static boolean removeOnChangeListener(ConnectionChangeListener listener) {
        synchronized (changeListeners) {
            return changeListeners.remove(listener);
        }
    }

    public static synchronized String getConnectionAddress() {
        return CONNECTION == null ? null : CONNECTION.connectionAddress;
    }

    public static synchronized boolean isConnected() {
        return CONNECTION != null;
    }

    public static synchronized void connect(@NotNull String address) throws IOException {
        try {
            CONNECTION = new Connection(address);
        } catch (Throwable e) {
            disconnectInternal();
            throw e;
        }

        synchronized (changeListeners) {
            for (ConnectionChangeListener listener : changeListeners) {
                try {
                    listener.onConnected(address);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static synchronized void forceDisconnect() {
        String address = getConnectionAddress();
        try {
            disconnectInternal();
        } catch (IOException e) {
            e.printStackTrace();
        }
        CONNECTION = null;

        synchronized (changeListeners) {
            for (ConnectionChangeListener listener : changeListeners) {
                try {
                    listener.onForceDisconnected(address);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static synchronized boolean disconnect() throws IOException {
        String address = getConnectionAddress();
        if (!disconnectInternal()) return false;

        synchronized (changeListeners) {
            for (ConnectionChangeListener listener : changeListeners) {
                try {
                    listener.onDisconnected(address);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    private static synchronized boolean disconnectInternal() throws IOException {
        if (CONNECTION == null) return false;
        CONNECTION.close();
        CONNECTION = null;
        return true;
    }

    public static synchronized boolean isModelSet() {
        return CONNECTION != null && CONNECTION.isModelSet;
    }

    public static synchronized String getActiveModelName() {
        return CONNECTION == null ? null : CONNECTION.activeModelName;
    }

    public static synchronized void setupModel(@Nullable ModelInfo modelInfo) throws IOException {
        if (CONNECTION == null) throw new IllegalStateException(LOG_TAG + " is not connected");
        CONNECTION.hardware.setup(modelInfo);
        CONNECTION.activeModelName = modelInfo == null ? null : modelInfo.name;
        CONNECTION.isModelSet = modelInfo != null;
    }

    public static synchronized RMIModes getModes() throws IOException {
        return CONNECTION == null ? null : CONNECTION.modes;
    }

    public interface ConnectionChangeListener {

        void onConnected(String address);

        void onDisconnected(String address);

        void onForceDisconnected(String address);
    }

    private static class Connection implements Closeable {

        final Client activeClient;
        final String connectionAddress;

        final RMIHardware hardware;
        final RMIModes modes;

        String activeModelName;
        boolean isModelSet;

        Connection(@NotNull String address) throws IOException {
            this.connectionAddress = address;
            activeClient = BaseConstants.startClient(address);
            activeClient.addClientListener(CLIENT_LISTENER);
            if (((RMIVersion) activeClient.getGlobal(RMIVersion.class))
                    .getServerVersionCode() != BaseConstants.SELF_VERSION_CODE) {
                throw new VersionMismatchException("Server uses another connection version then Client.");
            }
            hardware = (RMIHardware) activeClient.getGlobal(RMIHardware.class);
            activeModelName = hardware.getActiveModelName();
            isModelSet = hardware.isSet();
            modes = (RMIModes) activeClient.getGlobal(RMIModes.class);
        }

        @Override
        public void close() throws IOException {
            activeClient.removeClientListener(CLIENT_LISTENER);
            activeClient.close();
        }
    }

}
