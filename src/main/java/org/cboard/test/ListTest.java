package org.cboard.test;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ok_vince on 2018-05-12.
 */
public class ListTest {

    public static void main(String args[]) {
        LinkedList linList = new LinkedList();
        linList.add("123");
        linList.add("33");
        linList.add("332");
        linList.add("144423");
        System.out.println(linList.getLast());
    }
}
