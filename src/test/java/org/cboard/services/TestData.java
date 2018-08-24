package org.cboard.services;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestData {
    @Autowired
    DataManagerService dataManagerService=new DataManagerService();
    @Test
    public  void testDa(){


        dataManagerService.getTablesBySource(new Long(1));
    }
}
