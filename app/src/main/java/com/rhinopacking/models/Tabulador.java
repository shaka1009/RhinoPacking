package com.rhinopacking.models;

public class Tabulador {

    int n1, n2, n3;

    long r;

    public int getN1() {
        return n1;
    }

    public int getN2() {
        return n2;
    }

    public int getN3() {
        return n3;
    }

    public long getR() {
        return r;
    }

    public Tabulador(int n1, int n2, int n3) {
        this.n1 = n1;
        this.n2 = n2;
        this.n3 = n3;

        this.r = n1*n2*n3;
    }
}
