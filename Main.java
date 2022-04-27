class Database{
  private int readers; // number of active readers
  private int writers; // number of active writers
  private int number;

  private Semaphore mutex;
  private Semaphore writer_lock;
  private Semaphore reader_lock;

  /**
   * Initializes this database.
   */
  public Database(){
    readers = 0;
	writers = 0;
    number = 0;
    mutex = new Semaphore(1);
    writer_lock = new Semaphore(1);
	reader_lock = new Semaphore(1);
  }

  /**
   * Read from this database.
   */
  public void read(){
    mutex.sema_wait();
    readers++;
	while(writers != 0){
		reader_lock.sema_wait();
	}
    if (readers == 1){
      writer_lock.sema_wait();
    }
    mutex.sema_post();
    System.err.println("Reader " + Thread.currentThread().getName() + " starts reading: "+number);

    // simulate reading time
    final int DELAY = 1000;
    try{
      Thread.sleep((int) (Math.random() * DELAY));
    }catch (InterruptedException e) {}

    System.err.println("Reader " + Thread.currentThread().getName() + " stops reading.");
		while(writers != 0){
		reader_lock.sema_wait();
	}
    mutex.sema_wait();
    readers--;
    if (readers == 0){
      writer_lock.sema_post();
    }
    mutex.sema_post();
  }

  /**
   * Writes to this database.
   */
  public void write(){
	System.out.println("test 1");
	mutex.sema_wait();
		System.out.println("test 2");
	writers++;
	System.out.println("test 3");
	mutex.sema_post();
    writer_lock.sema_wait();
    number++;
    System.err.println("\t\tWriter " + Thread.currentThread().getName() + " starts writing: "+number);

    final int DELAY = 5000;
    try{
      Thread.sleep((int) (Math.random() * DELAY));
    }catch (InterruptedException e) {}

    System.err.println("\t\tWriter " + Thread.currentThread().getName() + " stops writing.");
System.out.println("test 4");
	mutex.sema_wait();
System.out.println("test 5");
	writers--;
System.out.println("test 6");
	reader_lock.sema_post();
System.out.println("test 7");
	mutex.sema_post();
System.out.println("test 8");
    writer_lock.sema_post();
  }
}

class Reader extends Thread{
  private Database database;

  /**
   * Creates a Reader for the specified database.
   * @param database database from which to be read.
   * @param name thread name.
   */
  public Reader(Database database, String name){
    super(name);
    this.database = database;
  }

  /**
   * Reads.
   */
  public void run(){
    while (true){
      final int DELAY = 5000;
      try{
        Thread.sleep((int) (Math.random() * DELAY));
      }catch (InterruptedException e) {}
      database.read();
    }
  }
}

/**
  This class represents a writer.
*/
class Writer extends Thread{
  private Database database;

  /**
   * Creates a Writer for the specified database.
   * @param database database to which to write.
   * @param name thread name.
   */
  public Writer(Database database, String name){
    super(name);
    this.database = database;
  }

  /**
   * Writes.
   */
  public void run(){
    while (true){
      final int DELAY = 5000;
      try{
        Thread.sleep((int) (Math.random() * DELAY));
      }catch (InterruptedException e) {}
      database.write();
    }
  }
}

/**
  This app creates a specified number of readers and
  writers and starts them.
*/
public class Main{
  /**
    Creates the specified number of readers and writers and starts them.
    @param args[0] The number of readers.
    @param args[1] The number of writers.
  */
  public static void main(String[] args){
    if (args.length < 2){
      System.out.println("Usage: java ReaderWriter <number of readers> <number of writers>");
    }else{
      final int READERS = Integer.parseInt(args[0]);
      final int WRITERS = Integer.parseInt(args[1]);
      Database database = new Database();
      for (int i = 0; i < READERS; i++){
        new Reader(database, ""+i).start();
      }
      for (int i = 0; i < WRITERS; i++){
        new Writer(database, ""+i).start();
      }
    }
  }
}
