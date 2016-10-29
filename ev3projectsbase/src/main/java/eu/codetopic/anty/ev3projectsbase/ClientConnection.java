package eu.codetopic.anty.ev3projectsbase;

import net.sf.lipermi.net.Client;
import net.sf.lipermi.net.IClientListener;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import eu.codetopic.anty.ev3projectsbase.slam.base.geometry.Pose;
import eu.codetopic.anty.ev3projectsbase.slam.base.map.OccupancyMap;
import eu.codetopic.anty.ev3projectsbase.slam.base.map.OccupancyMapImpl;
import eu.codetopic.anty.ev3projectsbase.slam.base.map.RMISlamMode;
import eu.codetopic.anty.ev3projectsbase.slam.base.move.Move;
import eu.codetopic.anty.ev3projectsbase.slam.base.move.RotateMove;
import eu.codetopic.anty.ev3projectsbase.slam.base.move.TravelMove;
import eu.codetopic.anty.ev3projectsbase.slam.base.scan.ScanResults;

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
            CONNECTION = address.trim().isEmpty() ? new Connection() : new Connection(address);// TODO: 26.10.16 better way to enter to test mode
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

    public static synchronized void setupModel(@Nullable ModelInfo modelInfo) throws Throwable {
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

        Connection() {// TODO: 26.10.16 rework or remove
            activeClient = null;
            connectionAddress = "";

            hardware = new RMIHardware() {
                @Override
                public void setup(@Nullable ModelInfo model) throws Throwable {
                    //unsupported - does nothing
                }

                @Override
                public boolean isSet() {
                    return true;
                }

                @Override
                public String getActiveModelName() {
                    return "TestModel";
                }
            };
            modes = new RMIModes() {
                private boolean running = false;
                private Map<BasicMode, RMIBasicMode> modes = new HashMap<>();

                {
                    RMIBasicMode unsupportedMode = new RMIBasicMode() {
                        @Override
                        public boolean isSupported() {
                            return false;
                        }

                        @Override
                        public boolean isRunning() {
                            return false;
                        }

                        @Override
                        public boolean start() {
                            return false;
                        }
                    };
                    modes.put(BasicMode.TEST_FORWARD, unsupportedMode);
                    modes.put(BasicMode.TEST_ROTATE, unsupportedMode);
                    modes.put(BasicMode.BEACON_FOLLOW, unsupportedMode);
                    modes.put(BasicMode.GRAPHICS_SCAN_LINES, unsupportedMode);
                    modes.put(BasicMode.GRAPHICS_SCAN_DOTS, unsupportedMode);
                    modes.put(BasicMode.SLAM_CLIENT, new RMISlamMode() {
                        private boolean modeRunning = false;
                        private Pose odometryPose = new Pose(0f, 0f, 0f);
                        private Pose realPose = new Pose(400f, 300f, 0f);
                        private final Random rand = new Random();
                        private final OccupancyMapImpl map = new OccupancyMapImpl(1000, 1000);

                        {
                            int centerX = 600;
                            int centerY = 500;
                            int size = 300;
                            for (int i = -size; i <= size; i++) {
                                map.set(i + centerX, -size + centerY, OccupancyMap.CELL_OCCUPIED_END);
                                map.set(i + centerX, size + centerY, OccupancyMap.CELL_OCCUPIED_END);
                                //map.set(-size + centerX, i + centerY, OccupancyMap.CELL_OCCUPIED_END);
                                map.set(size + centerX, i + centerY, OccupancyMap.CELL_OCCUPIED_END);
                            }

                            for (int i = -size - (-size / 3); i <= size - (size / 3); i++) {
                                map.set(centerX, i + centerY, OccupancyMap.CELL_OCCUPIED_END);
                                map.set(centerX + 10, i + centerY, OccupancyMap.CELL_OCCUPIED_END);
                            }

                            for (int i = 0; i <= 10; i++) {
                                map.set(i + centerX, -size - (-size / 3) + centerY, OccupancyMap.CELL_OCCUPIED_END);
                                map.set(i + centerX, size - (size / 3) + centerY, OccupancyMap.CELL_OCCUPIED_END);
                            }

                            for (int i = -size; i <= -size / 2; i++) {
                                map.set(-size + centerX, i + centerY, OccupancyMap.CELL_OCCUPIED_END);
                            }

                            for (int i = size / 2; i <= size; i++) {
                                map.set(-size + centerX, i + centerY, OccupancyMap.CELL_OCCUPIED_END);
                            }

                            for (int i = -size / 2; i <= size / 2; i++) {
                                map.set(i + centerX - (size + size / 2), -size / 2 + centerY, OccupancyMap.CELL_OCCUPIED_END);
                                map.set(i + centerX - (size + size / 2), size / 2 + centerY, OccupancyMap.CELL_OCCUPIED_END);
                                map.set(-size / 2 + centerX - (size + size / 2), i + centerY, OccupancyMap.CELL_OCCUPIED_END);
                                //map.set(size / 2 + centerX - (size + size / 2), i + centerY, OccupancyMap.CELL_OCCUPIED_END);
                            }
                        }

                        @Override
                        public Pose getOdometryPose() {
                            validateRun();
                            return odometryPose;
                        }

                        @Override
                        public void setOdometryPose(@NotNull Pose pose) {
                            validateRun();
                            this.odometryPose = pose;
                        }

                        @Override
                        public void move(@NotNull Move move) {
                            validateRun();

                            Thread.yield();
                            /*try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }*/

                            double realNoise = 1d;//(2 * rand.nextDouble() - 1) * 0.2;
                            double odometryNoise = 1d;//realNoise + (0.25 * rand.nextDouble() - 0.125d) * 0.2;
                            if (move instanceof TravelMove) {
                                float distance = ((TravelMove) move).getDistance();
                                odometryPose.moveUpdate((float) (distance * odometryNoise));
                                realPose.moveUpdate((float) (distance * realNoise));
                                return;
                            }
                            if (move instanceof RotateMove) {
                                float angle = ((RotateMove) move).getAngle();
                                odometryPose.rotateUpdate((float) (angle * odometryNoise));
                                realPose.rotateUpdate((float) (angle * realNoise));
                                return;
                            }

                            throw new IllegalArgumentException("Requested move is not usable, move: " + move);
                        }

                        @Override
                        public ScanResults scan() {
                            validateRun();
                            //long startTime = System.currentTimeMillis();

                            ScanResults results = new ScanResults();
                            {
                                //Rectangle rect = map.getBoundingRect();
                                float maxDistance = 250f;/*(float) Math.sqrt(Math.pow(rect.getWidth(), 2)
                                        + Math.pow(rect.getHeight(), 2));*/
                                Pose scannerPose = realPose.clone();
                                float baseAngle = scannerPose.getHeading();
                                for (float i = 0f; i <= 360f; i += 0.5f) {
                                    scannerPose.setHeading(baseAngle + i);
                                    float distance = map.getDistanceToWallFrom(scannerPose);
                                    results.add(distance > maxDistance ? Float.POSITIVE_INFINITY
                                            : distance, maxDistance, i);
                                }
                            }
                            Thread.yield();

                            /*try {
                                Thread.sleep(5000 - (System.currentTimeMillis() - startTime));
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }*/

                            return results;
                        }

                        @Override
                        public void stop() {
                            validateRun();
                            modeRunning = false;
                            running = false;
                        }

                        @Override
                        public boolean isSupported() {
                            return true;
                        }

                        @Override
                        public boolean isRunning() {
                            return modeRunning;
                        }

                        @Override
                        public boolean start() {
                            if (modeRunning) return false;
                            modeRunning = true;
                            running = true;
                            return true;
                        }

                        private void validateRun() {
                            if (!modeRunning)
                                throw new IllegalStateException(LOG_TAG + " is not running");
                        }
                    });
                }

                @Override
                public boolean isSupported(@NotNull BasicMode mode) {
                    return modes.get(mode).isSupported();
                }

                @Override
                public boolean isRunning(@Nullable BasicMode mode) {
                    return mode == null ? running : modes.get(mode).isRunning();
                }

                @Override
                public boolean start(@NotNull BasicMode mode) {
                    return !running && modes.get(mode).start();
                }

                @Override
                public RMIBasicMode getModeController(@NotNull BasicMode mode) {
                    return modes.get(mode);
                }
            };

            activeModelName = "TestModel";
            isModelSet = true;
        }

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
            if (activeClient != null) {
                activeClient.removeClientListener(CLIENT_LISTENER);
                activeClient.close();
            }
        }
    }

}
