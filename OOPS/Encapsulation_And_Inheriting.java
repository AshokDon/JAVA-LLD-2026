// ============================================================================
//  SESSION 1.3   Encapsulation  .  Inheritance
//
//  NOTE -> this file is a HANDOUT , not a program
//  the same class appears several times , growing one idea at a time ,
//  so your IDE will show red marks . that is expected . do not try to run it
//  the finished code that runs is in the  uber  package next to this file
// ============================================================================


//---------------------the story---------------------

//last session ended well . rating is private , the setter validates ,
//9.9 out of 5 is rejected at the door . encapsulation , pillar 1 . done

//six months later uber has 3 teams and the code is split into packages
//      uber            Driver , Trip , Fare , Route
//      uber.payments   invoices
//      uber.reports    dashboards

//monday morning the finance report shows a driver with id 7
//who never registered . nobody can explain it

//somebody in the uber package had written
//      d.driverId = 7;
//and it compiled . no error . no warning

//why ? because we wrote      int driverId;
//with NO modifier in front . that is called DEFAULT , and default means
//"anyone in the SAME PACKAGE can touch this"

//private stops other CLASSES . it does not stop the same package
//we thought the data was protected . it was only half protected


//---------------------ask your self---------------------

//Q1 we made rating private and left driverId with no modifier .
//   was that a decision , or an accident ?
//   in most codebases it is an accident . nobody chose "default" .
//   they just did not type anything

//Q2 how many levels of "who can touch this" does java actually give you ?
//   4 . and you must pick one for every field and every method you write

//Q3 a Car , a Bike and an Auto all have a vehicle number , a name ,
//   wheels and fuel . if we write 3 classes , we write those 4 fields
//   3 times . what happens when uber adds a 5th field next month ?

//Q4 the customer taps "book again" on last week's trip .
//   we copy the Fare object . the customer then edits the destination .
//   does last week's invoice change too ?
//   hold that question . it is idea 4 , and it is a real money bug


//---------------------idea 1 : access modifiers---------------------
//4 levels . from most closed to most open

//   private     ->  same CLASS only
//   default     ->  same class + same PACKAGE          (you write nothing)
//   protected   ->  same class + same package + SUBCLASS in another package
//   public      ->  everywhere

//                       same     same      subclass in    every
//                       class    package   other package  where
//      private            Y         N            N           N
//      default            Y         Y            N           N
//      protected          Y         Y            Y           N
//      public             Y         Y            Y           Y

//rule of thumb -> make every field private . open it up only when forced

package uber;

public class Driver {
    private int driverId;          //was default . that was the bug
    private String name;
    private double rating;
    private boolean isOnline;

    public Driver(int driverId, String name, double rating, boolean isOnline) {
        this.driverId = driverId;
        this.name = name;
        setRating(rating);
        this.isOnline = isOnline;
    }

    public int getDriverId() {     //read yes . write no . id never changes
        return driverId;
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
}

//client , in the same package
public class Client {
    public static void main(String[] args) {
        Driver d1 = new Driver(1, "Abc", 5.0, true);

        //d1.driverId = 7;    ->  error: driverId has private access in Driver
        //                        now it fails even inside the same package

        System.out.println(d1.getDriverId());   //1
    }
}

//the problem now -> look at how many constructors a real Driver needs .
//app signup gives a name . fleet import gives everything . a copy gives
//another driver . each constructor repeats the same assignment lines


//---------------------next idea---------------------
//CONSTRUCTOR CHAINING . one constructor calling another with  this()
//so the assignment logic is written ONCE

public class Driver {
    private int driverId;
    private String name;
    private double rating;
    private boolean isOnline;

    public Driver(int driverId, String name, double rating, boolean isOnline) {
        this.driverId = driverId;
        this.name = name;
        setRating(rating);
        this.isOnline = isOnline;
        System.out.println("main constructor ran");
    }

    //app signup . only a name . fill the rest with defaults
    public Driver(int driverId, String name) {
        this(driverId, name, 0.0, false);       //must be the FIRST line
        System.out.println("signup constructor ran");
    }

    //no-args . a placeholder driver
    public Driver() {
        this(0, "unknown");
        System.out.println("no-args constructor ran");
    }

    public String getName() {
        return name;
    }
}

//client
public class Client {
    public static void main(String[] args) {
        Driver d1 = new Driver();
    }
}

//prints
//      main constructor ran
//      signup constructor ran
//      no-args constructor ran

//read that order . the INNERMOST one finishes first .
//no-args called signup , signup called main , main ran , then they unwind

//rules
//   this(...) must be the FIRST line of the constructor
//   you cannot call this() twice in one constructor
//   two constructors calling each other = infinite recursion , compile error

//now the assignment and the validation live in ONE place .
//add a 5th field tomorrow and you edit one constructor , not three


//---------------------next idea---------------------
//COPY CONSTRUCTOR
//customer taps "book again" on last week's trip . we need a copy of the Fare
//a copy constructor takes the WHOLE object in , and builds a new one from it

//two new entities . a Route , and a Fare that HAS a Route

public class Route {
    String source;
    String destination;

    public Route(String source, String destination) {
        this.source = source;
        this.destination = destination;
    }
}

public class Fare {
    int fareId;
    double amount;
    Route route;

    public Fare(int fareId, double amount, Route route) {
        this.fareId = fareId;
        this.amount = amount;
        this.route = route;
    }

    //copy constructor
    public Fare(Fare other) {
        this.fareId = other.fareId;
        this.amount = other.amount;
        this.route = other.route;
    }
}

//client
public class Client {
    public static void main(String[] args) {
        Route r1 = new Route("Delhi", "Mumbai");
        Fare lastWeek = new Fare(1, 10000, r1);

        Fare bookAgain = new Fare(lastWeek);     //the copy

        System.out.println(bookAgain.amount);    //10000
    }
}

//looks correct . it is not . that is the next idea , and Q4


//---------------------next idea---------------------
//SHALLOW vs DEEP COPY . this is Q4 and it is a real money bug

//the customer edits the destination on the NEW trip
//driver class no change . only client

public class Client {
    public static void main(String[] args) {
        Route r1 = new Route("Delhi", "Mumbai");
        Fare lastWeek = new Fare(1, 10000, r1);

        Fare bookAgain = new Fare(lastWeek);
        bookAgain.route.destination = "Chennai";     //edit the NEW trip

        System.out.println(lastWeek.route.destination);   //Chennai
    }
}

//we edited the new trip . LAST WEEK'S INVOICE CHANGED
//the money is still 10000 but it now claims a different destination .
//accounting will never reconcile that

//why ? this line in the copy constructor
//      this.route = other.route;
//route is an OBJECT . we copied its ADDRESS , not the route
//both Fares point at the SAME Route . that is a SHALLOW COPY

//      lastWeek  ---\
//                    ---->  Route{Delhi, Mumbai}
//      bookAgain ---/

//a DEEP COPY makes a new Route too

public class Route {
    String source;
    String destination;

    public Route(String source, String destination) {
        this.source = source;
        this.destination = destination;
    }

    //Route needs its own copy constructor
    public Route(Route other) {
        this.source = other.source;
        this.destination = other.destination;
    }
}

public class Fare {
    int fareId;
    double amount;
    Route route;

    public Fare(int fareId, double amount, Route route) {
        this.fareId = fareId;
        this.amount = amount;
        this.route = route;
    }

    public Fare(Fare other) {
        this.fareId = other.fareId;
        this.amount = other.amount;
        this.route = new Route(other.route);     //NEW . a fresh Route
    }
}

//client
public class Client {
    public static void main(String[] args) {
        Route r1 = new Route("Delhi", "Mumbai");
        Fare lastWeek = new Fare(1, 10000, r1);

        Fare bookAgain = new Fare(lastWeek);
        bookAgain.route.destination = "Chennai";

        System.out.println(lastWeek.route.destination);   //Mumbai   safe now
    }
}

//      lastWeek  ---->  Route{Delhi, Mumbai}
//      bookAgain ---->  Route{Delhi, Chennai}      two separate objects

//what gets copied deeply for free , and what does not
//   primitives (int double boolean)   ->  always copied deeply . they ARE the value
//   String                            ->  safe , because String is IMMUTABLE .
//                                         you can never change it , only replace it
//   your own classes                  ->  you MUST copy them yourself

//the goal of a copy constructor is always a DEEP copy


//---------------------next idea---------------------
//INHERITANCE . pillar 2 . this is Q3
//uber has Car , Bike , Auto . all three have vehicleNo , name , wheels , fuel
//and all three can start and refill fuel . writing that 3 times is copy paste
//add a 5th field next month and you edit 3 files and forget one

//child = parent + extra
//      Car  IS-A  Vehicle
//that is the test . if you cannot say IS-A out loud , do not use inheritance

public class Vehicle {
    private int vehicleNo;
    String name;
    int wheels;
    int fuel;

    public Vehicle(int vehicleNo, String name, int wheels, int fuel) {
        this.vehicleNo = vehicleNo;
        this.name = name;
        this.wheels = wheels;
        this.fuel = fuel;
    }

    public void startVehicle() {
        System.out.println("vehicle is starting : " + name);
    }

    public void refillFuel(int fuel) {
        this.fuel += fuel;
    }

    public int getVehicleNo() {
        return vehicleNo;
    }
}

//Car gets everything from Vehicle , and adds its own
public class Car extends Vehicle {
    String musicSystem;

    public Car(int vehicleNo, String name, int wheels, int fuel, String musicSystem) {
        super(vehicleNo, name, wheels, fuel);       //parent first
        this.musicSystem = musicSystem;
    }

    public void turnOnAc() {
        System.out.println("AC on in vehicle " + getVehicleNo());
    }
}

public class Bike extends Vehicle {
    public Bike(int vehicleNo, String name, int fuel) {
        super(vehicleNo, name, 2, fuel);
    }
}

//client
public class Client {
    public static void main(String[] args) {
        Car c = new Car(1, "Swift", 4, 40, "Sony");
        c.startVehicle();       //inherited from Vehicle
        c.turnOnAc();           //its own

        Bike b = new Bike(2, "Splendor", 10);
        b.startVehicle();       //same inherited method
    }
}

//code reuse . the 4 fields and startVehicle are written ONCE
//child classes are also called SUBCLASSES
//inheritance is an IS-A relationship


//---------------------next idea---------------------
//super()  .  who builds the parent part of the object ?

//when you make a Car , java must build the Vehicle part first .
//it cannot set musicSystem on a half built vehicle

//the FIRST line of every child constructor is a call to a parent constructor .
//if you do not write it , java inserts  super();  for you . the no-args one

//that is why this fails

//      public class Vehicle {
//          public Vehicle(int vehicleNo, String name, int wheels, int fuel) { ... }
//      }
//      public class Car extends Vehicle {
//          public Car(String musicSystem) {
//              this.musicSystem = musicSystem;      //no super(...) written
//          }
//      }
//
//      error: constructor Vehicle in class Vehicle cannot be applied to given types;
//        required: int,String,int,int
//        found:    no arguments

//java tried to insert super() . Vehicle has no no-args constructor . dead

//order of execution . add prints and watch

public class Vehicle {
    public Vehicle(int vehicleNo, String name, int wheels, int fuel) {
        System.out.println("Vehicle constructor ran");
    }
}

public class Car extends Vehicle {
    public Car(int vehicleNo, String name, int wheels, int fuel, String musicSystem) {
        super(vehicleNo, name, wheels, fuel);
        System.out.println("Car constructor ran");
    }
}

//client
public class Client {
    public static void main(String[] args) {
        Car c = new Car(1, "Swift", 4, 40, "Sony");
    }
}

//prints
//      Vehicle constructor ran
//      Car constructor ran

//the parent is always built first . always . top of the family tree downwards

//   this(...)   calls another constructor in the SAME class
//   super(...)  calls a constructor in the PARENT class
//   both must be the FIRST line . so you can use only one of them


//---------------------next idea---------------------
//PROTECTED  .  the modifier that only makes sense once you have inheritance

//Vehicle is in package  uber
//the reports team writes a Car in package  uber.reports  and extends Vehicle
//can Car see the parent's fields ?

package uber;

public class Vehicle {
    private int vehicleNo;      //nobody outside Vehicle
    String name;                //default . same package only
    protected int wheels;       //subclass , even in another package
    public int fuel;            //everyone

    public Vehicle(int vehicleNo, String name, int wheels, int fuel) {
        this.vehicleNo = vehicleNo;
        this.name = name;
        this.wheels = wheels;
        this.fuel = fuel;
    }
}

//a different package
package uber.reports;

import uber.Vehicle;

public class Car extends Vehicle {

    public Car() {
        super(1, "Swift", 4, 40);
    }

    public void show() {
        System.out.println(wheels);      //protected . OK , we are a subclass
        System.out.println(fuel);        //public . OK

        //System.out.println(name);
        //  error: name is not public in Vehicle;
        //         cannot be accessed from outside package

        //System.out.println(vehicleNo);
        //  error: vehicleNo has private access in Vehicle
    }
}

//protected  =  default  +  subclasses in other packages
//use it when a child genuinely needs the field . not by habit .
//a protected field is still a field the whole world of subclasses can edit


//---------------------next idea---------------------
//FINAL  .  immutability . three different meanings

//   final VARIABLE  ->  cannot be reassigned
//   final METHOD    ->  cannot be overridden by a child
//   final CLASS     ->  cannot be inherited at all

//why this matters . uber shows the customer a fare of 250 .
//somewhere a subclass overrides getAmount() and adds 100 .
//the customer is charged 350 and the screen still says 250

public final class Fare {              //nobody can extend Fare
    private final int amount;          //set once , in the constructor , never again

    public Fare(int amount) {
        this.amount = amount;
    }

    public final int getAmount() {     //nobody can change what this returns
        return amount;
    }
}

//client
public class Client {
    public static void main(String[] args) {
        Fare f = new Fare(250);
        System.out.println(f.getAmount());     //250

        //f.amount = 500;
        //  error: cannot assign a value to final variable amount
    }
}

//and the attack we blocked

//      public class MutableFare extends Fare {
//          public int getAmount() { return super.getAmount() + 100; }
//      }
//
//      error: cannot inherit from final Fare
//      error: getAmount() in MutableFare cannot override getAmount() in Fare
//             overridden method is final

//final on a REFERENCE variable is the one people get wrong

//      final Route r = new Route("Delhi", "Mumbai");
//      r.destination = "Pune";        //ALLOWED . the object changed
//      r = new Route("A","B");        //error: cannot assign a value to final variable r

//final locks the REMOTE CONTROL , not the television
//to lock the object too , make its fields final as well


//---------------------what we fixed today---------------------
//   driverId was default , anyone in the package could edit it   ->  private
//   4 constructors repeated the same lines                       ->  this(...)
//   "book again" needed a copy                                   ->  copy constructor
//   the copy shared the Route and changed last week's invoice    ->  deep copy
//   Car Bike Auto repeated 4 fields and 2 methods                ->  inheritance
//   the parent part of the object must be built first            ->  super(...)
//   a subclass in another package could not see what it needed   ->  protected
//   a subclass could override the fare and overcharge            ->  final


//---------------------summary---------------------
// 1  4 access modifiers . private , default , protected , public
// 2  writing nothing = default = the whole package can touch it
// 3  make every field private . open it only when forced
// 4  this(...) chains constructors . must be the first line
// 5  the innermost constructor finishes first
// 6  a copy constructor takes the whole object and builds a new one
// 7  copying an object field copies the ADDRESS . that is a shallow copy
// 8  a shallow copy means two objects share one child . editing one edits both
// 9  primitives and String are safe . your own classes you must copy yourself
//10  inheritance = child is parent + extra . say IS-A out loud first
//11  the parent constructor always runs first . super(...) or java inserts super()
//12  no no-args parent constructor + no super(...) written = compile error
//13  protected = default + subclasses in other packages
//14  final variable cannot be reassigned , method cannot be overridden ,
//    class cannot be inherited
//15  final on a reference locks the remote , not the television


//---------------------homework---------------------
//1 give Trip a copy constructor . Trip HAS-A Driver and HAS-A Route .
//  make it a proper deep copy and prove it with a print
//2 make Auto extend Vehicle . 3 wheels . it should not have turnOnAc()
//3 put Driver in package uber and a DriverReport in uber.reports .
//  find the smallest modifier that lets DriverReport read the rating
//4 try to extend a final class and read the error . write it down
//5 make a final Route and change its destination . explain why java allowed it
