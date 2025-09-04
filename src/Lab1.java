import java.util.concurrent.Semaphore;

import TSim.*;

public class Lab1 {

  private static final int MAX_SPEED = 15; //vi kör detta än sålänge..
  public int sleeptime;

  public static Semaphore stationSemaphore = new Semaphore(1); 
  public static Semaphore crossSemaphore = new Semaphore(1);


  public Lab1(int speed1, int speed2) {

    TSimInterface tsi = TSimInterface.getInstance();

  
    Thread train1 = new Thread(new Train(1, speed1, sleeptime));
    Thread train2 = new Thread(new Train(2, speed2, sleeptime));
    train1.start();
    train2.start();

    try {
      tsi.setSpeed(1,speed1);
    }
    catch (CommandException e) {
      e.printStackTrace();    // or only e.getMessage() for the error
      System.exit(1);
    }
  }

  public class Train implements Runnable {
    int trainId;
    int speed;
    int sleepTime = 1000 + (20 * Math.abs(speed)); 


    Train(int trainId, int speed, int sleepTime) {
      this.trainId = trainId;
      this.speed = speed;
      this.sleepTime = sleepTime; 

    }

    public void run() {
      //will be continued
    }

    
   public void stationSensor(SensorEvent sensor, int trainid) {

    int x = sensor.getXpos(); 
    int y = sensor.getYpos(); 

    if (isSensorStation(x,y)){
    try {
        stationSemaphore.acquire();
        Thread.sleep(sleepTime); // Pausar tråden
    } catch (InterruptedException e) {
        e.printStackTrace();
    } finally {
        stationSemaphore.release();
    }
  }
}

  public void crossSensor(SensorEvent sensor, int trainId) {

    int x = sensor.getXpos();
    int y = sensor.getYpos();

    if (isSensorbyCross(x, y)) {
      try {
        crossSemaphore.acquire();
      } catch (InterruptedException e) {
        e.printStackTrace();
      } finally {
        crossSemaphore.release();
      }

  }
}
  

    private boolean isSensorStation(int x, int y){
      if (x == 14 && y == 5||x == 14 && y == 13){
       return true; 
      }
      return false; 
    }

    private boolean isSensorbyCross(int x, int y) {
      if (x == 7 && y == 7 || x == 8 && y == 8 || x == 15 && y == 7 || x == 18 && y == 7 || x == 16 && y == 9 || x == 14 && y == 9 ||
      x == 2 && y == 9 || x == 6 && y == 10 || x == 2 && y == 11 || x == 4 && y == 11 || x == 3 && y == 12 || x == 8 && y == 6){
        return true;
      }
      return false;
    }







}






  // varje sensor triggar en metod, inte mer än 10 semeforces. 


  /*  
  
  -- 1. Waiting at stations - trains must wait 1-2 seconds at each station after stopping.
  -- Avoid randomness
  
  -- 2. Maximum trainspeed, must be atleast 15
  -- All speeds in the range [0,maxpeed] must work.

  -- 3. Two trains, two threads but one implementation
  -- Use binary sempahores, no locks
  -- Trains mindre their own business
  -- Only shared variable is the semaphore.

  -- 4. Fixa sensormetod beroende på critical point.
  -- t.ex vid stationen ska tågen vänta 1-2 sekunder,
  -- dvs man kan ha en metod isStation?.
  */



}
