package org.safehaus.subutai.core.message.api;


import java.util.UUID;

import org.safehaus.subutai.core.peer.api.PeerInterface;


/**
 * Message Queue API.
 */
public interface Queue
{

    /**
     * Creates message with specified payload
     *
     * @param payload - object to send
     *
     * @return - message envelope
     */
    public Message createMessage( Object payload ) throws MessageException;


    /**
     * Sends message to recipient
     *
     * @param peer - target peer
     * @param message - message to send
     * @param recipient - recipient of message
     * @param timeToLive - time-to-live (in seconds) of message within which message is retried to be sent to recipient.
     * After ttl expires and message is still not sent, it expires
     */
    public void sendMessage( final PeerInterface peer, final Message message, final String recipient,
                             final int timeToLive ) throws MessageException;

    /**
     * Returns status of message in queue
     *
     * @param messageId - id of message
     *
     * @return - status of message
     */
    public MessageStatus getMessageStatus( UUID messageId );

    /**
     * Add listener of messages
     *
     * @param listener - listener
     */
    public void addMessageListener( MessageListener listener );

    /**
     * Remove listener
     *
     * @param listener - listener
     */
    public void removeMessageListener( MessageListener listener );
}
