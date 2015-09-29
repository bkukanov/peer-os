package io.subutai.core.peer.impl.entity;


import java.util.HashSet;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import io.subutai.common.host.ContainerHostInfo;
import io.subutai.common.host.ContainerHostState;
import io.subutai.common.host.HostInfo;
import io.subutai.common.metric.ProcessResourceUsage;
import io.subutai.common.peer.ContainerHost;
import io.subutai.common.peer.Peer;
import io.subutai.common.peer.PeerException;
import io.subutai.common.protocol.Template;
import io.subutai.common.quota.CpuQuotaInfo;
import io.subutai.common.quota.DiskPartition;
import io.subutai.common.quota.DiskQuota;
import io.subutai.common.quota.RamQuota;
import io.subutai.core.peer.api.ContainerGroup;
import io.subutai.core.peer.api.ContainerGroupNotFoundException;
import io.subutai.core.peer.api.LocalPeer;
import io.subutai.common.peer.ResourceHost;


/**
 * ContainerHost class.
 */
@Entity
@Table( name = "container_host" )
@Access( AccessType.FIELD )
public class ContainerHostEntity extends AbstractSubutaiHost implements ContainerHost
{

    @ManyToOne( targetEntity = ResourceHostEntity.class )
    @JoinColumn( name = "parent_id" )
    private ResourceHost parent;

    @Column( name = "containerName" )
    private String containerName;

    @Column( name = "environment_id" )
    private String environmentId;

    @ElementCollection( targetClass = String.class, fetch = FetchType.EAGER )
    private Set<String> tags = new HashSet<>();

    @Transient
    private volatile ContainerHostState state = ContainerHostState.STOPPED;

    //    @Transient
    //    private DataService dataService;

    //    @Transient
    //    private LocalPeer localPeer;


    protected ContainerHostEntity()
    {
    }


    @Override
    public String getContainerName()
    {
        return containerName;
    }


    //    public void setLocalPeer( final LocalPeer localPeer )
    //    {
    //        this.localPeer = localPeer;
    //    }


    //    public void setDataService( final DataService dataService )
    //    {
    //        this.dataService = dataService;
    //    }


    public ContainerHostEntity( String peerId, HostInfo hostInfo, String environmentId )
    {
        super( peerId, hostInfo );

        updateHostInfo( hostInfo );

        this.containerName = ( ( ContainerHostInfo ) hostInfo ).getContainerName();
        this.environmentId = environmentId;
    }


    public void setEnvironmentId( final String environmentId )
    {
        this.environmentId = environmentId;
    }


    public String getEnvironmentId()
    {
        //        try
        //        {
        //            ContainerGroup containerGroup = localPeer.findContainerGroupByContainerId( getId() );
        //
        //            return containerGroup.getEnvironmentId();
        //        }
        //        catch ( ContainerGroupNotFoundException e )
        //        {
        //            return null;
        //        }
        return environmentId;
    }


    @Override
    public Peer getPeer()
    {
        return parent.getPeer();
    }


    @Override
    public void addTag( final String tag )
    {
        Preconditions.checkArgument( !Strings.isNullOrEmpty( tag ) );
        this.tags.add( tag );
        //        this.dataService.update( this );
    }


    @Override
    public void removeTag( final String tag )
    {
        Preconditions.checkArgument( !Strings.isNullOrEmpty( tag ) );
        this.tags.remove( tag );
        //        this.dataService.update( this );
    }


    @Override
    public Set<String> getTags()
    {
        return this.tags;
    }


    @Override
    public void setDefaultGateway( final String gatewayIp ) throws PeerException
    {
        getPeer().setDefaultGateway( this, gatewayIp );
    }


    @Override
    public boolean isLocal()
    {
        return true;
    }


    public ContainerHostState getStatus()
    {
        return state;
    }


    public ResourceHost getParent()
    {
        return parent;
    }


    public void setParent( final ResourceHost parent )
    {
        this.parent = parent;
    }


    //unsupported START
    public String getNodeGroupName()
    {
        throw new UnsupportedOperationException();
    }


    public String getTemplateName()
    {
        throw new UnsupportedOperationException();
    }


    public Template getTemplate() throws PeerException
    {
        throw new UnsupportedOperationException();
    }
    //unsupported END


    public void dispose() throws PeerException
    {
        Peer peer = getPeer();
        peer.destroyContainer( this );
    }


    @Override
    public void start() throws PeerException
    {
        Peer peer = getPeer();
        peer.startContainer( this );
    }


    @Override
    public void stop() throws PeerException
    {
        Peer peer = getPeer();
        peer.stopContainer( this );
    }


    @Override
    public boolean updateHostInfo( final HostInfo hostInfo )
    {
        super.updateHostInfo( hostInfo );

        ContainerHostInfo containerHostInfo = ( ContainerHostInfo ) hostInfo;
        this.state = containerHostInfo.getStatus();
        return false;
    }


    @Override
    public ProcessResourceUsage getProcessResourceUsage( final int processPid ) throws PeerException
    {
        Peer peer = getPeer();
        return peer.getProcessResourceUsage( this, processPid );
    }


    @Override
    public int getRamQuota() throws PeerException
    {
        return getPeer().getRamQuota( this );
    }


    @Override
    public RamQuota getRamQuotaInfo() throws PeerException
    {
        return getPeer().getRamQuotaInfo( this );
    }


    @Override
    public void setRamQuota( final int ramInMb ) throws PeerException
    {
        getPeer().setRamQuota( this, ramInMb );
    }


    @Override
    public void setRamQuota( final RamQuota ramQuota ) throws PeerException
    {
        getPeer().setRamQuota( this, ramQuota );
    }


    @Override
    public int getCpuQuota() throws PeerException
    {
        return getPeer().getCpuQuota( this );
    }


    @Override
    public CpuQuotaInfo getCpuQuotaInfo() throws PeerException
    {
        return getPeer().getCpuQuotaInfo( this );
    }


    @Override
    public void setCpuQuota( final int cpuPercent ) throws PeerException
    {
        getPeer().setCpuQuota( this, cpuPercent );
    }


    @Override
    public Set<Integer> getCpuSet() throws PeerException
    {
        return getPeer().getCpuSet( this );
    }


    @Override
    public void setCpuSet( final Set<Integer> cpuSet ) throws PeerException
    {
        getPeer().setCpuSet( this, cpuSet );
    }


    @Override
    public DiskQuota getDiskQuota( final DiskPartition diskPartition ) throws PeerException
    {
        return getPeer().getDiskQuota( this, diskPartition );
    }


    @Override
    public void setDiskQuota( final DiskQuota diskQuota ) throws PeerException
    {
        getPeer().setDiskQuota( this, diskQuota );
    }


    @Override
    public int getAvailableRamQuota() throws PeerException
    {
        return getPeer().getAvailableRamQuota( this );
    }


    @Override
    public int getAvailableCpuQuota() throws PeerException
    {
        return getPeer().getAvailableCpuQuota( this );
    }


    @Override
    public DiskQuota getAvailableDiskQuota( final DiskPartition diskPartition ) throws PeerException
    {
        return getPeer().getAvailableDiskQuota( this, diskPartition );
    }
}
