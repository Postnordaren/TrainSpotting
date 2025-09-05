import TSim.*;
import java.util.concurrent.Semaphore;

public class Lab1 {

  private static final int MAX_SPEED = 20; //vi kör detta än sålänge..
  public int sleeptime;

  public static Semaphore northStationSemaphore = new Semaphore(1); 
  public static Semaphore southStationSemaphore = new Semaphore(1); 

  public static Semaphore crossSemaphore = new Semaphore(1);


  public Lab1(int speed1, int speed2) {

    TSimInterface tsi = TSimInterface.getInstance();

    Thread train1 = new Thread(new Train(1, speed1));
    Thread train2 = new Thread(new Train(2, speed2));

    try {
      //tsi.setSwitch(17, 7, TSimInterface.SWITCH_RIGHT);
      //tsi.setSwitch(3, 11, TSimInterface.SWITCH_RIGHT);
     // tsi.setSwitch(4, 9, TSimInterface.SWITCH_RIGHT);

    }
    catch (Exception e) {

    }
    train1.start();
    train2.start();
  }


  public class Train implements Runnable {
    int trainId;
    int speed;
    int sleepTime;
    private final TSimInterface tsi = TSimInterface.getInstance();


    Train(int trainId, int speed) {
      this.trainId = trainId;
      this.speed = speed;
      this.sleepTime = 1000 + (20 * Math.abs(speed));
    }
    
    public void run() {
      try {
          tsi.setSpeed(trainId, speed);
          
          while (true) {
              SensorEvent sensor = tsi.getSensor(trainId); 

              int x = sensor.getXpos();
              int y = sensor.getYpos();
  
            if (sensor.getStatus() == SensorEvent.ACTIVE) {
            if (isNorthStation(x, y)) {
              northStationSensor(sensor);
            } else if (isSouthStation(x, y)) {
              southStationSensor(sensor);
            }// else if (isSensorbyCross(x, y)) {
             // crossSensor();
             handleSwitch(sensor);
            //}
          }
          
        }
      } catch (CommandException | InterruptedException e) {
          e.printStackTrace();
      }
  }
  
    public void switchDirection() throws CommandException {
      this.speed = -this.speed;
      tsi.setSpeed(trainId, this.speed);
    } 

    // Metod för att byta switch

    // sensor 15,7 : switch 17,7 till upp
    // sensor 18,7 : switch 17,7 till ner

    // sensor 16,9 : switch 15,9 till ner
    // sensor 14,9 : switch 15,9 till upp

    // sensor 6,10 : switch 4,9 till ner
    // sensor 2,9 : switch 4,9 till upp

    // sensor 2,11 : switch 3,11 till ner
    // sensor 3,12 : switch 3,11 till upp

    // sensor 4,11 : switch 3,11 till upp

    public void handleSwitch(SensorEvent sensor) throws CommandException {

      int x = sensor.getXpos();
      int y = sensor.getYpos();
  
      if (x == 15 && y == 7) {
          tsi.setSwitch(17, 7, TSimInterface.SWITCH_LEFT); // Switch till "upp"
      } else if (x == 18 && y == 7) {
          tsi.setSwitch(17, 7, TSimInterface.SWITCH_RIGHT); // Switch till "ner"
      } else if (x == 16 && y == 9) {
          tsi.setSwitch(15, 9, TSimInterface.SWITCH_RIGHT); // Switch till "ner"
      } else if (x == 14 && y == 9) {
          tsi.setSwitch(15, 9, TSimInterface.SWITCH_LEFT); // Switch till "upp"
      } else if (x == 6 && y == 10) {
          tsi.setSwitch(4, 9, TSimInterface.SWITCH_RIGHT); // Switch till "ner"
      } else if (x == 2 && y == 9) {
          tsi.setSwitch(4, 9, TSimInterface.SWITCH_LEFT); // Switch till "upp"
      } else if (x == 2 && y == 11) {
          tsi.setSwitch(3, 11, TSimInterface.SWITCH_RIGHT); // Switch till "ner"
      } else if (x == 3 && y == 12) {
          tsi.setSwitch(3, 11, TSimInterface.SWITCH_LEFT); // Switch till "upp"
      } else if (x == 6 && y == 11) {
          tsi.setSwitch(3, 11, TSimInterface.SWITCH_RIGHT); // Switch till "upp"
      }
  }

    
   public void northStationSensor(SensorEvent sensor) {
    //if (sensor.getStatus() == SensorEvent.ACTIVE) { // Bara när tåget kommer in
        try {
            System.out.println(sensor.getXpos() + " " + sensor.getYpos() + " ...");
            Lab1.northStationSemaphore.acquire();
            tsi.setSpeed(trainId, 0);
            Thread.sleep(sleepTime);
            switchDirection();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            Lab1.northStationSemaphore.release();
        }
    //}
  }

  public void southStationSensor(SensorEvent sensor) {
    //if (sensor.getStatus() == SensorEvent.ACTIVE) { // Bara när tåget kommer in
        try {
            System.out.println(sensor.getXpos() + " " + sensor.getYpos() + " ...");
            Lab1.northStationSemaphore.acquire();
            tsi.setSpeed(trainId, 0);
            Thread.sleep(sleepTime);
            switchDirection();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            Lab1.southStationSemaphore.release();
        }
    //}
  }

  public void crossSensor() throws CommandException {  
      try {
        Lab1.crossSemaphore.acquire();
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      } finally {
        Lab1.crossSemaphore.release();
      }
  }

    private boolean isNorthStation(int x, int y) {
      return (x == 14 && y == 5) || (x == 16 && y == 11);
    }
    
    private boolean isSouthStation(int x, int y) {
      return (x == 14 && y == 13) || (x == 14 && y == 4);
    }


    private boolean isSensorbyCross(int x, int y) {
      if (x == 7 && y == 7 || x == 8 && y == 8 || x == 15 && y == 7 || x == 18 && y == 7 || x == 16 && y == 9 || x == 14 && y == 9 ||
      x == 2 && y == 9 || x == 6 && y == 10 || x == 2 && y == 11 || x == 4 && y == 11 || x == 3 && y == 12 || x == 8 && y == 6){
        return true;
      }
      return false;
    }
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




