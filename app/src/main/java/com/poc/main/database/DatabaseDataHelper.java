package com.poc.main.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseDataHelper extends SQLiteOpenHelper {

    //	The Android's default system path of your application database.
    private String DB_PATH;
    //    "/data/data/com.manhattan.wbus/databases/";

    private static String DB_NAME = DatabaseData.DATABASE_NAME+ DatabaseData.DATABASE_NUMBER;

    private SQLiteDatabase myDataBase;

    private final Context myContext;

    public DatabaseDataHelper(DatabaseContext context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
        DB_PATH = AbstractBO.getExternalDatabaseFolder();
    }

    public DatabaseDataHelper(Context context) {

        super(context, DB_NAME, null, 1);
        this.myContext = context;
        String pkg = myContext.getPackageName();
        DB_PATH = AbstractBO.getInternalDatabaseFolder();
        System.out.println(DB_PATH);
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     **/
    public void destroyCurrentVersion(){
        String outFileName = DB_PATH + DB_NAME;
        File f = new File(outFileName);
        f.delete();
        System.out.println("Destroy Current Base");
    }

    public void createDataBase() throws IOException {
        this.createDataBase(true);
    }

    public void createDataBase(boolean deleteOldVersions) throws IOException {

        boolean dbExist = checkDataBase();

        if(dbExist){
            System.out.println("##DBEXISTS");
        }else{
            System.out.println("##Crating News DATABASE");
            try{
                // By calling this method and empty database will be created into the default system path
                //of your application so we are gonna be able to overwrite that database with our database.
                this.getReadableDatabase();
                this.close();
            }catch(Exception ers){}

            try {

                copyDataBase();

            } catch (IOException e) {
                e.printStackTrace();
                //throw new Error("Error copying database");

            }
            if(deleteOldVersions){
                try {
                    System.out.println("deleting old versions");
                    deleteOlderVersionsDatabase();

                } catch (Exception e) {
                    e.printStackTrace();
                    //throw new Error("Error deleting database");

                }
            }
            //Toast.makeText(myContext, "Base de dados criada.", Toast.LENGTH_LONG);

            /*
             *  Cria registo do n�mero do telefone
             */
				/*try{
				   TelephonyManager tMgr =(TelephonyManager)myContext.getSystemService(Context.TELEPHONY_SERVICE);
				   String numero = tMgr.getLine1Number();
				   Database d = Database.getDatabase(myContext);
				   ParamGeralBO pBO = new ParamGeralBO(d);
				   String site = pBO.getAcessSite();
				   site = site+"registraNumero.php?num="+numero+"&pkg="+myContext.getPackageName();
				   System.out.println("Site:"+site);
				   URL yahoo = new URL(site);
				   BufferedReader in = new BufferedReader(
				               new InputStreamReader(
				               yahoo.openStream()));
				   in.close();

				 }catch(Exception ers){
					 ers.printStackTrace();
				 }*/


        }
    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * 	@return true if it exists, false if it doesn't
     */
    public boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){

            //	database does't exist yet.

        }

        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * 	system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException {

        String outFileName = DB_PATH + DB_NAME;

        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;

        String[] path =  myContext.getAssets().list("");

        int size = 0;

        for(String y:path){
            if(y.toLowerCase().indexOf("data")>=0){
                size = size+1;
            }
        }

        System.out.println("Number of parts"+ size);

        for(int i=1;i<=size;i++){
            InputStream myInput = myContext.getAssets().open(DB_NAME+".db."+ i);

            System.out.println("Processing"+DB_NAME+".db."+ i);

            while ((length = myInput.read(buffer))>0){
                myOutput.write(buffer, 0, length);
            }
            myInput.close();

        }

        //	Close the streams
        myOutput.flush();
        myOutput.close();

    }

    public void deleteOlderVersionsDatabase()  throws Exception {
        Integer number = Integer.parseInt(DatabaseData.DATABASE_NUMBER);
        if(number>1){
            for(int i=1;i<number;i++){
                File f = new File(DB_PATH + DatabaseData.DATABASE_NAME+i);
                if(f.exists()){
                    System.out.println("Deleting " + DatabaseData.DATABASE_NAME+i);
                    f.delete();
                }
                f = null;
            }

        }else{
            System.out.println("Deleting sschedulers");
            for(int i=1;i<240;i++){
                File f = new File(DB_PATH +"data"+i+".db");
                if(f.exists()){
                    System.out.println("Deleting " + "data"+i+".db");
                    f.delete();
                }
                f = null;
                f = new File(DB_PATH +"scheduler"+i);
                if(f.exists()){
                    System.out.println("Deleting " + "scheduler"+i);
                    f.delete();
                }
            }
        }
    }
    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void deleteDatabase() throws Exception {
        File f = new File(DB_PATH + DB_NAME);
        if(f.exists()){
            System.out.println("Deleting " + DB_NAME);
            f.delete();
        }
        f = null;
    }

    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
    // to you to create adapters for your views.

}

