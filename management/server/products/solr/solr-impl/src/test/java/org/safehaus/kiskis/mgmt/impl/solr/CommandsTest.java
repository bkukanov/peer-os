package org.safehaus.kiskis.mgmt.impl.solr;


import org.junit.BeforeClass;
import org.junit.Test;
import org.safehaus.kiskis.mgmt.api.commandrunner.Command;
import org.safehaus.kiskis.mgmt.product.common.test.unit.mock.CommandRunnerMock;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;


public class CommandsTest {

    private static Commands commands;

    @BeforeClass
    public static void setUp() {
        commands = new Commands( new CommandRunnerMock() );
    }


    @Test
    public void getInstallCommand() {
        Command command = commands.getInstallCommand( null );

        assertNotNull( command );
        assertEquals( Commands.INSTALL, command.getDescription() );
    }


    @Test
    public void getStartCommand() {
        Command command = commands.getStartCommand( null );

        assertNotNull( command );
        assertEquals( Commands.START, command.getDescription() );
    }


    @Test
    public void getStopCommand() {
        Command command = commands.getStopCommand( null );

        assertNotNull( command );
        assertEquals( Commands.STOP, command.getDescription() );
    }


    @Test
    public void getStatusCommand() {
        Command command = commands.getStatusCommand( null );

        assertNotNull( command );
        assertEquals( Commands.STATUS, command.getDescription() );
    }
}
