import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

/**
 * @author cstockhoff
 */
public class FileBundle {

	private BufferedWriter writer = null;
	private BufferedReader bufReader = null;

	public FileBundle() {}

	public void writeFile( String path, ArrayList<String> list ) {
		File file = new File( path );
		try {
			writer = new BufferedWriter( new FileWriter( file ) );

			for( String zeile : list ) {
				writer.write( zeile + "\n" );
			}
		} catch( IOException e ) {
			Logger.error( e );
		} finally {
			if( writer != null ) try {
				writer.close();
			} catch( IOException e ) {
				Logger.error( e );
			}
		}
	}

	public ArrayList<String> readFile( Reader reader ) {
		ArrayList<String> retList = new ArrayList<String>();
		try {
			bufReader = new BufferedReader( reader );

			String zeile = null;
			while( ( zeile = bufReader.readLine() ) != null ) {
				retList.add( zeile );
			}
		} catch( IOException e ) {
			Logger.error( e );
		} finally {
			if( reader != null ) try {
				reader.close();
			} catch( IOException e ) {
				Logger.error( e );
			}
		}
		return retList;
	}

	public ArrayList<String> readFile( String path ) {
		ArrayList<String> retList = new ArrayList<String>();
		File tFile = new File( path );
		try {
			bufReader = new BufferedReader( new FileReader( tFile ) );

			String zeile = null;
			while( ( zeile = bufReader.readLine() ) != null ) {
				retList.add( zeile );
			}
		} catch( IOException e ) {
			Logger.error( e );
		} finally {
			if( bufReader != null ) try {
				bufReader.close();
			} catch( IOException e ) {
				Logger.error( e );
			}
		}
		return retList;
	}

	public void appendFile( String path, String string ) {
		ArrayList<String> list = new ArrayList<String>();
		list.add( string );
		appendFile( path, list );
	}

	public void appendFile( String path, ArrayList<String> list ) {
		File tFile = new File( path );
		try {
			writer = new BufferedWriter( new FileWriter( tFile, true ) );

			for( String zeile : list ) {
				writer.write( zeile + "\n" );
			}
		} catch( IOException e ) {
			Logger.error( e );
		} finally {
			if( writer != null ) try {
				writer.close();
			} catch( IOException e ) {
				Logger.error( e );
			}
		}
	}

	public void delete( String path ) {
		File file = new File( path );
		if( file.exists() ) file.delete();
	}

	public void delete( String path, int anzBackups ) {
		File file = new File( path );
		if( !file.exists() ) return;

		if( anzBackups > 0 ) {
			File backupFile = createBackups( path, 1, anzBackups );
			try {
				copyFile( file, backupFile );
			} catch( FileNotFoundException e ) {
				e.printStackTrace();
			} catch( IOException e ) {
				e.printStackTrace();
			}
		}
		file.delete();
	}

	private File createBackups( String path, int backupNr, int anzBackups ) {
		File backupFile = new File( path + "." + backupNr );
		if( backupFile.exists() && backupNr < anzBackups ) {
			File returnedFile = createBackups( path, backupNr + 1, anzBackups );
			try {
				copyFile( backupFile, returnedFile );
				backupFile.delete();
				backupFile.createNewFile();
			} catch( FileNotFoundException e ) {
				e.printStackTrace();
			} catch( IOException e ) {
				e.printStackTrace();
			}
			return backupFile;
		} else {
			if( backupNr >= anzBackups ) backupFile.delete();
			try {
				backupFile.createNewFile();
			} catch( IOException e ) {
				e.printStackTrace();
			}
			return backupFile;
		}
	}

	private void copyFile( File input, File output ) throws IOException {
		BufferedInputStream in = new BufferedInputStream( new FileInputStream( input ) );
		BufferedOutputStream out = new BufferedOutputStream( new FileOutputStream( output, true ) );
		int bytes = 0;
		while( ( bytes = in.read() ) != -1 ) {
			out.write( bytes );
		}
		in.close();
		out.close();
	}
}
