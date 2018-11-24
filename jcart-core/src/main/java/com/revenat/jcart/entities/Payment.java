package com.revenat.jcart.entities;

import com.revenat.jcart.base.AbstractEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "payments")
public class Payment extends AbstractEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "cc_number")
    private String ccNumber;

    private String cvv;

    private BigDecimal amount;

    public String getCcNumber() {
        return ccNumber;
    }

    public void setCcNumber(String ccNumber) {
        this.ccNumber = ccNumber;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Payment{");
        sb.append("amount=").append(amount);
        sb.append(", id=").append(id);
        sb.append('}');
        return sb.toString();
    }
}
