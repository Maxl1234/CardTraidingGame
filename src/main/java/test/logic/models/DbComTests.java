package test.logic.models;

import com.example.app.mtcg.db.DbCom;
import org.junit.Test;

import java.util.Vector;

import static org.junit.Assert.*;

public class DbComTests {

    @Test
    public void testConnectDb() {
        DbCom dbCom = new DbCom();
        dbCom.connectdb();
        assertNotNull(dbCom);
        dbCom.disconectdb();
    }

    @Test
    public void testGetAllUsers() {
        DbCom dbCom = new DbCom();
        dbCom.connectdb();
        assertNotNull(dbCom.getAllUsers());
        dbCom.disconectdb();
    }


    @Test
    public void testGetPackage() {
        DbCom dbCom = new DbCom();
        dbCom.connectdb();
        assertNotNull(dbCom.getPackage());
        dbCom.disconectdb();
    }

}

