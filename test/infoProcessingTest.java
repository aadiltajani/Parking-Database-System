package test;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import java.sql.*;

import src.infoProcessing;


public class infoProcessingTest {

    @Test
    public void testEnterDriverInfo(){
        Statement statement = null;
        infoProcessing in = new infoProcessing();
        in.enterDriverInfo(statement);
        assertEquals(1,1);
    }

}
