import java.io.PrintStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author cstockhoff
 */
@LoggingAnnotation.SkipClassLogging
public class Logger {

	public enum LocationLevel {
		FULL_QUALIFIED,
		CLASSNAME_METHODNAME_LINENUMBER,
		CLASSNAME_METHODNAME,
		METHODNAME_LINENUMBER,
		METHODNAME,
		NONE
	}

	public enum LogType {
		INFO,
		MESSAGE,
		DEBUG,
		ERROR,
		PRINT,
		SYSTEM
	}

	private static int anzLogBackups = 0;
	private static String logFile = "log.txt";

	public static int errorLogLevel = 20;

	public static LocationLevel locationLevel = LocationLevel.CLASSNAME_METHODNAME_LINENUMBER;

	public static boolean LOG_INFO = true;
	public static boolean LOG_PRINT = true;
	public static boolean LOG_MESSAGE = true;
	public static boolean LOG_SYSTEM = true;
	public static boolean LOG_ERROR = true;
	public static boolean LOG_DEBUG = true;

	public static boolean LOG_TO_IDE_CONSOLE = true;
	public static boolean LOG_TO_FILE = false;
	public static boolean LOG_DATE = false;

	private static final String INFO = "[" + "INFO" + "]" + " ";
	private static final String MESSAGE = "[" + "MESSAGE" + "]" + " ";
	private static final String SYSTEM = "[" + "SYSTEM" + "]" + " ";
	private static final String ERROR = "[" + "ERROR" + "]" + " ";
	private static final String DEBUG = "[" + "DEBUG" + "]" + " ";
	private static final String PRINT = "[" + "PRINT" + "]" + " ";

	private static final String defaultString = "{0}" + "{1}" + "{2}" + "{3}";
	private static String logString = defaultString;

	public static PrintStream out = System.out;

	static {
		new Logger();
	}

	public Logger() {
		ResBundle res = new ResBundle( "Logger" );
		if( res.getString( "logFile" ) != null )
			logFile = res.getString( "logFile" );
		if( res.getInteger( "anzLogBackups" ) != null )
			anzLogBackups = res.getInteger( "anzLogBackups" );
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
			LOG_TO_IDE_CONSOLE = res.getBoolean( "LOG_TO_IDE_CONSOLE" );
		if( res.getBoolean( "LOG_TO_FILE" ) != null )
			LOG_TO_FILE = res.getBoolean( "LOG_TO_FILE" );
		if( res.getBoolean( "LOG_DATE" ) != null )
			LOG_DATE = res.getBoolean( "LOG_DATE" );

		FileBundle fb = new FileBundle();
		fb.delete( logFile, anzLogBackups );
	}

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

	private static synchronized void error( StackTraceElement[] pSTE, String pError ) {
		if( !LOG_ERROR )
			return;
		String error = "[ " + "Message: " + pError + " ]" + "\n";

		String blanks = "";
		for( int i = 0; i < ERROR.length(); i++ )
			blanks += " ";

		String stackTrace = blanks + pSTE[ 0 ].toString();
		for( int i = 1; i < pSTE.length && i < errorLogLevel; i++ )
			stackTrace += "\n" + blanks + pSTE[ i ].toString();

		logString = logString.replace( "{0}", ERROR );
		logString = logString.replace( "{2}", error );
		logString = logString.replace( "{3}", stackTrace );

		log( LogType.ERROR );
	}

	public static synchronized void error( Exception e, Object error ) {
		error( e.getStackTrace(), error.toString() );
	}

	public static synchronized void error( Exception e ) {
		String exception = e.toString();
		error( e.getStackTrace(), exception );
	}

	public static synchronized void system( Object system ) {
		if( setupLogString( system.toString(), LOG_SYSTEM, SYSTEM ) )
			return;

		log( LogType.SYSTEM );
	}

	public static synchronized void debug( Object debug ) {
		if( setupLogString( debug.toString(), LOG_DEBUG, DEBUG ) )
			return;

		log( LogType.DEBUG );
	}

	public static synchronized void info( Object info ) {
		if( setupLogString( info.toString(), LOG_INFO, INFO ) )
			return;

		log( LogType.INFO );
	}

	public static synchronized void message( Object message ) {
		if( setupLogString( message.toString(), LOG_MESSAGE, MESSAGE ) )
			return;

		log( LogType.MESSAGE );
	}

	public static synchronized void print( Object text ) {
		/*if( !LOG_PRINT )
			return;
		logString = logString.replace( "{0}", "" );
		logString = logString.replace( "{2}", "" );
		logString = logString.replace( "{3}", text );
		// boolean dateFlag = false;
		// if( LOG_DATE ) dateFlag = true;
		log( LogType.PRINT );
		// if( dateFlag ) LOG_DATE = true;
		*/
		if( setupLogString( text.toString(), LOG_PRINT, PRINT ) )
			return;

		log( LogType.PRINT );
	}

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
						"[ " + elements[ index ].getClassName().substring( elements[ index ].getClassName().lastIndexOf( '.' )+ 1  )
								+ " - " + elements[ index ].getMethodName() + " ] " );
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

	private static int getLogIndex( StackTraceElement[] elements ) {
		int index = 1;
		boolean done = false;

		while( !done ) {
			try {
				Class<?> clazz = Class.forName( elements[ index ].getClassName() );

				if( clazz.isAnnotationPresent( LoggingAnnotation.SkipClassLogging.class ) && clazz.getAnnotation( LoggingAnnotation.SkipClassLogging.class ).value() ) {
					index++;
				} else {
					done = true;
				}
			} catch( ClassNotFoundException e ) {
				e.printStackTrace();
				done = true;
			}
		}
		return index;
	}

	private static synchronized void log( LogType logType ) {
		if( LOG_DATE )
			logString = logString.replace( "{1}", "[ " + new Date() + " ]" );
		else
			logString = logString.replace( "{1}", "" );

		if( LOG_TO_IDE_CONSOLE ) {
			if( logType == LogType.ERROR )
				out = System.err;
			else
				out = System.out;
			out.println( logString );
		}

		if( LOG_TO_FILE ) {
			FileBundle fb = new FileBundle();
			fb.appendFile( logFile, logString );
		}

		logString = defaultString;
	}

	public static void disable( LogType logType ) {
		toggleLogType( logType, false );
	}

	public static void enable( LogType logType ) {
		toggleLogType( logType, true );
	}

	public static void disableAll() {
		for( LogType logType : LogType.values() )
			disable( logType );
	}

	public static void disableAllWithout( LogType... args ) {
		List<LogType> tempList = Arrays.asList( args );

		for( LogType logType : LogType.values() )
			if( !tempList.contains( logType ) )
				disable( logType );

	}

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
