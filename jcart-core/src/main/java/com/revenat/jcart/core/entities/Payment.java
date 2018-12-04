package com.revenat.jcart.core.entities;

import com.revenat.jcart.core.base.AbstractEntity;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Payment)) return false;
        if (!super.equals(o)) return false;

        Payment payment = (Payment) o;

        if (ccNumber != null ? !ccNumber.equals(payment.ccNumber) : payment.ccNumber != null) return false;
        return cvv != null ? cvv.equals(payment.cvv) : payment.cvv == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (ccNumber != null ? ccNumber.hashCode() : 0);
        result = 31 * result + (cvv != null ? cvv.hashCode() : 0);
        return result;
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
