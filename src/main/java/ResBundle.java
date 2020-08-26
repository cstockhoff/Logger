import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

/**
 * @author cstockhoff
 */
public class ResBundle {
	private ResourceBundle res;
	private Locale locale;

	public ResBundle( String res ) {
		this( res, Locale.getDefault() );
	}

	public ResBundle( Class<?> c ) {
		this( c.getCanonicalName(), Locale.getDefault() );
	}

	private ResBundle( String res, Locale loc ) {
		locale = loc;
		try {
			this.res = ResourceBundle.getBundle( res, locale );
		} catch( MissingResourceException e ) {
			e.printStackTrace();
			return;
		} catch( NullPointerException e ) {
			e.printStackTrace();
			return;
		}
	}

	public String getString( String key ) {
		if( res != null ) {
			if( key != null ) return res.getString( key );
		} else
			Logger.info( "Properties missing" );
		return null;
	}

	public Boolean getBoolean( String key ) {
		Boolean retVal = null;
		if( res != null ) {
			if( key != null ) {
				try {
					retVal = Boolean.parseBoolean( res.getString( key ) );
				} catch( MissingResourceException e ) {
					retVal = null;
					Logger.info( e.getMessage() );
				}
			}
		} else
			Logger.info( "Properties missing" );
		return retVal;
	}

	public Integer getInteger( String key ) {
		Integer retVal = null;
		if( res != null ) {
			if( key != null ) {
				try {
					retVal = Integer.parseInt( res.getString( key ) );
				} catch( NumberFormatException e ) {
					Logger.error( e );
				} catch( MissingResourceException e ) {
					retVal = null;
					Logger.info( e.getMessage() );
				}
			}
		} else
			Logger.info( "Properties missing" );
		return retVal;
	}

	public String getMessage( String key ) {
		return getMessage( key, "" );
	}

	public String getMessage( String key, Object arg ) {
		Object[] args = new Object[]{ arg };
		return getMessage( key, args );
	}

	public String getMessage( String key, Object[] args ) {
		String retVal = null;
		if( res != null ) {
			if( key != null ) {
				try {
					retVal = res.getString( key );
				} catch( MissingResourceException mre ) {
					JOptionPane.showMessageDialog( null, key + " not available." );
					System.err.println( "Resource not found!" );
				}
				if( args != null ) {
					for( int i = 0; i < args.length; i++ ) {
						String argPosi = "{" + i + "}";
						if( args[ i ] == null )
							retVal = retVal.replace( argPosi, "" );
						else
							retVal = retVal.replace( argPosi, (String) args[ i ] );
					}
				}
			}
		} else
			Logger.info( "Properties missing" );
		return retVal;
	}
}
