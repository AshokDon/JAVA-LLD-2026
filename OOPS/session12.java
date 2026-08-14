// ============================================================================
//  SESSION 1.2   Intro to OOP . Classes , Objects , Encapsulation
//
//  NOTE -> this file is a HANDOUT , not a program
//  it contains the same Driver class 6 times , growing one idea at a time ,
//  so your IDE will show red marks . that is expected . do not try to run it
//  the finished code that actually runs is in  uber/Driver.java + uber/Client.java
// ============================================================================


//---------------------the story---------------------

//monday morning . the manager drops one line on your desk
//
//      "build uber"
//
//that is the whole requirement . nothing else
//you cannot type  class  yet . you do not know WHAT to write a class for
//so the real first question is -> WHAT ARE THE THINGS IN THIS SYSTEM ?

//open the uber app in your head and say the nouns out loud
//      someone comes and picks you up          ->  Driver
//      you are the one booking                 ->  Customer
//      he arrives in something                 ->  Vehicle
//      at the end money moves                  ->  Payment
//      the journey itself                      ->  Trip

//five nouns . these are the ENTITIES . every real design starts here .
//not with code . with nouns


//---------------------ask your self---------------------

//Q1 uber is huge . maps , pricing , fraud , invoices . why only FIVE words ?
//   because we need the few things that matter and nothing else
//   that is ABSTRACTION -> a complex system shown in a few working steps
//   you use it daily ->  login -> dashboard -> book a ride
//   three steps . behind them are 400 servers you never think about

//Q2 take one entity . Driver . what does the system need to KNOW about him ,
//   and what does he DO ?
//      knows -> name , rating , driver id , is he online
//      does  -> accept a ride , go offline
//   should those two lists live in two places in the codebase , or in ONE ?

//Q3 a driver in the real world is a person . a driver in our program is ?
//   a real world entity living in the memory of our program . an OBJECT
//   the plan used to build it is a CLASS

//Q4 later we will write   Driver d2 = d1;
//   does that give me a SECOND driver ?
//   hold that question . it comes back at idea 3


//      Encapsulation  ]
//      Inheritance    ]  the 3 PILLARS  . the tools
//      Polymorphism   ]
//      Abstraction       the PRINCIPLE  . the result you get from them
//people say 4 pillars . it is really 3 tools and 1 outcome

//we build ONE entity properly . Driver
//Customer , Vehicle , Payment , Trip are built exactly the same way


//---------------------idea 1 : class and object---------------------
//make a package  uber  . inside it make 2 classes
//      Driver.java   the entity . what a driver IS
//      Client.java   the runner . has main() . we test from here

//let us create a Driver class in Driver.java file , now
//think of what we need for a driver (driver id , name , rating , isOnline)

public class Driver {
    int driverId;
    String name;
    double rating;
    boolean isOnline;
}

//now open client class and create an object
public class Client {
    public static void main(String[] args) {
        Driver d1 = new Driver();
        d1.driverId = 1;
        d1.name = "Abc";
        d1.rating = 5.0;
        d1.isOnline = true;
    }
}

//variables inside a class = MEMBER VARIABLES (fields) . their values = STATE
//Driver d1 = new Driver()
//  Driver = the type , d1 = the reference variable , new = builds the real object
//the problem is how many lines you need to create 100 drivers -> 500 lines
//and a driver can only hold data . it cannot DO anything


//---------------------next idea---------------------
//a driver accepts rides and goes offline . put the behaviour inside the class

public class Driver {
    int driverId;
    String name;
    double rating;
    boolean isOnline;

    public void acceptRide(String rideId) {
        System.out.println(name + " accepted " + rideId);
    }

    public void goOffline() {
        isOnline = false;
    }
}

//client
public class Client {
    public static void main(String[] args) {
        Driver d1 = new Driver();
        d1.driverId = 1;
        d1.name = "Abc";
        d1.rating = 5.0;
        d1.isOnline = true;

        d1.acceptRide("RIDE-9001");
    }
}

//functions inside a class = MEMBER METHODS
//we did not pass the name . the object already knows its own name
//data + the code that uses that data , in one place . that is a class
//still 5 lines per driver . not fixed yet


//---------------------next idea---------------------
//this is Q4 . before fixing that , answer -> Driver d2 = d1;  is that a 2nd driver ?
//driver class no change . only client

public class Client {
    public static void main(String[] args) {
        Driver d1 = new Driver();
        d1.name = "Abc";

        Driver d2 = d1;              //no  new  here
        d2.name = "Xyz";

        System.out.println(d1.name);        //Xyz
        System.out.println(d1 == d2);       //true

        Driver d3 = new Driver();
        System.out.println(d1 == d3);       //false
    }
}

//we changed d2 and d1 changed too . there is only ONE driver
//d1 is NOT the object . d1 holds the ADDRESS of the object
//stack holds d1 d2 d3 . heap holds the objects made with  new
//d2 = d1 copies the ADDRESS . two remote controls , one television
//==  compares address .  .equals  compares content
//to get a 2nd driver you must write  new  again


//---------------------next idea---------------------
//same rule with a twist . String pool . client only

public class Client {
    public static void main(String[] args) {
        String s1 = "ABC";                  //goes in the string pool
        String s2 = "ABC";                  //finds it , points at the SAME one
        String s3 = new String("ABC");      //new forces a fresh object

        System.out.println(s1 == s2);       //true
        System.out.println(s1 == s3);       //false
        System.out.println(s1.equals(s3));  //true
    }
}

//never compare strings with  ==  . always use  .equals()
//it passes in testing because test data hits the pool ,
//then fails in production where the string came from a database


//---------------------next idea---------------------
//back to the 5 lines . and something worse
//after  new Driver()  the object EXISTS but has no name , rating 0.0 , isOnline false
//
//a real bug -> a driver called uber support
//   "i am logged in 3 days , not one ride"
//   someone forgot the  isOnline = true  line
//   20 places create a Driver . 19 set all 4 fields . one does not . java says nothing
//
//whose job is it to make sure a driver is complete ? not the 20 callers .
//it should be the Driver class itself
//
//CONSTRUCTOR -> first method that runs on  new
//   name is EXACTLY the class name , NO return type , only  new  can call it

public class Driver {
    int driverId;
    String name;
    double rating;
    boolean isOnline;

    public Driver(int driverId, String name, double rating, boolean isOnline) {
        this.driverId = driverId;
        this.name = name;
        this.rating = rating;
        this.isOnline = isOnline;
    }

    public void acceptRide(String rideId) {
        System.out.println(name + " accepted " + rideId);
    }

    public void goOffline() {
        isOnline = false;
    }
}

//client
public class Client {
    public static void main(String[] args) {
        Driver d1 = new Driver(1, "Abc", 5.0, true);
        Driver d2 = new Driver(2, "Xyz", 4.6, true);

        d1.acceptRide("RIDE-9001");
    }
}

//5 lines became 1 . 100 drivers = 100 lines , not 500
//and you can no longer forget a field . the compiler asks for all four
//  new Driver(3, "Pqr", 4.9)  ->  error: required: int,String,double,boolean
//                                        found:    int,String,double
//the bug moved from a support call 3 weeks later to a red line in your editor now

//TRAP -> now try  new Driver()
//  error: required: int,String,double,boolean / found: no arguments
//java gives a free no-args constructor ONLY if you wrote none
//write even one of your own and java stops giving it
//EITHER the programmer writes the constructors , OR java does . NEVER BOTH
//want it back ? write  public Driver() { }  yourself
//but think first . that means "i allow half built drivers"


//---------------------next idea---------------------
//we want to rename a driver . the setter looks obvious but is a silent bug

public class Driver {
    int driverId;
    String name;
    double rating;
    boolean isOnline;

    public Driver(int driverId, String name, double rating, boolean isOnline) {
        this.driverId = driverId;
        this.name = name;
        this.rating = rating;
        this.isOnline = isOnline;
    }

    public void setNameWrong(String name) {
        name = name;                //both are the parameter . field never touched
    }

    public void setName(String name) {
        this.name = name;           //field = parameter
    }

    public void acceptRide(String rideId) {
        System.out.println(name + " accepted " + rideId);
    }
}

//client
public class Client {
    public static void main(String[] args) {
        Driver d1 = new Driver(1, "Abc", 5.0, true);

        d1.setNameWrong("Xyz");
        System.out.println(d1.name);        //Abc   unchanged

        d1.setName("Xyz");
        System.out.println(d1.name);        //Xyz   correct
    }
}

//nothing failed . it simply did not work . javac does not even warn
//this = the object this method was called on
//always write  this.field = parameter  in a constructor or a setter
//notice the constructor above already did this . that was not decoration


//---------------------next idea---------------------
//name and rating belong to ONE driver . but "how many drivers in total"
//is not Ravi's property and not Sita's property . it belongs to the CLASS
//that is  static  . one copy shared by everyone

public class Driver {
    int driverId;
    String name;
    double rating;
    boolean isOnline;
    static int totalDrivers;

    static {                        //runs ONCE when the class loads
        totalDrivers = 0;
    }

    public Driver(int driverId, String name, double rating, boolean isOnline) {
        this.driverId = driverId;
        this.name = name;
        this.rating = rating;
        this.isOnline = isOnline;
        totalDrivers++;
    }

    public static void register() {
        System.out.println("ran on the class , no object needed");
        //System.out.println(name);  ->  error: non-static variable name
        //                                cannot be referenced from a static context
    }

    public void setName(String name) {
        this.name = name;
    }
}

//client
public class Client {
    public static void main(String[] args) {
        System.out.println(Driver.totalDrivers);    //0

        Driver d1 = new Driver(1, "Abc", 5.0, true);
        Driver d2 = new Driver(2, "Xyz", 4.6, true);

        System.out.println(Driver.totalDrivers);    //2
        System.out.println(d1.totalDrivers);        //2   same copy
        System.out.println(d2.totalDrivers);        //2   same copy

        Driver.register();
    }
}

//the static block runs before any object exists
//d1 and d2 both say 2 . there is no "d1's copy" . there is ONE copy
//write  Driver.totalDrivers  not  d1.totalDrivers . both compile ,
//but the first one says what is really happening
//a static method cannot touch instance variables . whose name would it print ?
//that is also why  main  is static


//---------------------next idea---------------------
//JAVA IS ALWAYS PASS BY VALUE . there is no pass by reference
//for an object the value that gets copied is the ADDRESS
//driver class no change . only client

public class Client {
    public static void main(String[] args) {
        int number = 10;
        changePrimitive(number);
        System.out.println(number);         //10   unchanged

        Driver d1 = new Driver(1, "Abc", 5.0, true);
        changeObject(d1);
        System.out.println(d1.name);        //XYZ  changed
        System.out.println(d1 == null);     //false
    }

    static void changePrimitive(int n) {
        n = 999;
    }

    static void changeObject(Driver d) {
        d.name = "XYZ";                 //same object . the caller sees this
        d = null;                       //only our copy of the address
    }
}

//you can change the channel , you cannot swap my television


//---------------------next idea---------------------
//System.out.println(d1) prints  Driver@1b6d3586  . useless at 2 AM
//every class secretly extends Object , which already has toString() . override it
//add this at the end of Driver

    @Override
    public String toString() {
        return "Driver{id=" + driverId + ", name='" + name + "', rating=" + rating
                + ", online=" + isOnline + "}";
    }

//client
public class Client {
    public static void main(String[] args) {
        Driver d1 = new Driver(1, "Abc", 5.0, true);
        System.out.println(d1);
        //Driver{id=1, name='Abc', rating=5.0, online=true}
    }
}

//println(obj) calls obj.toString() for you . so does joining with  +
//every class you write gets a toString . the IDE writes it . alt+insert


//---------------------next idea---------------------
//ENCAPSULATION . pillar 1
//our fields are still wide open . watch
//      d1.rating = 9.9;      a rating of 9.9 out of 5 . accepted
//      d1.rating = -3;       negative . accepted
//      d1.driverId = 7;      the id changes after registration . accepted
//no error . the Driver class is supposed to own its data . right now anyone owns it
//
//capsule -> holds the data and the methods together ,
//           and protects the data from the outside
//
//make the fields private . a getter to read . a setter that VALIDATES before writing

public class Driver {
    private int driverId;
    private String name;
    private double rating;
    private boolean isOnline;
    static int totalDrivers;

    public Driver(int driverId, String name, double rating, boolean isOnline) {
        this.driverId = driverId;
        this.name = name;
        setRating(rating);          //even the constructor goes through the rule
        this.isOnline = isOnline;
        totalDrivers++;
    }

    public int getDriverId() {      //getter only . the id can never change
        return driverId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        if (rating < 0 || rating > 5) {
            throw new RuntimeException("Invalid rating value : " + rating);
        }
        this.rating = rating;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void goOffline() {
        this.isOnline = false;
    }

    @Override
    public String toString() {
        return "Driver{id=" + driverId + ", name='" + name + "', rating=" + rating
                + ", online=" + isOnline + "}";
    }
}

//client
public class Client {
    public static void main(String[] args) {
        Driver d1 = new Driver(1, "Abc", 5.0, true);

        //d1.rating = 9.9;    ->  error: rating has private access in Driver

        d1.setRating(4.9);
        System.out.println(d1.getRating());     //4.9
        System.out.println(d1);                 //Driver{id=1, name='Abc', rating=4.9, online=true}

        d1.setRating(9.9);      //RuntimeException: Invalid rating value : 9.9
    }
}

//now the class owns its data
//   driverId -> getter only . it can never change after registration
//   rating   -> setter with a rule . 9.9 is rejected at the door
//invalid state is now impossible , not just discouraged
//
//a getter+setter for every field is NOT encapsulation . that is public fields
//with extra typing . encapsulation is deciding WHO may change WHAT , under what rule


//---------------------homework---------------------
//1 build Customer the same way (customerId , name , phone , walletBalance)
//  walletBalance must never go negative
//2 build Vehicle with a static counter totalVehicles
//3 prove by code that  Customer c2 = c1  does not create a 2nd customer
//4 make a static method print an instance field . read the error . write it down
