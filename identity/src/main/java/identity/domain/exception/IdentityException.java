package identity.domain.exception;

public class IdentityException extends RuntimeException {

    private final IdentityExceptionCode exceptionCode;

    public IdentityException(IdentityExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }

    public IdentityExceptionCode getExceptionCode() {
        return exceptionCode;
    }

    public String getErrorCode() {
        return exceptionCode.getCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof IdentityException)) {
            return false;
        }
        return this.exceptionCode.getCode().equals(((IdentityException) obj).exceptionCode.getCode());
    }
}