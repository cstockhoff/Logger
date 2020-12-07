package logging;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author cstockhoff
 */
public class ResBundle {

	private ResourceBundle res;
	private final Locale locale;

	/**
	 * Creats a ResBundle with a explicit given Path to the ResourceBundle
	 *
	 * @param res Path to the ResourceBundle
	 */
	public ResBundle( String res ) {
		this( res, Locale.getDefault() );
	}

	/**
	 * Creates a ResBundle by a Class
	 * The canonical name of the class is used to locate the corresponding ResourceBundle
	 *
	 * @param clazz to which the ResBundle is associated with
	 */
	public ResBundle( Class<?> clazz ) {
		this( clazz.getCanonicalName(), Locale.getDefault() );
	}

	private ResBundle( String res, Locale loc ) {
		locale = loc;
		try {
			this.res = ResourceBundle.getBundle( res, locale );
		} catch( MissingResourceException | NullPointerException e ) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns a String given by a key
	 * Format: Key=Value
	 */
	public String getString( String key ) {
		if( res != null ) {
			if( key != null )
				return res.getString( key );
		} else
			Logger.info( "Properties missing" );
		return null;
	}

	/**
	 * Returns a Boolean given by a key
	 * Format: Key=Value
	 */
	public Boolean getBoolean( String key ) {
		Boolean retVal = null;
		if( res != null ) {
			if( key != null ) {
				try {
					retVal = Boolean.parseBoolean( res.getString( key ) );
				} catch( MissingResourceException e ) {
					Logger.info( e.getMessage() );
				}
			}
		} else
			Logger.info( "Properties missing" );
		return retVal;
	}

	/**
	 * Returns a Integer given by a key
	 * Format: Key=Value
	 */
	public Integer getInteger( String key ) {
		Integer retVal = null;
		if( res != null ) {
			if( key != null ) {
				try {
					retVal = Integer.parseInt( res.getString( key ) );
				} catch( NumberFormatException e ) {
					Logger.error( e );
				} catch( MissingResourceException e ) {
					Logger.info( e.getMessage() );
				}
			}
		} else
			Logger.info( "Properties missing" );
		return retVal;
	}

	/**
	 * Returns a Message given by a key (this method is handled like getString)
	 * Format: Key=Value
	 */
	public String getMessage( String key ) {
		return getMessage( key, "" );
	}

	/**
	 * Returns a Message given by a key
	 * The found value corresponding to the key is searched for {0},
	 * which is replaced by the Object-Content
	 * <p>
	 * Format: Key=Prefix-Value{0}Suffix-Value
	 */
	public String getMessage( String key, Object arg ) {
		Object[] args = new Object[]{ arg };
		return getMessage( key, args );
	}

	/**
	 * Returns a Message given by a key
	 * The found value corresponding to the key is searched for {0} - {args.length} replaceable parts
	 * Each replaceable part is replaced by its Object-Content from args
	 * <p>
	 * Format: Key=Prefix-Value{0}Suffix-Value
	 */
	public String getMessage( String key, Object[] args ) {
		String retVal = null;
		if( res != null ) {
			if( key != null ) {
				try {
					retVal = res.getString( key );
				} catch( MissingResourceException mre ) {
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
