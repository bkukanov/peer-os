package org.safehaus.kiskis.mgmt.api.storm;

import java.util.UUID;
import org.safehaus.kiskis.mgmt.shared.protocol.ApiBase;

public interface Storm extends ApiBase<Config> {

    public UUID statusCheck(String clusterName, String hostname);

    public UUID startNode(String clusterName, String hostname);

    public UUID stopNode(String clusterName, String hostname);

    public UUID restartNode(String clusterName, String hostname);

    public UUID addNode(String clusterName, String hostname);

    public UUID destroyNode(String clusterName, String hostname);
}
