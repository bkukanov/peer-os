package org.safehaus.subutai.core.peer.cli;


import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;


@Command( scope = "peer", name = "jetty" )
public class JettyCommand extends OsgiCommandSupport
{
    private static Logger log = LoggerFactory.getLogger( JettyCommand.class );


    @Argument( index = 0, name = "command", multiValued = false, description = "start/stop/restart", required = true )
    private String command;

    @Argument( index = 1, name = "bundleName", multiValued = false, description = "start/stop/restart", required =
            false )
    private String PAX_WEB_JETTY_BUNDLE_NAME = "org.ops4j.pax.web.pax-web-jetty";

    @Override
    protected Object doExecute() throws Exception
    {
        Bundle[] bundles = getBundleContext().getBundles();
        for ( Bundle bundle : bundles )
        {
            if ( PAX_WEB_JETTY_BUNDLE_NAME.equals( bundle.getSymbolicName() ) )
            {
                System.out.println( String.format( "%s", bundle ) );
                if ( "start".equals( command ) )
                {
                    bundle.start();
                    //                    bundle.update();
                    System.out.println( "Jetty successfully started." );
                }
                else if ( "stop".equals( command ) )
                {
                    bundle.stop();
                    System.out.println( "Jetty successfully stopped." );
                }
                else if ( "restart".equals( command ) )
                {
                    bundle.stop();
                    bundle.start();
                    System.out.println( "Jetty successfully restarted." );
                }
                else if ( "status".equals( command ) )
                {
                    System.out.println( String.format( "Jetty bundle id: %d", bundle.getState() ) );
                }
                else if ( "update".equals( command ) )
                {
                    bundle.update();
                    System.out.println( "Successfully updated bundle." );
                }
                else
                {
                    System.out.println( "Unknown command. Available commands start/stop/restart" );
                }
                break;
            }
        }
        return null;
    }
}

