class MainClass {
    public static void main(String[] args) {
        System.out.println(0);
    }
}

class A {
    MainClass field;                          //not allowed field of type MainClass

    public MainClass method(MainClass x) {    //not allowed return type and param of type MainClass
        MainClass local;                      //not allowed local var of type MainClass
        return x;
    }
}

class B extends MainClass {                   //ok
    int x;
}
