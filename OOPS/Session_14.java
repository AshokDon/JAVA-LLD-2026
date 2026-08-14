// ============================================================================
//  SESSION 1.4   Polymorphism  .  Abstraction
//
//  NOTE -> this file is a HANDOUT , not a program
//  the same class appears several times , growing one idea at a time ,
//  so your IDE will show red marks . that is expected . do not try to run it
//  the finished code that runs is in the  uber  package next to this file
// ============================================================================


//---------------------the story---------------------

//uber now has Car and Bike . the pricing team wrote this

//      public double calculateFare(Object vehicle, int km) {
//          if (vehicle instanceof Car) {
//              return 10.0 * km;
//          } else if (vehicle instanceof Bike) {
//              return 5.0 * km;
//          }
//          return 0;
//      }

//it works . it is readable . nothing is wrong with this code . for 2 vehicles

//then uber launches AUTO in hyderabad
//a developer adds  else if (vehicle instanceof Auto) return 7.0*km;

//but that same if-else chain also lives in
//      the fare estimator on the booking screen
//      the invoice generator
//      the driver earnings report
//      the surge pricing engine
//      the refund calculator

//he updated 4 of the 5 . the refund calculator still had no Auto branch
//it returned 0 . every cancelled auto ride refunded ZERO rupees
//for 9 days . no error . the chain just fell through to  return 0


//---------------------ask your self---------------------

//Q1 who should know how much a Car costs per km ?
//   the pricing team , in an if-else ? or the Car class itself ?

//Q2 when we add a 6th vehicle type , how many files should change ?
//   right now 5 . it should be 1 . the new class

//Q3 the if-else asks "WHAT ARE YOU" and then decides .
//   could we instead just say "calculate your fare" and let the object decide ?
//   that would mean one method name behaving differently for each object

//Q4 does a plain  Vehicle  exist in the real world ?
//   you cannot book "a vehicle" . you book a car , a bike , an auto
//   Vehicle is a CONCEPT . so should  new Vehicle()  even be allowed ?
//   hold that question . it is idea 5


//polymorphism  ->  poly = many , morph = forms . many forms
//one person . Kunal IS an instructor , a son , an employee , a friend .
//same person , different form depending on WHO is looking . context sensitive

//two types
//      compile time  ->  method overloading   . decided by the compiler
//      runtime       ->  method overriding    . decided while running


//---------------------idea 1 : compile time polymorphism---------------------
//METHOD OVERLOADING . same method name , different parameter list , same class
//the compiler picks which one , by looking at what you passed

public class FareCalculator {

    public double calculateFare(int km) {
        return 10.0 * km;
    }

    public double calculateFare(int km, double surge) {
        return 10.0 * km * surge;
    }

    public double calculateFare(int km, double surge, int waitingMinutes) {
        return (10.0 * km * surge) + (2.0 * waitingMinutes);
    }
}

//client
public class Client {
    public static void main(String[] args) {
        FareCalculator f = new FareCalculator();

        System.out.println(f.calculateFare(10));            //100.0
        System.out.println(f.calculateFare(10, 1.5));       //150.0
        System.out.println(f.calculateFare(10, 1.5, 5));    //160.0
    }
}

//the SIGNATURE is what makes them different . signature =
//      the data type of the parameters
//      the number of parameters
//      the order of the parameters

//parameter NAMES are not part of the signature
//      int add(int a, int b)
//      int add(int x, int y)
//      error: method add(int,int) is already defined in class Calc

//RETURN TYPE is not part of the signature either
//      int    add(int a, int b, int c)
//      double add(int a, int b, int c)
//      error: method add(int,int,int) is already defined in class Calc

//order DOES count . these two are legal together
//      double add(int a, double b)
//      double add(double a, int b)
//but then  add(3, 5)  is ambiguous . both need one widening . compile error

//if there is no exact match java WIDENS
//      int  ->  long  ->  float  ->  double
//      show('x')  with no char version  ->  the int version runs


//---------------------next idea---------------------
//RUNTIME POLYMORPHISM . method OVERRIDING . this kills the if-else chain
//parent defines the method . every child redefines it for itself

public class Vehicle {
    String name;

    public Vehicle(String name) {
        this.name = name;
    }

    public double calculateFare(int km) {
        return 8.0 * km;
    }

    public void startEngine() {
        System.out.println("engine started for " + name);
    }
}

public class Car extends Vehicle {
    public Car() { super("Car"); }

    @Override
    public double calculateFare(int km) {       //Car decides its own price
        return 10.0 * km;
    }
}

public class Bike extends Vehicle {
    public Bike() { super("Bike"); }

    @Override
    public double calculateFare(int km) {
        return 5.0 * km;
    }
}

public class Auto extends Vehicle {
    public Auto() { super("Auto"); }

    @Override
    public double calculateFare(int km) {
        return 7.0 * km;
    }
}

//now the pricing service . no if . no else . no instanceof
public class PricingService {
    List<Vehicle> vehicles = new ArrayList<>();

    public void printAllFares(int km) {
        for (Vehicle v : vehicles) {
            System.out.println(v.name + " : " + v.calculateFare(km));
        }
    }
}

//client
public class Client {
    public static void main(String[] args) {
        PricingService ps = new PricingService();
        ps.vehicles.add(new Car());
        ps.vehicles.add(new Bike());
        ps.vehicles.add(new Auto());

        ps.printAllFares(10);
        //Car  : 100.0
        //Bike : 50.0
        //Auto : 70.0
    }
}

//ONE line  ->  v.calculateFare(km)
//three different answers . the OBJECT decided , not the caller

//that is Q1 , Q2 and Q3 answered
//add a 6th vehicle tomorrow -> write ONE new class . PricingService never changes
//the refund calculator bug becomes impossible . there is no chain to forget


//---------------------next idea---------------------
//UPCASTING . how does a List<Vehicle> hold a Car ?

//      Vehicle v = new Vehicle();      allowed  (for now . see idea 5)
//      Car     c = new Car();          allowed
//      Vehicle v = new Car();          ALLOWED . a Car IS-A Vehicle
//      Car     c = new Vehicle();      NOT allowed . a Vehicle is not a Car

//      Parent p = new Child();         allowed
//      Child  c = new Parent();        not allowed

//think of it as a job title . a Car can be called a Vehicle .
//not every Vehicle can be called a Car

//but there is a price

public class Car extends Vehicle {
    public Car() { super("Car"); }

    @Override
    public double calculateFare(int km) { return 10.0 * km; }

    public void turnOnAc() {                    //Car only . not in Vehicle
        System.out.println("AC on");
    }
}

//client
public class Client {
    public static void main(String[] args) {
        Vehicle v = new Car();

        System.out.println(v.calculateFare(10));    //100.0  Car's version runs

        //v.turnOnAc();
        //  error: cannot find symbol . method turnOnAc()

        //the REFERENCE type decides what you are ALLOWED to call
        //the OBJECT type decides WHICH version actually runs

        ((Car) v).turnOnAc();       //downcast . works , but you must be sure
    }
}

//if you find yourself downcasting a lot , your design is wrong .
//the method probably belongs on Vehicle


//---------------------the trap---------------------
//METHODS are polymorphic . VARIABLES ARE NOT

//      variables  ->  resolved by the REFERENCE type
//      methods    ->  resolved by the OBJECT type

public class Vehicle {
    String name = "Vehicle";
    public String who() { return "Vehicle"; }
    public static String hello() { return "Vehicle.hello"; }
}

public class Car extends Vehicle {
    String name = "Car";
    public String who() { return "Car"; }
    public static String hello() { return "Car.hello"; }
}

//client
public class Client {
    public static void main(String[] args) {
        Vehicle v = new Car();

        System.out.println(v.name);        //Vehicle   <- the FIELD came from the reference
        System.out.println(v.who());       //Car       <- the METHOD came from the object
        System.out.println(Vehicle.hello());//Vehicle.hello
    }
}

//that is called FIELD HIDING and it is a bug factory .
//never redeclare a field that already exists in the parent

//static methods are also NOT overridden . they are HIDDEN .
//a static method belongs to the class , and the reference type picks the class .
//never call a static method through an object reference


//---------------------next idea---------------------
//THE RULES FOR OVERRIDING . 3 things the compiler checks

//1  the SIGNATURE must be exactly the same . change it and you have
//   accidentally overloaded , not overridden . @Override catches this

//2  the RETURN TYPE must be the same , or a SUBTYPE (covariant return)
//      Vehicle getVehicle()     in the parent
//      Car     getVehicle()     in the child     ->  allowed
//   because a Car IS-A Vehicle , so every caller still gets what it expected

//3  the ACCESS MODIFIER can be WIDENED but never NARROWED
//      public  in parent  ->  public in child           ok
//      public  in parent  ->  private in child          ERROR
//
//      error: getName() in Car cannot override getName() in Vehicle
//             attempting to assign weaker access privileges; was public
//
//   why ? someone holding a  Vehicle  reference is promised getName() works .
//   if Car made it private , that promise would break at runtime

//   the widening order is   private  ->  default  ->  protected  ->  public

//also
//   a  final  method cannot be overridden at all . that was session 1.3
//   a  static  method cannot be overridden . it is hidden
//   a  private method is not visible to the child , so it is not overriding


//---------------------next idea---------------------
//ABSTRACT CLASS . this is Q4

//      Vehicle v = new Vehicle();

//what is that object ? not a car , not a bike , not an auto . just "a vehicle"
//it has no real world existence . and look at what it returns

//      public double calculateFare(int km) { return 8.0 * km; }

//8.0 per km . where did that number come from ? nowhere . we invented it
//so a plain Vehicle would silently charge a made up price

//ABSTRACT CLASS -> when we want the class only as a CONCEPT ,
//                  but its real world object will never exist

//an abstract class CAN have both
//      abstract methods      no body . every child MUST implement them
//      normal methods        with a body . shared by every child

public abstract class Vehicle {
    String name;

    public Vehicle(String name) {                //abstract classes DO have constructors
        this.name = name;                        //the child calls it with super(...)
    }

    public abstract double calculateFare(int km);   //no body . no invented price

    public void startEngine() {                     //shared . written once
        System.out.println("engine started for " + name);
    }
}

public class Car extends Vehicle {
    public Car() { super("Car"); }

    @Override
    public double calculateFare(int km) { return 10.0 * km; }
}

//client
public class Client {
    public static void main(String[] args) {
        //Vehicle v = new Vehicle("x");
        //  error: Vehicle is abstract; cannot be instantiated

        Vehicle v = new Car();      //still allowed . this is the point
        v.startEngine();
        System.out.println(v.calculateFare(10));    //100.0
    }
}

//the made up 8.0 per km is now impossible
//and a child that forgets calculateFare will not compile

//NOTE -> if a child does not implement every abstract method ,
//        the child must itself be declared abstract


//---------------------next idea---------------------
//INTERFACE . a contract . "you MUST be able to do this"

//uber has HumanUser -> Customer and Driver . both have name , email , login()
//that is IS-A . inheritance handles it

//but now uber launches self driving cars . a RoboticDriver
//it can accept a ride . it is NOT a human . it has no email , no login

//      Driver  IS-A   User        ->  extends
//      Driver  CAN    Drive       ->  implements

//an interface holds only abstract methods . no state . no constructor

public interface Drivable {
    void acceptRide(String rideId);
    void completeRide(String rideId);
}

public abstract class HumanUser {
    String name;
    String email;

    public void login() {
        System.out.println(name + " logged in");
    }
}

public class Customer extends HumanUser {
    //a customer is a user but cannot drive
}

public class Driver extends HumanUser implements Drivable {
    public void acceptRide(String rideId) {
        System.out.println(name + " accepted " + rideId);
    }
    public void completeRide(String rideId) {
        System.out.println(name + " completed " + rideId);
    }
}

public class RoboticDriver implements Drivable {     //not a HumanUser at all
    public void acceptRide(String rideId) {
        System.out.println("robot accepted " + rideId);
    }
    public void completeRide(String rideId) {
        System.out.println("robot completed " + rideId);
    }
}

//client
public class Client {
    public static void main(String[] args) {
        List<Drivable> fleet = new ArrayList<>();
        fleet.add(new Driver());
        fleet.add(new RoboticDriver());

        for (Drivable d : fleet) {
            d.acceptRide("RIDE-1");     //one call . a human or a robot answers
        }
    }
}

//a class can extend ONE class but implement MANY interfaces
//interfaces let you enforce a contract without forcing a family tree

//   abstract class  ->  IS-A . shares state and code . one parent only
//   interface       ->  CAN-DO . a capability . as many as you like


//---------------------next idea---------------------
//THE PENGUIN PROBLEM . why we do not put everything in the parent

//birds . they eat , they sleep , they fly . so

//      public abstract class Bird {
//          public void eat() { ... }
//          public void fly() { ... }        <- looks harmless
//      }
//      public class Penguin extends Bird {
//          public void fly() { throw new RuntimeException("penguins cannot fly"); }
//      }

//a method that exists only to throw an exception is your design telling you
//it is unhappy . Penguin was forced to inherit something that is not true

//split the capability out

public abstract class Bird {
    public void eat() { System.out.println("bird is eating"); }
    public void sleep() { System.out.println("bird is sleeping"); }
}

public interface Flyable {
    void fly();
}

public class Pigeon extends Bird implements Flyable {
    public void fly() { System.out.println("pigeon is flying"); }
}

public class Penguin extends Bird {          //no Flyable . and that is correct
    public void swim() { System.out.println("penguin is swimming"); }
}

//now it is impossible to ask a Penguin to fly . the compiler stops you
//same idea in uber -> CashOnDelivery can pay , but it cannot refund online


//---------------------next idea---------------------
//COMPOSITION vs INHERITANCE

//      inheritance  ->  IS-A     Car IS-A Vehicle
//      composition  ->  HAS-A    TripService HAS-A PaymentGateway

//an Elephant is not a Cage . a Cage HAS an Elephant . do not extend Cage

//TripService needs to take money . if we write

//      public class TripService extends RazorPayGateway { ... }

//then switching to Paytm means changing the parent of TripService .
//and TripService is not a payment gateway . it USES one

public interface PaymentGateway {
    void payMoney(double amount);
}

public class RazorPayGateway implements PaymentGateway {
    public void payMoney(double amount) {
        System.out.println("paid " + amount + " via razorpay");
    }
}

public class PaytmGateway implements PaymentGateway {
    public void payMoney(double amount) {
        System.out.println("paid " + amount + " via paytm");
    }
}

public class TripService {
    private PaymentGateway gateway;              //HAS-A

    public TripService(PaymentGateway gateway) { //handed in from outside
        this.gateway = gateway;
    }

    public void completeTrip(double amount) {
        System.out.println("trip completed");
        gateway.payMoney(amount);
    }
}

//client
public class Client {
    public static void main(String[] args) {
        TripService t1 = new TripService(new RazorPayGateway());
        t1.completeTrip(250);

        TripService t2 = new TripService(new PaytmGateway());   //swap . one word
        t2.completeTrip(250);
    }
}

//TripService never mentions razorpay or paytm . it only knows the interface
//add a third gateway tomorrow -> TripService does not change at all

//rule of thumb -> prefer composition . reach for inheritance only when
//                 you can honestly say IS-A out loud


//---------------------next idea---------------------
//THE Object CLASS . every class in java secretly extends it
//it gives you 3 methods you will override again and again

//      toString()   we did this in 1.2
//      equals()     are these two objects the same ?
//      hashCode()   a number used by HashMap and HashSet to find a bucket

//by default
//      ==        compares ADDRESSES
//      equals()  ALSO compares addresses . the default is useless
//      hashCode() is derived from the address

//uber wants two Driver objects with the same driverId treated as the same driver

public class Driver {
    int driverId;
    String name;

    public Driver(int driverId, String name) {
        this.driverId = driverId;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Driver other = (Driver) o;
        return this.driverId == other.driverId;
    }

    //NO hashCode yet . watch what happens
}

//client
public class Client {
    public static void main(String[] args) {
        Driver d1 = new Driver(1, "Ravi");
        Driver d2 = new Driver(1, "Ravi Kumar");    //same id . same driver

        System.out.println(d1 == d2);          //false . different objects
        System.out.println(d1.equals(d2));     //true  . our rule says same

        Map<Driver, String> ridesToday = new HashMap<>();
        ridesToday.put(d1, "RIDE-9001");

        System.out.println(ridesToday.get(d2));   //null
    }
}

//equals says TRUE . the map says NULL . both at the same time

//why ? a HashMap first calls hashCode() to pick a bucket ,
//and only then calls equals() inside that bucket
//d1 and d2 have different default hashCodes , so it looked in the WRONG bucket
//and never even got to equals

//THE CONTRACT
//      if a.equals(b) is true , then a.hashCode() MUST equal b.hashCode()
//      always override equals and hashCode TOGETHER . never one alone

public class Driver {
    int driverId;
    String name;

    public Driver(int driverId, String name) {
        this.driverId = driverId;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        return this.driverId == ((Driver) o).driverId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(driverId);      //SAME field as equals
    }

    @Override
    public String toString() {
        return "Driver{" + driverId + ", " + name + "}";
    }
}

//now  ridesToday.get(d2)  returns  RIDE-9001

//rules
//   use the same fields in equals and in hashCode
//   use fields that do not change . if you hash on a mutable field and then
//   change it , the object is lost inside its own map
//   your IDE generates both together in one click . let it


//---------------------what we fixed today---------------------
//   an if-else chain in 5 files , one forgotten , refunds of zero  ->  overriding
//   the caller decided the price                                   ->  the object decides
//   new Vehicle() charged a made up 8.0 per km                     ->  abstract class
//   a robot driver is not a human but must accept rides            ->  interface
//   Penguin was forced to inherit fly()                            ->  split the interface
//   TripService was welded to razorpay                             ->  composition
//   equals said true and the HashMap said null                     ->  hashCode


//---------------------summary---------------------
// 1  polymorphism = many forms . one name , different behaviour
// 2  compile time = overloading . runtime = overriding
// 3  signature = type , number and order of parameters . NOT names , NOT return type
// 4  no exact match ? java widens . int -> long -> float -> double
// 5  overriding lets the OBJECT decide , which deletes the if-else chain
// 6  Parent p = new Child() is allowed . Child c = new Parent() is not
// 7  the reference type decides what you may CALL . the object decides what RUNS
// 8  fields are NOT polymorphic . they follow the reference type . never hide a field
// 9  static methods are hidden , not overridden
//10  overriding : same signature , covariant return , access widened not narrowed
//11  abstract class = a concept with no real object . can hold both kinds of method
//12  interface = a contract . CAN-DO . one class , many interfaces
//13  a method that only throws means the class was forced to inherit a lie
//14  IS-A -> inheritance . HAS-A -> composition . prefer composition
//15  override equals and hashCode TOGETHER , on the same unchanging fields


//---------------------homework---------------------
//1 add Auto to the pricing service . count how many files you had to change (1)
//2 add a Bicycle that has no engine . should it extend Vehicle ? argue both ways
//3 make Customer implement a Payable interface with pay() . Driver should not have it
//4 write a Wallet class and give TripService a WalletGateway . do not touch TripService
//5 put 3 Drivers with the same driverId into a HashSet . how many are in the set ?
//  remove hashCode and answer again
