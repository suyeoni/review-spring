package com.example.java;

import java.util.ArrayList;
import java.util.List;

class Point {
    int x, y;
}

interface Colorable {
    void setColor(int color);
}

class ColoredPoint extends Point implements Colorable {
    int color;

    @Override
    public void setColor(int color) { this.color = color; }
}

class TestPoint extends Point {}

final class EndPoint extends Point {}

class ConversionTest {
    public static void main(String[] args) {
        Point p = new Point();
        ColoredPoint cp = new ColoredPoint();
        Colorable c;
        // The following may cause errors at run time because
        // we cannot be sure they will succeed; this possibility
        // is suggested by the casts:
        cp = (ColoredPoint) p;  // p might not reference an
        // object which is a ColoredPoint
        // or a subclass of ColoredPoint
        c = (Colorable) p;      // p might not be Colorable
        // The following are incorrect at compile time because
        // they can never succeed as explained in the text:
//        Long l = (Long) p;            // compile-time error #1
        EndPoint e = new EndPoint();
//        c = (Colorable) e;            // compile-time error #2
        TestPoint tp = new TestPoint();
        c = (Colorable) tp;              // runtime error

        List a = new ArrayList<Integer>();
        List<Integer> b = a; // unchecked
    }
}
