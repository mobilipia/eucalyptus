package com.eucalyptus.records;

import org.apache.log4j.Logger;
import org.mule.RequestContext;
import org.mule.api.MuleEvent;
import edu.ucsb.eucalyptus.msgs.EucalyptusMessage;

public class EventRecord extends EucalyptusMessage {
  private static Logger            LOG   = Logger.getLogger( EventRecord.class );
  
  public static Record create( final String component, final String eventUserId, final String eventCorrelationId, final Object eventName, final String other, int dist ) {
    return new LogFileRecord( component, eventUserId, eventCorrelationId, eventName.toString( ), getMessageString( other ), 3 + dist );
  }

  public static Record here( final Class component, final EventType eventName, final String... other ) {
    EucalyptusMessage msg = tryForMessage( );
    return create( component.getSimpleName( ), msg.getUserId( ), msg.getCorrelationId( ), eventName.toString( ), getMessageString( other ), 1 );
  }
    
  public static Record caller( final Class component, final EventType eventName, final Object... other ) {
    EucalyptusMessage msg = tryForMessage( );
    return create( component.getSimpleName( ), msg.getUserId( ), msg.getCorrelationId( ), eventName.toString( ), getMessageString( other ), 2 );
  }
  
  private static String getMessageString( final Object... other ) {
    StringBuffer last = new StringBuffer( );
    for ( Object x : other ) {
      last.append( ":" ).append( x.toString( ) );
    }
    return last.length( ) > 1 ? last.substring( 1 ) : last.toString( );
  }

  private static EucalyptusMessage BOGUS  = getBogusMessage( );
  private static EucalyptusMessage getBogusMessage( ) {
    EucalyptusMessage hi = new EucalyptusMessage( );
    hi.setUserId( null );
    hi.setEffectiveUserId( null );
    hi.setCorrelationId( null );
    return hi;
  }
  private static EucalyptusMessage tryForMessage( ) {
    EucalyptusMessage msg = null;
    MuleEvent event = RequestContext.getEvent( );
    if ( event != null ) {
      if ( event.getMessage( ) != null && event.getMessage( ).getPayload( ) != null && event.getMessage( ).getPayload( ) instanceof EucalyptusMessage ) {
        msg = ( ( EucalyptusMessage ) event.getMessage( ).getPayload( ) );
      }
    }
    return msg == null ? BOGUS : msg;
  }

  
}