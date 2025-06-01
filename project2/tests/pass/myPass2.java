class InvalidArrayLengthAccess {
    public static void main(String[] args) {
        System.out.println(0);
    }
}

class Test {
    public int getLength() {
        int a;
        a = new int[5].length;  //accepted
        return a;
    }
}
