class Main {
  public static void main(String[] args) {
    A a;
    A b;
    A c;
    A result;
    a = new A();
    b = new A();
    c = new A();

    result = (a.bar(a.foo(b), c));

    result = (( result.foo(b.bar(b, a.foo(c)))).bar(result, a.foo(a)));
  }
}

class A {
  public A foo(A x) { return x; }
  public A bar(A x, A y) { return x.foo(y); }
}