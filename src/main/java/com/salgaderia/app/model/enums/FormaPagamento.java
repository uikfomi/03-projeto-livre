package com.salgaderia.app.model.enums;

public enum FormaPagamento {
    DINHEIRO {
        @Override
        public boolean isPermiteTroco() {
            return true;
        }
    },
    PIX {
        @Override
        public boolean isPermiteTroco() {
            return false;
        }
    },
    CARTAO_CREDITO {
        @Override
        public boolean isPermiteTroco() {
            return false;
        }
    },
    CARTAO_DEBITO {
        @Override
        public boolean isPermiteTroco() {
            return false;
        }
    };

    public abstract boolean isPermiteTroco();
}
