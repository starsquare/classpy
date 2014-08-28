package com.github.zxh.classpy.dexfile.data;

import com.github.zxh.classpy.dexfile.list.SizeKnownList;
import com.github.zxh.classpy.dexfile.DexComponent;
import com.github.zxh.classpy.dexfile.DexFile;
import com.github.zxh.classpy.dexfile.DexReader;
import com.github.zxh.classpy.dexfile.datatype.Uleb128;
import com.github.zxh.classpy.dexfile.ids.FieldIdItem;
import com.github.zxh.classpy.dexfile.ids.MethodIdItem;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author zxh
 */
public class ClassDataItem extends DexComponent {

    private Uleb128 staticFieldsSize;
    private Uleb128 instanceFieldsSize;
    private Uleb128 directMethodsSize;
    private Uleb128 virtualMethodsSize;
    private SizeKnownList<EncodedField> staticFields;
    private SizeKnownList<EncodedField> instanceFields;
    private SizeKnownList<EncodedMethod> directMethods;
    private SizeKnownList<EncodedMethod> virtualMethods;
    
    @Override
    protected void readContent(DexReader reader) {
        staticFieldsSize = reader.readUleb128();
        instanceFieldsSize = reader.readUleb128();
        directMethodsSize = reader.readUleb128();
        virtualMethodsSize = reader.readUleb128();
        staticFields = reader.readSizeKnownList(staticFieldsSize, EncodedField::new);
        instanceFields = reader.readSizeKnownList(instanceFieldsSize, EncodedField::new);
        directMethods = reader.readSizeKnownList(directMethodsSize, EncodedMethod::new);
        virtualMethods = reader.readSizeKnownList(virtualMethodsSize, EncodedMethod::new);
    }

    @Override
    protected void postRead(DexFile dexFile) {
        super.postRead(dexFile);
        
        int fieldIdx = 0;
        for (EncodedField field : staticFields) {
            fieldIdx += field.fieldIdxDiff.getValue();
            FieldIdItem fieldId = dexFile.getFieldIdItem(fieldIdx);
            field.setDesc(fieldId.getDesc());
        }
        fieldIdx = 0;
        for (EncodedField field : instanceFields) {
            fieldIdx += field.fieldIdxDiff.getValue();
            FieldIdItem fieldId = dexFile.getFieldIdItem(fieldIdx);
            field.setDesc(fieldId.getDesc());
        }
        int methodIdx = 0;
        for (EncodedMethod method : directMethods) {
            methodIdx += method.methodIdxDiff.getValue();
            MethodIdItem methodId = dexFile.getMethodIdItem(methodIdx);
            method.setDesc(methodId.getDesc());
        }
        methodIdx = 0;
        for (EncodedMethod method : virtualMethods) {
            methodIdx += method.methodIdxDiff.getValue();
            MethodIdItem methodId = dexFile.getMethodIdItem(methodIdx);
            method.setDesc(methodId.getDesc());
        }
    }

    @Override
    public List<? extends DexComponent> getSubComponents() {
        return Arrays.asList(staticFieldsSize, instanceFieldsSize,
                directMethodsSize, virtualMethodsSize,
                staticFields, instanceFields,
                directMethods, virtualMethods);
    }
    
    
    public static class EncodedField extends DexComponent {

        private Uleb128 fieldIdxDiff;
        private Uleb128 accessFlags;
        
        @Override
        protected void readContent(DexReader reader) {
            fieldIdxDiff = reader.readUleb128();
            accessFlags = reader.readUleb128();
        }
        
        @Override
        public List<? extends DexComponent> getSubComponents() {
            return Arrays.asList(fieldIdxDiff, accessFlags);
        }
    
    }
    
    public static class EncodedMethod extends DexComponent {

        private Uleb128 methodIdxDiff;
        private Uleb128 accessFlags;
        private Uleb128 codeOff;
        private CodeItem codeItem;
        
        @Override
        protected void readContent(DexReader reader) {
            methodIdxDiff = reader.readUleb128();
            accessFlags = reader.readUleb128();
            codeOff = reader.readUleb128();
            if (codeOff.getValue() > 0) {
                readCodeItem(reader);
            }
        }
        
        private void readCodeItem(DexReader reader) {
            int currentPosition = reader.getPosition();
            reader.setPosition(codeOff.getValue());
            codeItem = new CodeItem();
            codeItem.read(reader);
            reader.setPosition(currentPosition);
        }
        
        @Override
        public List<? extends DexComponent> getSubComponents() {
            Stream<DexComponent> all = Stream.of(methodIdxDiff, accessFlags,
                    codeOff, codeItem);
            
            return all.filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        
    }
    
}