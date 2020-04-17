package com.example.java;

public class JavaInitializeTest {
    static class Super {
        static int taxi = 1729;
        static Integer obj;
        Integer obj2;

        static {
            obj = new Integer(1);
            System.out.println("static initializer");
        }

        {
            obj2 = new Integer(2);
            System.out.println("instance initializer");
        }
    }

    static class Sub extends Super {
        static { System.out.print("Sub "); }
    }

    interface I {
        int i = 1, ii = out("ii", 2);
    }

    interface J extends I {
        int j = out("j", 3), jj = out("jj", 4);
    }

    interface K extends J {
        int k = out("k", 5);
    }

    static int out(String s, int i) {
        System.out.println(s + "=" + i);
        return i;
    }

    public static void main(String[] args) {
        System.out.println(Sub.taxi);

        System.out.println(J.i);
        System.out.println(K.j);
    }

}


