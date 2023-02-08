package com.poc.main.database;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class AbstractBO {
	public Context context;

	public DatabaseData databaseData;
	
	public int cityId;
	public String DBVERSION = "";

	public static boolean hasOwnerPermission() {
		
		//if (android.os.Build.VERSION.SDK_INT >= 27) {
		//	return false;
		//}
		boolean ret = true;
		String path =  "/data/data/com.datalupa.android/databases/";
		FileOutputStream fileOuputStream = null;
		byte[] bFile = new byte[1];
		bFile[0] = 1;
		String fileDest = path + UUID.randomUUID().toString();

		try {
			File fPath = new File(path);
			if(!fPath.exists()) {
				fPath.mkdir();
			}
			fileOuputStream = new FileOutputStream(fileDest);
			fileOuputStream.write(bFile);

		} catch (IOException e) {
			e.printStackTrace();
			ret = false;
		} finally {
			if (fileOuputStream != null) {
				try {
					fileOuputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		if(ret) {
			try {
				new File(fileDest).delete();
			}catch(Exception ers) {}
		}
		return ret;
	}


	public static String getInternalDatabaseFolder() {
		String path = "/data/data/com.datalupa.android/databases/";
		return path;
	}
	
	public static String getExternalDatabaseFolder() {
		File sdcard = Environment.getExternalStorageDirectory();
		String dbfile = sdcard.getAbsolutePath() + File.separator+ "mkdatabase";
		File result = new File(dbfile);

		if (!result.exists()) {
			boolean created = result.mkdir();
			System.out.println("created="+created);
		}
		String a =  result.getAbsolutePath();
		return a + File.separator;
	}
	
	public AbstractBO(Context context, int cityId) {
		super();
		this.cityId = cityId;
		this.context = context;
		this.databaseData = DatabaseData.getDatabaseStock(context);

	}


	public String getDBVERSION() {
		return DBVERSION;
	}
	public void setDBVERSION(String dBVERSION) {
		DBVERSION = dBVERSION;
	}
	
	
}
