package logging;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author cstockhoff
 */
@LoggingAnnotation.SkipLogging
public class Logger {

	/**
	 * Different Location-Levels
	 * <p>
	 * FULL_QUALIFIED: [ ClassName (with package) - MethodName - LineNumber ]
	 * CLASSNAME_METHODNAME_LINENUMBER: [ ClassName (without package) - MethodName - LineNumber ]
	 * CLASSNAME_METHODNAME: [ ClassName - MethodName ]
	 * CLASSNAME_LINENUMBER: [ ClassName - LineNumber ]
	 * METHODNAME_LINENUMBER: [ MethodName - LineNumber ]
	 * METHODNAME: [ MethodName ]
	 * NONE
	 * <p>
	 * see locationLevel-Attribute
	 */
	public enum LocationLevel {
		FULL_QUALIFIED,
		CLASSNAME_METHODNAME_LINENUMBER,
		CLASSNAME_METHODNAME,
		CLASSNAME_LINENUMBER, // DEFAULT
		METHODNAME_LINENUMBER,
		METHODNAME,
		NONE
	}

	/**
	 * Different available Log-Types
	 * Can be individually switched on/off
	 */
	public enum LogType {
		INFO,
		MESSAGE,
		DEBUG,
		ERROR,
		PRINT,
		SYSTEM
	}

	public static final LocationLevel locationLevel = LocationLevel.CLASSNAME_LINENUMBER;

	/**
	 * Value that indicates how many Log-File-Backups should be created
	 * Will be numbered from zero to countLogBackups (if greater zero)
	 * (default: 0)
	 * <p>
	 * If Logger.properties exists, this value will be overwritten, but
	 * only if the file contains a value for this attribute
	 */
	private static int countLogBackups = 0;

	/**
	 * Name/Path of the Log-File (will be created if it does not exist)
	 * If LOG_TO_FILE is true, the logs will be written to this file
	 */
	private static String logFile = "log.txt";

	/**
	 * Choose, wow many Elements of the StackTrace should be printed, if an error is logged
	 * (default: 20)
	 * <p>
	 * If Logger.properties exists, this value will be overwritten, but
	 * only if the file contains a value for this attribute
	 */
	public static int errorLogLevel = 20;

	/**
	 * Enable/Disable all INFO-Logs
	 * <p>
	 * If Logger.properties exists, this value will be overwritten, but
	 * only if the file contains a value for this attribute
	 */
	public static boolean LOG_INFO = true;

	/**
	 * Enable/Disable all PRINT-Logs
	 * <p>
	 * If Logger.properties exists, this value will be overwritten, but
	 * only if the file contains a value for this attribute
	 */
	public static boolean LOG_PRINT = true;

	/**
	 * Enable/Disable all MESSAGE-Logs
	 * <p>
	 * If Logger.properties exists, this value will be overwritten, but
	 * only if the file contains a value for this attribute
	 */
	public static boolean LOG_MESSAGE = true;

	/**
	 * Enable/Disable all SYSTEM-Logs
	 * <p>
	 * If Logger.properties exists, this value will be overwritten, but
	 * only if the file contains a value for this attribute
	 */
	public static boolean LOG_SYSTEM = true;

	/**
	 * Enable/Disable all ERROR-Logs
	 * <p>
	 * If Logger.properties exists, this value will be overwritten, but
	 * only if the file contains a value for this attribute
	 */
	public static boolean LOG_ERROR = true;

	/**
	 * Enable/Disable all DEBUG-Logs
	 * <p>
	 * If Logger.properties exists, this value will be overwritten, but
	 * only if the file contains a value for this attribute
	 */
	public static boolean LOG_DEBUG = true;

	/**
	 * Enable/Disable log to the individual chosen PrintStream (see attribute 'out')
	 * <p>
	 * If Logger.properties exists, this value will be overwritten, but
	 * only if the file contains a value for this attribute
	 * <p>
	 * (default: true)
	 */
	public static boolean LOG_TO_PRINTSTREAM = true;

	/**
	 * Enable/Disable log to the individual chosen file (see attribute 'logFile')
	 * <p>
	 * If Logger.properties exists, this value will be overwritten, but
	 * only if the file contains a value for this attribute
	 * <p>
	 * (default: false)
	 */
	public static boolean LOG_TO_FILE = false;

	/**
	 * Enable/Disable logging the date
	 * <p>
	 * If Logger.properties exists, this value will be overwritten, but
	 * only if the file contains a value for this attribute
	 * <p>
	 * (default: false)
	 */
	public static boolean LOG_DATE = false;

	/**
	 * Log-Indicator for INFO-Logs
	 */
	private static final String INFO = "[" + "INFO" + "]" + " ";

	/**
	 * Log-Indicator for MESSAGE-Logs
	 */
	private static final String MESSAGE = "[" + "MESSAGE" + "]" + " ";

	/**
	 * Log-Indicator for SYSTEM-Logs
	 */
	private static final String SYSTEM = "[" + "SYSTEM" + "]" + " ";

	/**
	 * Log-Indicator for ERROR-Logs
	 */
	private static final String ERROR = "[" + "ERROR" + "]" + " ";

	/**
	 * Log-Indicator for DEBUG-Logs
	 */
	private static final String DEBUG = "[" + "DEBUG" + "]" + " ";

	/**
	 * Log-Indicator for PRINT-Logs
	 */
	private static final String PRINT = "[" + "PRINT" + "]" + " ";

	/**
	 * Log-String: Replacing of the different parts
	 * {0} : Replaced by the Log-Indicator
	 * {1} : Replaced by non or current Date
	 * {2} : Replaced by chosen Location-Level
	 * {3} : Replaced by individual content
	 */
	private static final String defaultString = "{0}" + "{1}" + "{2}" + "{3}";

	/**
	 * After every log, this String is reset to the defaultString
	 */
	private static String logString = defaultString;

	/**
	 * Individual PrintStream (default is System.out)
	 * If LOG_TO_IDE_CONSOLE is true, the logs will be forwarded to this PrintStream
	 */
	public static PrintStream out = System.out;

	static {
		//noinspection InstantiationOfUtilityClass
		new Logger();
	}

	public Logger() {
		ResBundle res = new ResBundle( "Logger" );
		if( res.getString( "logFile" ) != null )
			logFile = res.getString( "logFile" );
		if( res.getInteger( "anzLogBackups" ) != null )
			countLogBackups = res.getInteger( "anzLogBackups" );
		if( res.getInteger( "errorLogLevel" ) != null )
			errorLogLevel = res.getInteger( "errorLogLevel" );
		if( res.getBoolean( "LOG_INFO" ) != null )
			LOG_INFO = res.getBoolean( "LOG_INFO" );
		if( res.getBoolean( "LOG_PRINT" ) != null )
			LOG_PRINT = res.getBoolean( "LOG_PRINT" );
		if( res.getBoolean( "LOG_MESSAGE" ) != null )
			LOG_MESSAGE = res.getBoolean( "LOG_MESSAGE" );
		if( res.getBoolean( "LOG_SYSTEM" ) != null )
			LOG_SYSTEM = res.getBoolean( "LOG_SYSTEM" );
		if( res.getBoolean( "LOG_DEBUG" ) != null )
			LOG_DEBUG = res.getBoolean( "LOG_DEBUG" );
		if( res.getBoolean( "LOG_ERROR" ) != null )
			LOG_ERROR = res.getBoolean( "LOG_ERROR" );
		if( res.getBoolean( "LOG_TO_IDE_CONSOLE" ) != null )
			LOG_TO_PRINTSTREAM = res.getBoolean( "LOG_TO_IDE_CONSOLE" );
		if( res.getBoolean( "LOG_TO_FILE" ) != null )
			LOG_TO_FILE = res.getBoolean( "LOG_TO_FILE" );
		if( res.getBoolean( "LOG_DATE" ) != null )
			LOG_DATE = res.getBoolean( "LOG_DATE" );

		FileBundle.delete( logFile, countLogBackups );
	}

	/**
	 * Logs a splitter in form of '>>> ---------- ----------  <<<'
	 *
	 * @param type Log-Type for the splitter
	 *             (supported LogTypes: INFO, MESSAGE, DEBUG, PRINT, SYSTEM)
	 */
	public static synchronized void splitter( LogType type ) {
		String splitter = ">>> ---------- ----------  <<<";
		switch( type ) {
			case INFO:
				info( splitter );
				break;
			case MESSAGE:
				message( splitter );
				break;
			case DEBUG:
				debug( splitter );
				break;
			case PRINT:
				print( splitter );
				break;
			case SYSTEM:
				system( splitter );
				break;
		}
	}

	/**
	 * Analyzes the StackTrace to the depth depending on the errorLogLevel and
	 * concatenate the StackTraceElement to one String
	 *
	 * @param pSTE   StackTrace of the Exception
	 * @param pError Message to be logged
	 */
	private static synchronized void error( StackTraceElement[] pSTE, String pError ) {
		if( !LOG_ERROR )
			return;
		String error = "[ " + "Message: " + pError + " ]" + "\n";

		StringBuilder blanks = new StringBuilder();
		blanks.append( " ".repeat( ERROR.length() ) );

		StringBuilder stackTrace = new StringBuilder( blanks + pSTE[ 0 ].toString() );
		for( int i = 1; i < pSTE.length && i < errorLogLevel; i++ )
			stackTrace.append( "\n" ).append( blanks ).append( pSTE[ i ].toString() );

		logString = logString.replace( "{0}", ERROR );
		logString = logString.replace( "{2}", error );
		logString = logString.replace( "{3}", stackTrace.toString() );

		log( LogType.ERROR );
	}

	/**
	 * Log an Exception with a specific message
	 *
	 * @param e     Exception to be logged/analyzed
	 * @param error Individual message to be logged
	 */
	public static synchronized void error( Exception e, Object error ) {
		error( e.getStackTrace(), error.toString() );
	}

	/**
	 * Log an Exception with a specific message
	 *
	 * @param e Exception to be logged/analyzed
	 */
	public static synchronized void error( Exception e ) {
		String exception = e.toString();
		error( e.getStackTrace(), exception );
	}

	/**
	 * Log a text on the SYSTEM-Level
	 *
	 * @param system Individual text/string
	 */
	public static synchronized void system( Object system ) {
		if( setupLogString( system.toString(), LOG_SYSTEM, SYSTEM ) )
			return;

		log( LogType.SYSTEM );
	}

	/**
	 * Log a text on the SYSTEM-Level
	 *
	 * @param debug Individual text/string
	 */
	public static synchronized void debug( Object debug ) {
		if( setupLogString( debug.toString(), LOG_DEBUG, DEBUG ) )
			return;

		log( LogType.DEBUG );
	}

	/**
	 * Log a text on the INFO-Level
	 *
	 * @param info Individual text/string
	 */
	public static synchronized void info( Object info ) {
		if( setupLogString( info.toString(), LOG_INFO, INFO ) )
			return;

		log( LogType.INFO );
	}

	/**
	 * Log a text on the INFO-Level
	 *
	 * @param message Individual text/string
	 */
	public static synchronized void message( Object message ) {
		if( setupLogString( message.toString(), LOG_MESSAGE, MESSAGE ) )
			return;

		log( LogType.MESSAGE );
	}

	/**
	 * Log a text on the PRINT-Level
	 *
	 * @param text Individual string/text
	 */
	public static synchronized void print( Object text ) {
		if( setupLogString( text.toString(), LOG_PRINT, PRINT ) )
			return;

		log( LogType.PRINT );
	}

	/**
	 * Prepares the Log-String (Replaces the different parts)
	 * {0} : Replaced by the Log-Indicator
	 * {1} : Not replaced yet!
	 * {2} : Replaced by chosen Location-Level
	 * {3} : Replaced by individual content
	 *
	 * @param arg  text to be logged
	 * @param log  Log-Level
	 * @param type Indicator of the Log-Level
	 * @return Prepared Log-String
	 */
	private static boolean setupLogString( String arg, boolean log, String type ) {
		if( !log )
			return true;
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();

		logString = logString.replace( "{0}", type );

		int index = getLogIndex( elements );

		switch( locationLevel ) {
			case FULL_QUALIFIED:
				logString = logString.replace( "{2}",
						"[ " + elements[ index ].getClassName()
								+ " - " + elements[ index ].getMethodName()
								+ ":" + elements[ index ].getLineNumber() + " ] " );
				break;
			case CLASSNAME_METHODNAME_LINENUMBER:
				logString = logString.replace( "{2}",
						"[ " + elements[ index ].getClassName().substring( elements[ index ].getClassName().lastIndexOf( '.' ) + 1 )
								+ " - " + elements[ index ].getMethodName()
								+ ":" + elements[ index ].getLineNumber() + " ] " );
				break;
			case CLASSNAME_METHODNAME:
				logString = logString.replace( "{2}",
						"[ " + elements[ index ].getClassName().substring( elements[ index ].getClassName().lastIndexOf( '.' ) + 1 )
								+ " - " + elements[ index ].getMethodName() + " ] " );
				break;
			case CLASSNAME_LINENUMBER:
				logString = logString.replace( "{2}",
						"[ " + elements[ index ].getClassName().substring( elements[ index ].getClassName().lastIndexOf( '.' ) + 1 )
								+ ":" + elements[ index ].getLineNumber() + " ] " );
				break;
			case METHODNAME_LINENUMBER:
				logString = logString.replace( "{2}",
						"[ " + elements[ index ].getMethodName()
								+ ":" + elements[ index ].getLineNumber() + " ] " );
				break;
			case METHODNAME:
				logString = logString.replace( "{2}",
						"[ " + elements[ index ].getMethodName() + " ] " );
				break;
			case NONE:
			default:
				logString = logString.replace( "{2}", "" );
				break;
		}

		logString = logString.replace( "{3}", arg );
		return false;
	}

	/**
	 * Analyzes the StackTraceElement-Classes and look up, if the Class must be skipped or not
	 *
	 * @param elements StackTraceElements to be analyzed
	 * @return Index of the first non annotated Class with SkipClassLogging
	 */
	private static int getLogIndex( StackTraceElement[] elements ) {
		int index = 1;
		boolean done = false;

		while( !done ) {
			try {
				Class<?> clazz = Class.forName( elements[ index ].getClassName() );

				// skip class maybe?
				if( clazz.isAnnotationPresent( LoggingAnnotation.SkipLogging.class ) && clazz.getAnnotation( LoggingAnnotation.SkipLogging.class ).value() ) {
					index++;
				} else {
					// skip method maybe?
					int finalIndex = index;
					Method method = Arrays.stream( clazz.getDeclaredMethods() ).filter( a -> a.getName().equals( elements[ finalIndex ].getMethodName() ) ).findFirst().get();
					if( method.isAnnotationPresent( LoggingAnnotation.SkipLogging.class ) && method.getAnnotation( LoggingAnnotation.SkipLogging.class ).value() ) {
						index++;
					} else {
						done = true;
					}
				}
			} catch( ClassNotFoundException e ) {
				e.printStackTrace();
				done = true;
			}
		}
		return index;
	}

	/**
	 * Log the Log-String based on the given Log-Level to the
	 * Print-Stream (if LOG_TO_PRINTSTREAM is true) and to the
	 * File logFile (if LOG_TO_FILE is true)
	 * Replaces {1} by the current Date, if LOG_DATE is true
	 * <p>
	 * At the end it will reset the logString to the defaultString
	 *
	 * @param logType Log-Level, to which the text/string is logged
	 */
	private static synchronized void log( LogType logType ) {
		if( LOG_DATE )
			logString = logString.replace( "{1}", "[ " + new Date() + " ]" );
		else
			logString = logString.replace( "{1}", "" );

		if( LOG_TO_PRINTSTREAM ) {
			if( logType == LogType.ERROR )
				out = System.err;
			else
				out = System.out;
			out.println( logString );
		}

		if( LOG_TO_FILE )
			FileBundle.appendFile( logFile, logString );

		logString = defaultString;
	}

	/**
	 * Disable a LogType
	 */
	public static void disable( LogType logType ) {
		toggleLogType( logType, false );
	}

	/**
	 * Enable a LogType
	 */
	public static void enable( LogType logType ) {
		toggleLogType( logType, true );
	}

	/**
	 * Disable all LogTypes
	 */
	public static void disableAll() {
		for( LogType logType : LogType.values() )
			disable( logType );
	}

	/**
	 * Disable all LogTypes, except the given LogTypes
	 */
	public static void disableAllWithout( LogType... args ) {
		List<LogType> tempList = Arrays.asList( args );

		for( LogType logType : LogType.values() )
			if( !tempList.contains( logType ) )
				disable( logType );

	}

	/**
	 * Set logging of the Log-Type to arg
	 *
	 * @param logType to be enable/disable
	 * @param arg     LogType to be set to
	 */
	private static void toggleLogType( LogType logType, boolean arg ) {
		switch( logType ) {
			case INFO:
				LOG_INFO = arg;
				break;
			case MESSAGE:
				LOG_MESSAGE = arg;
				break;
			case DEBUG:
				LOG_DEBUG = arg;
				break;
			case ERROR:
				LOG_ERROR = arg;
				break;
			case PRINT:
				LOG_PRINT = arg;
				break;
			case SYSTEM:
				LOG_SYSTEM = arg;
				break;
		}
	}
}
