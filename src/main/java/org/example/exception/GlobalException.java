package org.example.exception;

import org.example.result.CodeMsg;

public class GlobalException extends RuntimeException {
    private static final long serialVersionUID = 1L;//版本号
    private CodeMsg codeMsg;

    public GlobalException(CodeMsg codeMsg) {
        super(codeMsg.toString());
        this.codeMsg = codeMsg;
    }

    public CodeMsg getCodeMsg() {
        return codeMsg;
    }

}
