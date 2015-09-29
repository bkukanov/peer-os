package io.subutai.core.peer.impl.container;


import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import io.subutai.common.command.CommandException;
import io.subutai.common.command.CommandResult;
import io.subutai.common.command.CommandUtil;
import io.subutai.common.command.RequestBuilder;
import io.subutai.common.peer.ContainerHost;
import io.subutai.common.protocol.Template;
import io.subutai.common.settings.Common;
import io.subutai.common.util.NumUtil;
import io.subutai.core.peer.api.ContainerCreationException;
import io.subutai.common.peer.HostNotFoundException;
import io.subutai.common.peer.ResourceHost;
import io.subutai.common.peer.ResourceHostException;


public class CreateContainerTask implements Callable<ContainerHost>
{
    protected static final Logger LOG = LoggerFactory.getLogger( CreateContainerTask.class );
    private static final int TEMPLATE_IMPORT_TIMEOUT_SEC = 10 * 60 * 60;
    private final ResourceHost resourceHost;
    private final String hostname;
    private final Template template;
    private final String ip;
    private final int vlan;
    private final String gateway;
    private final int timeoutSec;
    protected CommandUtil commandUtil = new CommandUtil();


    public CreateContainerTask( final ResourceHost resourceHost, final Template template, final String hostname,
                                final String ip, final int vlan, final String gateway, final int timeoutSec )
    {
        Preconditions.checkNotNull( resourceHost );
        Preconditions.checkArgument( !Strings.isNullOrEmpty( hostname ) );
        Preconditions.checkNotNull( template );
        Preconditions.checkArgument( timeoutSec > 0 );

        this.resourceHost = resourceHost;
        this.template = template;
        this.hostname = hostname;
        this.ip = ip;
        this.vlan = vlan;
        this.gateway = gateway;
        this.timeoutSec = timeoutSec;
    }


    @Override
    public ContainerHost call() throws Exception
    {

        prepareTemplate( template );
        if ( !Strings.isNullOrEmpty( ip ) && ip.matches( Common.CIDR_REGEX ) && NumUtil
                .isIntBetween( vlan, Common.MIN_VLAN_ID, Common.MAX_VLAN_ID ) )
        {
            commandUtil.execute( new RequestBuilder( "subutai clone" ).withCmdArgs(
                    Lists.newArrayList( template.getTemplateName(), hostname, "-i",
                            String.format( "\"%s %s\"", ip, vlan ) ) ).withTimeout( 1 ).daemon(), resourceHost );
        }
        else
        {
            commandUtil.execute( new RequestBuilder( "subutai clone" )
                    .withCmdArgs( Lists.newArrayList( template.getTemplateName(), hostname ) ).withTimeout( 1 )
                    .daemon(), resourceHost );
        }
        long start = System.currentTimeMillis();

        ContainerHost containerHost = null;
        while ( System.currentTimeMillis() - start < timeoutSec * 1000 && ( containerHost == null || Strings
                .isNullOrEmpty( containerHost.getIpByInterfaceName( Common.DEFAULT_CONTAINER_INTERFACE ) ) ) )
        {
            Thread.sleep( 100 );
            try
            {
                containerHost = resourceHost.getContainerHostByName( hostname );
            }
            catch ( HostNotFoundException e )
            {
                //ignore
            }
        }

        if ( containerHost == null )
        {
            throw new ContainerCreationException(
                    String.format( "Container %s did not connect within timeout with proper IP", hostname ) );
        }
        else if ( !Strings.isNullOrEmpty( gateway ) && gateway.matches( Common.IP_REGEX ) )
        {
            containerHost.setDefaultGateway( gateway );
        }

        return containerHost;
    }


    protected void prepareTemplate( final Template template ) throws ResourceHostException
    {
        Preconditions.checkNotNull( template, "Invalid template" );

        if ( !templateExists( template ) )
        {
            importTemplate( template );

            if ( !templateExists( template ) )
            {
                LOG.debug( String.format( "Could not prepare template %s on %s.", template.getTemplateName(),
                        resourceHost.getHostname() ) );
                throw new ResourceHostException(
                        String.format( "Could not prepare template %s on %s", template.getTemplateName(),
                                resourceHost.getHostname() ) );
            }
        }
    }


    protected boolean templateExists( final Template template ) throws ResourceHostException
    {
        Preconditions.checkNotNull( template, "Invalid template" );

        try
        {
            CommandResult commandresult = resourceHost.execute( new RequestBuilder( "subutai list -t" )
                    .withCmdArgs( Lists.newArrayList( template.getTemplateName() ) ) );
            if ( commandresult.hasSucceeded() )
            {
                String[] lines = commandresult.getStdOut().split( "\n" );
                if ( lines.length == 3 && lines[2].startsWith( template.getTemplateName() ) )
                {
                    LOG.debug( String.format( "Template %s exists on %s.", template.getTemplateName(),
                            resourceHost.getHostname() ) );
                    return true;
                }
            }
            LOG.warn( String.format( "Template %s does not exists on %s.", template.getTemplateName(),
                    resourceHost.getHostname() ) );
            return false;
        }
        catch ( CommandException ce )
        {
            LOG.error( "Command exception.", ce );
            throw new ResourceHostException( "General command exception on checking container existence.", ce );
        }
    }


    protected void importTemplate( final Template template ) throws ResourceHostException
    {
        Preconditions.checkNotNull( template, "Invalid template" );

        try
        {
            commandUtil.execute( new RequestBuilder( "subutai import" ).withTimeout( TEMPLATE_IMPORT_TIMEOUT_SEC )
                                                                       .withCmdArgs( Lists.newArrayList(
                                                                               template.getTemplateName() ) ),
                    resourceHost );
        }
        catch ( CommandException ce )
        {
            LOG.error( "Template import failed", ce );
            throw new ResourceHostException( "Template import failed", ce );
        }
    }
}
