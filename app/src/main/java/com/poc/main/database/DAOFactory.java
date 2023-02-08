package com.poc.main.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DAOFactory {

    private Connection connection = null;
        private static DAOFactory dao;


        public static DAOFactory getInstance(){
            if(dao == null){
                dao = new DAOFactory();
            }
            return dao;
        }

        public static Connection getConnection() {
            DAOFactory d = getInstance();
            if (d.connection == null) {
                d.connect();
            }
            try {
                if (d.connection.isClosed()) {
                    d.connect();
                }
            } catch (SQLException sqlE) {
                sqlE.printStackTrace();
            }
            return d.connection;
        }

        public static String getDatabaseFile(){
            String name = DatabaseData.DATABASE_NAME+ DatabaseData.DATABASE_NUMBER;
            if(AbstractBO.hasOwnerPermission()){
                return AbstractBO.getInternalDatabaseFolder() + name;
            }else{
                return AbstractBO.getExternalDatabaseFolder() + name;
            }
        }

        public void connect(){
            try {
                Class.forName(getDriverName());
                connection = DriverManager.getConnection("jdbc:sqldroid:"+getDatabaseFile());
            } catch (ClassNotFoundException e) {  //Driver nao encontrado encontrado
                e.printStackTrace();
                System.out.println("O driver expecificado nao foi encontrado.");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Nao foi possivel conectar ao Banco de Dados.");
            }
        }

        public void close(){
            try{
                if(connection!=null){
                    connection.close();
                }
            } catch (SQLException e) {
                System.out.println("Nao foi possivel conectar ao Banco de Dados.");
            }
        }

        private String getDriverName() {
            return "org.sqldroid.SQLDroidDriver";
        }


}
