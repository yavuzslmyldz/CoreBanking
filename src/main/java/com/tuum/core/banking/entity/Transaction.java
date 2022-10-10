package com.tuum.core.banking.entity;

import com.tuum.core.banking.constants.TransactionStatus;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

import static com.tuum.core.banking.constants.Messages.*;

@Entity
@Table(name = "transaction")
public class Transaction extends CoreBankingBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_id_generator")
    @SequenceGenerator(name="transaction_id_generator",
            sequenceName = "transaction_seq", allocationSize=1, initialValue=1)
    private long id;

    @Column(name="balance_id")
    @NotNull
    private long balanceId;

    @Column(name="account_id", nullable = false)
    @NotNull
    @Min(value = 100000,message = INVALID_ACCOUNT)
    private long accountId;

    @Column(name="currency")
    @NotBlank(message= CURRENCY_MISSING)
    private String currency;

    @Column(name="amount")
    @NotNull
    @DecimalMin(value = "0.00",message = INVALID_AMOUNT_GREATER)
    @DecimalMax(value="9999999999999.99", message= INVALID_AMOUNT_LESS)
    private BigDecimal amount;

    @Column(name="direction")
    @NotBlank(message=DIRECTION_MISSING)
    @NotNull
    private String direction;

    @Column(name="description")
    @Size(max = 255, message = DESCRIPTION_TOO_LONG)
    @NotBlank(message= DESCRIPTION_MISSING)
    @NotNull
    private String description;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column(name="process_note")
    @ElementCollection
    private List<String> processNote;



    public Transaction(long _balanceId, long _accountId, String _currency, BigDecimal _amount, String _direction,
                       String _description) {
        balanceId = _balanceId;
        accountId = _accountId;
        currency = _currency;
        amount = _amount;
        direction = _direction;
        description = _description;
    }

    public Transaction() {

    }

    public long getId() {
        return id;
    }
    public long getBalanceId() {
        return balanceId;
    }
    public long getAccountId() {
        return accountId;
    }
    public String getDirection() {
        return direction;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getProcessNote() {
        return processNote;
    }
    public String getCurrency() {
        return currency;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void setBalanceId(long balanceId) {
        this.balanceId = balanceId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public void setProcessNote(List<String> processNote) {
        this.processNote = processNote;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String toString() {
       StringBuilder sb = new StringBuilder();
        sb.append("Transaction -> ");
        sb.append(getId());
        sb.append(System.getProperty("line.separator"));
        sb.append("Account Id -> ");
        sb.append(getAccountId());
        sb.append(System.getProperty("line.separator"));
        sb.append("Balance Id -> ");
        sb.append(getBalanceId());
        sb.append(System.getProperty("line.separator"));
        sb.append("Available Amount -> ");
        sb.append(getAmount()); sb.append(" "); sb.append(getCurrency());

        return sb.toString();
    }


}
