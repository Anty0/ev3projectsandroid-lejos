package eu.codetopic.anty.ev3projectsbase;

public class RMIVersionImpl implements RMIVersion {

    @Override
    public int getServerVersionCode() {
        return BaseConstants.SELF_VERSION_CODE;
    }
}
