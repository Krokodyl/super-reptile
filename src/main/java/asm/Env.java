package asm;

import resources.Hex;

import java.util.*;

import static asm.Register.*;
import static resources.Hex.h4;

public class Env {
    
    int[] wram = new int[200];
    Map<Register, Integer> registers = new HashMap<>();
    Deque<Integer> stack = new ArrayDeque<>(); 
    
    public Env() {
        rep(0xFF);
        setRegister(A, 0);
        setRegister(X, 0);
        setRegister(Y, 0);
    }
    
    private int readWram(int address, RegisterSize rs) {
        if (rs == RegisterSize.SIZE_8_BITS) return wram[address];
        else {
            return wram[address+1]*0x100 + wram[address];
        }
    }
    
    private void writeWram(int address, RegisterSize rs, int value) {
        if (rs == RegisterSize.SIZE_8_BITS) wram[address] = value;
        else {
            wram[address+1] = value / 0x100;
            wram[address] = value % 0x100;
        }
    }
    
    public void setRegister(Register r, int value) {
        RegisterSize rs = RegisterSize.SIZE_16_BITS;
        if (r == A) {
            rs = getAccumulatorSize();
        } else {
            rs = getIndexSize();
        }
        int result = value & 0xFFFF;
        if (rs == RegisterSize.SIZE_16_BITS) {
            registers.put(r, result);
            if (result>=0x8000) registers.put(P_NEGATIVE, 1);
            else registers.put(P_NEGATIVE, 0);
        } else {
            int previous = registers.get(r);
            previous = previous & 0xFF00;
            result = value & 0xFF;
            registers.put(r, previous + result);
            if (result>=0x80) registers.put(P_NEGATIVE, 1);
            else registers.put(P_NEGATIVE, 0);
        }
        if (result==0) registers.put(P_ZERO, 1);
        else registers.put(P_ZERO, 0);
    }

    /**
     * STATUS REGISTER
     */
    public void sep(int value) {
        if ((value & 0x01) == 0x01) registers.put(P_CARRY, 1);
        if ((value & 0x02) == 0x02) registers.put(P_ZERO, 1);
        if ((value & 0x04) == 0x04) registers.put(P_IRQ, 1);
        if ((value & 0x08) == 0x08) registers.put(P_DECIMAL, 1);
        if ((value & 0x10) == 0x10) registers.put(P_INDEX, 1);
        if ((value & 0x20) == 0x20) registers.put(P_ACCUMULATOR, 1);
        if ((value & 0x40) == 0x40) registers.put(P_OVERFLOW, 1);
        if ((value & 0x80) == 0x80) registers.put(P_NEGATIVE, 1);
        
    }

    public void rep(int value) {
        if ((value & 0x01) == 0x01) registers.put(P_CARRY, 0);
        if ((value & 0x02) == 0x02) registers.put(P_ZERO, 0);
        if ((value & 0x04) == 0x04) registers.put(P_IRQ, 0);
        if ((value & 0x08) == 0x08) registers.put(P_DECIMAL, 0);
        if ((value & 0x10) == 0x10) registers.put(P_INDEX, 0);
        if ((value & 0x20) == 0x20) registers.put(P_ACCUMULATOR, 0);
        if ((value & 0x40) == 0x40) registers.put(P_OVERFLOW, 0);
        if ((value & 0x80) == 0x80) registers.put(P_NEGATIVE, 0);
    }
    
    public void ldx(int i) {
        int result = i & 0xFFFF;
        registers.put(X, result);
        if (result>=0x8000) registers.put(P_NEGATIVE, 1);
        else registers.put(P_NEGATIVE, 0);
        if (result==0) registers.put(P_ZERO, 1);
        else registers.put(P_ZERO, 0);
    }
    
    public void ldx(byte b) {
        int result = b & 0xFF;
        registers.put(X, result);
        if (result>=0x80) registers.put(P_NEGATIVE, 1);
        else registers.put(P_NEGATIVE, 0);
        if (result==0) registers.put(P_ZERO, 1);
        else registers.put(P_ZERO, 0);
    }
    
    public void sec() {
        registers.put(P_CARRY, 0x01);
    }

    public void txa() {
        int result = 0;
        if (getAccumulatorSize()==RegisterSize.SIZE_16_BITS) {
            result = registers.get(X);
            registers.put(A, result);
            if (result>=0x8000) registers.put(P_NEGATIVE, 1);
            else registers.put(P_NEGATIVE, 0);
        } else {
            int a = registers.get(A);
            a = a & 0xFF00;
            result = registers.get(X);
            result = result & 0xFF;
            registers.put(A, a + result);
            if (result>=0x80) registers.put(P_NEGATIVE, 1);
            else registers.put(P_NEGATIVE, 0);
        }
        if (result==0) registers.put(P_ZERO, 1);
        else registers.put(P_ZERO, 0);
    }

    public void tax() {
        int result = 0;
        if (getIndexSize()==RegisterSize.SIZE_16_BITS) {
            result = registers.get(A);
            registers.put(X, result);
            if (result>=0x8000) registers.put(P_NEGATIVE, 1);
            else registers.put(P_NEGATIVE, 0);
        } else {
            int x = registers.get(X);
            x = x & 0xFF00;
            result = registers.get(A);
            result = result & 0xFF;
            registers.put(X, x + result);
            if (result>=0x80) registers.put(P_NEGATIVE, 1);
            else registers.put(P_NEGATIVE, 0);
        }
        if (result==0) registers.put(P_ZERO, 1);
        else registers.put(P_ZERO, 0);
    }

    /**
     * STACK instructions
     */
    public void pha() {
        stack.push(registers.get(A));
    }
    
    public void pla() {
        int value = stack.poll();
        int result = value;
        if (getAccumulatorSize() == RegisterSize.SIZE_8_BITS) {
            result = value & 0xFF;
            int hb = value / 0x100;
            stack.push(hb);
        }
        setRegister(A, result);
        
    }

    public RegisterSize getAccumulatorSize() {
        if (registers.get(P_ACCUMULATOR) == 0x01) {
            return RegisterSize.SIZE_8_BITS;
        }
        else return RegisterSize.SIZE_16_BITS;
    }

    public RegisterSize getIndexSize() {
        if (registers.get(P_INDEX) == 0x01) {
            return RegisterSize.SIZE_8_BITS;
        }
        else return RegisterSize.SIZE_16_BITS;
    }

    public void printEnv() {
        System.out.println(
                String.format("A:%s X:%s Y:%s",
                        h4(registers.get(A)),
                        h4(registers.get(X)),
                        h4(registers.get(Y))
                        )
        );
    }
}
