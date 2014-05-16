package org.safehaus.kiskis.mgmt.product.common.test.unit.mock;


import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.safehaus.kiskis.mgmt.api.agentmanager.AgentListener;
import org.safehaus.kiskis.mgmt.api.agentmanager.AgentManager;
import org.safehaus.kiskis.mgmt.shared.protocol.Agent;


public class AgentManagerMock implements AgentManager {

    @Override
    public Set<Agent> getAgents() {
        return null;
    }


    @Override
    public Set<Agent> getPhysicalAgents() {
        return null;
    }


    @Override
    public Set<Agent> getLxcAgents() {
        return null;
    }


    @Override
    public Agent getAgentByHostname( String hostname ) {
        return null;
    }


    @Override
    public Agent getAgentByUUID( UUID uuid ) {
        return null;
    }


    @Override
    public Set<Agent> getLxcAgentsByParentHostname( String parentHostname ) {
        return null;
    }


    @Override
    public void addListener( AgentListener listener ) {

    }


    @Override
    public void removeListener( AgentListener listener ) {

    }
}
