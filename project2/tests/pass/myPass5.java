class test03{
    public static void main(String[] x) {
        boolean rv;
        A a;
        B b;
        C c;
        rv = (new B().set()) && (new C().set());
        System.out.println(b.get());
        System.out.println(c.get());
    }
}


class A {
    public int[] getArr(B c1, B c2, B c3){
        return new int[1000];
    }
}

class B{
    C obj;

    public boolean set(){
        return ((new int[new boolean[100].length]).length) < 100;
    }

    public int get(){
        int[] arr;
        int i;
        B test;

        test = new C();
        arr = new int[10];
        obj  = new C();
        i = 0;
        while((i < 10) && (((arr[i]) < 10) && ((arr[(arr[i])]) < 10)))
            arr[i] = i;

        {i = 37;}

        if(((this.get()) < 10) && (!!!!((((((obj.set()))))))))
            arr[9] = 0;

        else
            arr[9] = 1;

        return arr[((1 + ((2 + 3) * 77)) + ((new A().getArr(this, obj, new C()))[23]))];
    }
}

class C extends B{
}