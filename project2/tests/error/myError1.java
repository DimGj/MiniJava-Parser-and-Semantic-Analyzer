class MainClass {
    public static void main(String[] args) {
        System.out.println(0);
    }
}

class A extends B {
    int hi;
}

class B extends A {  //error:circular inheritance
    int hey;
}
