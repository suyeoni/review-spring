package com.example.java;

import java.util.Arrays;
import java.util.List;

public class Dispatch {

    static class StaticDispatch {
        public void run(Integer a) {
            System.out.println("run(" + a + ")");
        }

        public void run(String a) {
            System.out.println("run(" + a + ")");
        }
    }

    /*
     * Single Dynamic Dispatch
     * */
    static abstract class DynamicDispatch {
        abstract void run();
    }

    static class DynamicDispatchA extends DynamicDispatch {
        @Override
        void run() {
            System.out.println("run : " + getClass().getSimpleName());
        }
    }

    static class DynamicDispatchB extends DynamicDispatch {
        @Override
        void run() {
            System.out.println("run : " + getClass().getSimpleName());
        }
    }

    /*
     * Double Dynamic Dispatch
     * */
    static abstract class Post {
        abstract void postOn(SNS sns);

        abstract void postOn2(Twitter sns);

        abstract void postOn2(Instagram sns);
    }

    static class Text extends Post {
        @Override
        void postOn(SNS sns) {
            // if (sns instanceof Twitter)
            // if (sns instanceof Instagram)
            sns.post(this);
        }

        @Override
        void postOn2(Twitter sns) {
            //nothing
        }

        @Override
        void postOn2(Instagram sns) {
            //nothing
        }
    }

    static class Picture extends Post {
        @Override
        void postOn(SNS sns) {
            sns.post(this);
        }

        @Override
        void postOn2(Twitter sns) {
            //nothing
        }

        @Override
        void postOn2(Instagram sns) {
            //nothing
        }
    }

    interface SNS {
        void post(Post postType);
    }

    static class Twitter implements SNS {
        @Override
        public void post(Post postType) {
            System.out.println("post " + postType.getClass().getSimpleName() + " to Twitter");
        }
    }

    static class Instagram implements SNS {
        @Override
        public void post(Post postType) {
            System.out.println("post " + postType.getClass().getSimpleName() + " to Instagram");
        }
    }

    public static void main(String[] args) {
        StaticDispatch staticDispatch = new StaticDispatch();
        // method overloading은 static dispatch이다. (compile시점에 가져옴.
        // bytecode에도 어떤 method를 사용할지 이미 명시되어있음)
        /*
        *L1
          LINENUMBER 108 L1
          ALOAD 1
          ICONST_1
          INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;
          INVOKEVIRTUAL com/example/java/Dispatch$StaticDispatch.run (Ljava/lang/Integer;)V
         L2
          LINENUMBER 109 L2
          ALOAD 1
          LDC "str"
          INVOKEVIRTUAL com/example/java/Dispatch$StaticDispatch.run (Ljava/lang/String;)V
        * */
        staticDispatch.run(1);
        staticDispatch.run("str");

        DynamicDispatch dynamicDispatch = new DynamicDispatchA();
        dynamicDispatch.run(); // dynamic dispatch.
        // 아무것도 안 넘어가는것처럼 보이지만 'receiver parameter'라는 것이 runtime에 넘어간다.
        // 여기에선 DynamicDispatchA 의 this가 넘어간다

        // multiple polymorphism
        List<DynamicDispatch> dynamicDispatchs = Arrays.asList(new DynamicDispatchA(), new DynamicDispatchB());
        dynamicDispatchs.forEach(dispatch -> dispatch.run()); // 마찬가지

        List<Post> posts = Arrays.asList(new Text(), new Picture());
        List<SNS> sns = Arrays.asList(new Instagram(), new Twitter());
        // Java는 single receiver라서 receiver parameter 조건이 한개밖에안돼 (multi인 언어들도있음)
        posts.forEach(post -> sns.forEach(post::postOn));

        // compile error
        // compile 시점에 receiver parameter를 정해야되는데(== parmeter type은 컴파일시점에 결정되어야한다)
        // sns라는 추상클래스로 넘기면 정확히 어떤 값인지 알 수 없기때문
        // posts.forEach(post -> sns.forEach(sns -> post.postOn2(sns)));
        // dynamic dispatch는 parameter type을 기준으로 하지 않는다!
        // -> static dispatch의 overloading에서 가능하다. 위 StaticDispatch 예제 참고

        // static dispatch가 dynamic dispatch보다 당연히 빠르다 (초창기 Android에선 interface/enum도 쓰지말라고 권고했대..)
    }

}
