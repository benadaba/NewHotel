package suncertify.db;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */





// posted 24 January 2009 21:54:27
//Howdy y'all, what y'all doin'?!
//
//Guys, here are the tests I created to test the main functionalities
//of my implementation of the Data class and the locking mechanism.
//It also contains a header I created for each class.
//Some customization of this code will be necessary,
//according to the implementation of each developer.
//For instance, my project was the URLyBird 1.3.1,
//and my lock method does not return any value,
//and there are some cases where the lock method returns a long value,
//so this would be a case of customization.
//This test is supposed to be executed in less than 1 second, 
//so if it doesn't finish in less than 1 second, then the deadlock occurred.

//
//view plaincopy to clipboardprint?
/*
 * @(#)DataClassTest.java    1.0 05/11/2008
 *
 * Candidate: Roberto Perillo
 * Prometric ID: Your Prometric ID here
 * Candidate ID: Your candidade ID here
 *   
 * Sun Certified Developer for Java 2 Platform, Standard Edition Programming
 * Assignment (CX-310-252A)
 *
 * This class is part of the Programming Assignment of the Sun Certified
 * Developer for Java 2 Platform, Standard Edition certification program, must
 * not be used out of this context and must be used exclusively by Sun
 * Microsystems, Inc.
 */


/**
 * The <code>DataClassTest</code> tests the main functionalities of the
 * {@link Data} class. In order to simulate several clients trying to use it and
 * exercise the locking mechanism, it also has several inner classes that extend
 * the {@link Thread} class, where each class represents one client requesting
 * one operation, and mainly requesting updating and deletion of records. The
 * <code>FindingRecordsThread</code> exercises two functionalities: finding
 * records and reading records.
 *
 * @author Roberto Perillo
 * @version 1.0 05/11/2008
 */
public class DataClassTest {

    private static Data data ;


    /*
     * If any preparation has to be done before using the Data class, it can be
     * done in a static block; in this case, before using the Data class, the
     * loadDbRecords method has to be called prior to any other operation, so
     * the records in the physical .db file can be placed in the Map that keeps
     * them in memory; I also have a method called persistDbRecords, which
     * writes each record back to the physical .db file, but this test aims only
     * to test the functionalities without altering the database, so this method
     * is never called anywhere
     */


    public void startTests() {
        try {

            /*
             * Practically, it is not necessary to execute this loop more than 1
             * time, but if you want, you can increase the controller variable,
             * so it is executed as many times as you want
             */
            for (int i = 0; i < 5; i++) {
                Thread updatingRandom = new UpdatingRandomRecordThread();
                updatingRandom.start();
                Thread updatingRecord1 = new UpdatingRecord1Thread();
                updatingRecord1.start();
                Thread creatingRecord = new CreatingRecordThread();
                creatingRecord.start();
                Thread deletingRecord = new DeletingRecord1Thread();
                deletingRecord.start();
                Thread findingRecords = new FindingRecordsThread();
                findingRecords.start();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    private class UpdatingRandomRecordThread extends Thread {

        @Override
        public void run() {
            final HotelRoom room = new HotelRoom();
            room.setName("Palace");
            room.setLocation("Smallville");
            room.setSize("2");
            room.setSmoking(true);
            room.setRate("$150.00");
            room.setDate("2005, 06, 27");
            room.setOwner("54120584");

            final long recNo = (long) (Math.random() * 30);
            try {
            String[]    createRoom = {"Palace","Smallville","2","true",
            "$150.00","2005/06/27","54120584"};
                System.out.println(Thread.currentThread().getId()
                        + " trying to lock record #" + recNo
                        + " on UpdatingRandomRecordThread");

                /*
                 * The generated record number may not exist in the database, so
                 * a RecordNotFoundException must be thrown by the lock method.
                 * Since the database records are in a cache, it is not
                 * necessary to put the unlock instruction in a finally block,
                 * because an exception can only occur when calling the lock
                 * method (not when calling the update/delete methods),
                 * therefore it is not necessary to call the unlock method in a
                 * finally block, but you can customize this code according to
                 * your reality
                 */
                long cookie =  data.lockRecord(recNo);
                System.out.println(Thread.currentThread().getId()
                        + " trying to update record #" + recNo
                        + " on UpdatingRandomRecordThread");

                /*
                 * An exception cannot occur here, otherwise, the unlock
                 * instruction will not be reached, and the record will be
                 * locked forever. In this case, I created a class called
                 * HotelRoomRetriever, which transforms from HotelRoom to String array,
                 * and vice-versa, but it could also be done this way:
                 *
                 * data.update(recNo, new String[] {"Palace", "Smallville", "2",
                 * "Y", "$150.00", "2005/07/27", null});
                 */
                data.updateRecord(recNo, createRoom,cookie);
                System.out.println(Thread.currentThread().getId()
                        + " trying to unlock record #" + recNo
                        + " on UpdatingRandomRecordThread");
                data.unlockRecord(recNo, cookie);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private class UpdatingRecord1Thread extends Thread {

        @Override
        public void run() {
            final HotelRoom room = new HotelRoom();
            room.setName("Castle");
            room.setLocation("Digitopolis");
            room.setSize("2");
            room.setSmoking(false);
            room.setRate("$90.00");
            room.setDate("2004, 01, 17");
            room.setOwner("88006644");

            try {
                String[]    createRoom1 = {"Castle","Digitopolis","2","false",
                "$90.00","2004/01/17","88006644"};
                System.out.println(Thread.currentThread().getId()
                        + " trying to lock record #1 on"
                        + " UpdatingRecord1Thread");
                long cookie = data.lockRecord(1);
                System.out.println(Thread.currentThread().getId()
                        + " trying to update record #1 on"
                        + " UpdatingRecord1Thread");
                data.updateRecord(1,createRoom1 , cookie);
                System.out.println(Thread.currentThread().getId()
                        + " trying to unlock record #1 on"
                        + "UpdatingRecord1Thread");

                /*
                 * In order to see the deadlock, this instruction can be
                 * commented, and the other Threads, waiting to update/delete
                 * record #1 will wait forever and the deadlock will occur
                 */
                data.unlockRecord(1, cookie);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private class CreatingRecordThread extends Thread {

        @Override
        public void run() {
            HotelRoom room = new HotelRoom();
            room.setName("Kwabena");
            room.setLocation("EmeraldCity");
            room.setSize("6");
            room.setSmoking(false);
            room.setRate("$120.00");
            room.setDate("2003, 06, 10");

            try {
                String[]    createRoom2 = {"Kwabena","EmeraldCity",
                "6","false","$120.00","2003/06/10","34006644"};
                System.out.println(Thread.currentThread().getId()
                         +" trying to create a record");

          long val =  data.createRecord(createRoom2);

          System.out.println("record created at :" + val);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private class DeletingRecord1Thread extends Thread {

        @Override
        public void run() {
            //deleting at random
            final long recNo = (long) (Math.random() * 30);
            try {
                System.out.println(Thread.currentThread().getId()
                        + " trying to lock record #1 "+ recNo + " on "
                        + "DeletingRecord1Thread");
                long cookie3 = data.lockRecord(recNo);
                System.out.println(Thread.currentThread().getId()
                        + " trying to delete record #1" + recNo + " on "
                        + "DeletingRecord1Thread");
                data.deleteRecord(recNo, cookie3);
                System.out.println(Thread.currentThread().getId()
                        + " trying to unlock record #1 "+ recNo + " on "
                        + "DeletingRecord1Thread");
               data.unlockRecord(recNo,cookie3);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private class FindingRecordsThread extends Thread {

        @Override
        public void run() {
            try {
                System.out.println(Thread.currentThread().getId()
                        + " trying to find records");
                final String [] criteria = {"Be", "Xa"
                        , null, null,
                        null, null, null};

                 final long [] results = data.findByCriteria(criteria);
                // customized ff 2 lines all this myself
                 for(long r : results)
                 System.out.println(r);
                 int x = -1;
             label1:   for (int i = 0; i < results.length; i++) {
                    x++;
                    System.out.println(results.length + " results found.");
                    try {
                        final String message = Thread.currentThread().getId()
                                + " going to read record #" + results[i]
                                + " in FindingRecordsThread - still "
                                + ((results.length - 1) - i) + " to go.";
                        System.out.println(message);
                        //put this here myself
                        // but there should be a better way to get only
                        // record numbers from array returned by the find()
                        if(x>0&results[i]==0)
                            break label1;
                        
                        final String [] room = data.readRecord(results[i]);

                        for(String s : room){
                            System.out.println("Hotel (FindingRecordsThread): "
                                + s);
                        }
                        System.out.println("Has next? "
                                + (i < (results.length - 1)));

                    } catch (Exception e) {
                        /*
                         * In case a record was found during the execution of
                         * the find method, but deleted before the execution of
                         * the read instruction, a RecordNotFoundException will
                         * occur, which would be normal then
                         */
                        System.out.println("Exception in "
                                + "FindingRecordsThread - " + e);
                    }
                }
                System.out.println("Exiting for loop");
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public static void main(String [] args) {
       try {

        data = new Data("\\db-1x3.db");
        new DataClassTest().startTests();
       
       } catch(Exception e){
           System.err.println(e);
       }
    }

}

