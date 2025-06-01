class S {
    public static void main(String[] args) {
    }
}


class A{
    int i;

    public int get(int k){return i;}

}

class B extends A{
    int d;
}

class C extends B{
    int c;

    public int get(){return i;}
}