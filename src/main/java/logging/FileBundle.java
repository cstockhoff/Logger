package logging;

import java.io.*;
import java.util.ArrayList;

/**
 * @author cstockhoff
 */
public class FileBundle {

	/**
	 * Write Strings to a file given by its path
	 * Every Element in list will be written in a new line
	 *
	 * @param path of the file to be written
	 * @param list content to be written
	 */
	public static void writeFile( String path, ArrayList<String> list ) {
		File file = new File( path );
		try (BufferedWriter writer = new BufferedWriter( new FileWriter( file ) )) {
			for( String line : list ) {
				writer.write( line + "\n" );
			}
		} catch( IOException e ) {
			Logger.error( e );
		}
	}

	/**
	 * Read a file given by a reader
	 *
	 * @param reader Specific reader
	 * @return List of read lines
	 */
	public static ArrayList<String> readFile( Reader reader ) {
		ArrayList<String> retList = new ArrayList<>();
		try (BufferedReader bufReader = new BufferedReader( reader )) {
			String line;
			while( ( line = bufReader.readLine() ) != null ) {
				retList.add( line );
			}
		} catch( IOException e ) {
			Logger.error( e );
		}
		return retList;
	}

	/**
	 * Read a file given by a path
	 *
	 * @param path of the file to be read
	 * @return List of read lines
	 */
	public static ArrayList<String> readFile( String path ) {
		ArrayList<String> retList = new ArrayList<>();
		File tFile = new File( path );
		try (BufferedReader bufReader = new BufferedReader( new FileReader( tFile ) )) {
			String line;
			while( ( line = bufReader.readLine() ) != null ) {
				retList.add( line );
			}
		} catch( IOException e ) {
			Logger.error( e );
		}
		return retList;
	}

	/**
	 * Append the content of a file by the given string
	 *
	 * @param path   of the file
	 * @param string which should be appended
	 */
	public static void appendFile( String path, String string ) {
		ArrayList<String> list = new ArrayList<>();
		list.add( string );
		appendFile( path, list );
	}

	/**
	 * Append the content of a file by the given all strings in the list
	 * Every Element in list will be written in a new line
	 *
	 * @param path of the file
	 * @param list of string, which should be appended
	 */
	public static void appendFile( String path, ArrayList<String> list ) {
		File tFile = new File( path );
		try (BufferedWriter writer = new BufferedWriter( new FileWriter( tFile, true ) )) {
			for( String zeile : list ) {
				writer.write( zeile + "\n" );
			}
		} catch( IOException e ) {
			Logger.error( e );
		}
	}

	/**
	 * Delete a file specified by the path
	 *
	 * @param path to the file that should be deleted
	 */
	public static void delete( String path ) {
		File file = new File( path );
		if( file.exists() )
			file.delete();
	}

	/**
	 * Create a backup before deleting the file specified by the path
	 * The name of the backup is filepath + backupNr (see createBackups)
	 *
	 * @param path         to the file that should be deleted
	 * @param countBackups Indicates the count of backups
	 */
	public static void delete( String path, int countBackups ) {
		File file = new File( path );
		if( !file.exists() )
			return;

		if( countBackups > 0 ) {
			File backupFile = createBackups( path, 1, countBackups );
			try {
				copyFile( file, backupFile );
			} catch( IOException e ) {
				e.printStackTrace();
			}
		}
		file.delete();
	}

	/**
	 * Create backupNr-Backups recursively of the File path
	 * Old Backups will be moved one level down (that means, the backupNr is increased)
	 *
	 * @param path       original filepath
	 * @param backupNr   current backup-Number
	 * @param anzBackups max. Backups to be created
	 * @return Backup-File
	 */
	private static File createBackups( String path, int backupNr, int anzBackups ) {
		File backupFile = new File( path + "." + backupNr );
		if( backupFile.exists() && backupNr < anzBackups ) {
			File returnedFile = createBackups( path, backupNr + 1, anzBackups );
			try {
				copyFile( backupFile, returnedFile );
				backupFile.delete();
				backupFile.createNewFile();
			} catch( IOException e ) {
				e.printStackTrace();
			}
			return backupFile;
		} else {
			if( backupNr >= anzBackups )
				backupFile.delete();
			try {
				backupFile.createNewFile();
			} catch( IOException e ) {
				e.printStackTrace();
			}
			return backupFile;
		}
	}

	/**
	 * Simple Copy-File-Method
	 *
	 * @param input  copy-from
	 * @param output copy-to
	 * @throws IOException forwarded exception of FileInput- and FileOutputStream
	 */
	private static void copyFile( File input, File output ) throws IOException {
		BufferedInputStream in = new BufferedInputStream( new FileInputStream( input ) );
		BufferedOutputStream out = new BufferedOutputStream( new FileOutputStream( output, true ) );
		int bytes;
		while( ( bytes = in.read() ) != -1 ) {
			out.write( bytes );
		}
		in.close();
		out.close();
	}
}
