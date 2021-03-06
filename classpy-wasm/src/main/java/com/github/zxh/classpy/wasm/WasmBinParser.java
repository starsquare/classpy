package com.github.zxh.classpy.wasm;

import com.github.zxh.classpy.common.FileComponent;
import com.github.zxh.classpy.common.FileParser;

public class WasmBinParser implements FileParser {

    @Override
    public WasmBinFile parse(byte[] data) {
        WasmBinFile wasm = new WasmBinFile();
        try {
            wasm.read(new WasmBinReader(data));
            postRead(wasm, wasm);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return wasm;
    }

    private static void postRead(WasmBinComponent bc,
                                 WasmBinFile wasm) {
        for (FileComponent c : bc.getComponents()) {
            postRead((WasmBinComponent) c, wasm);
        }
        bc.postRead(wasm);
    }

}
