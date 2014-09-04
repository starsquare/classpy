package com.github.zxh.classpy.classfile.datatype;

import com.github.zxh.classpy.classfile.ClassComponent;
import com.github.zxh.classpy.classfile.ClassReader;
import com.github.zxh.classpy.common.IntValue;

/**
 * Unsigned four-byte quantity.
 * 
 * @author zxh
 */
public class U4 extends ClassComponent implements IntValue {

    private int value;

    @Override
    public int getValue() {
        return value;
    }
    
    @Override
    protected void readContent(ClassReader reader) {
        value = reader.readInt();
        if (value < 0) {
            // todo
        }
        setDesc(String.valueOf(value));
    }
    
}
