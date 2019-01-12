package net.omisoft.rest.service.interkassa;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import net.omisoft.rest.mapper.jackson.LocalDateTimeDeserializer;
import net.omisoft.rest.mapper.jackson.LocalDateTimeSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InterkassaLog {

    @ApiModelProperty(notes = "Invoice Id", value = "5632156", example = "5632156", required = true, position = 0)
    @JsonProperty(value = "ik_inv_id")
    private String id;

    @ApiModelProperty(notes = "Transaction Id", value = "ID_4233", example = "ID_4233", required = true, position = 1)
    @JsonProperty(value = "ik_trn_id")
    private String idTransaction;

    @ApiModelProperty(notes = "Payment No.", value = "ff3d71a993774584938efaa3554d9eb7", example = "ff3d71a993774584938efaa3554d9eb7", required = true, position = 2)
    @JsonProperty(value = "ik_pm_no")
    private String uuid;

    @ApiModelProperty(notes = "Checkout ID", value = "4f269503a1da92c807000002", example = "4f269503a1da92c807000002", required = true, position = 3)
    @JsonProperty(value = "ik_co_id")
    private String idCashbox;

    @ApiModelProperty(notes = "Checkout Purse Id", value = "307447812424", example = "307447812424", required = true, position = 4)
    @JsonProperty(value = "ik_co_prs_id")
    private String idPurse;

    @ApiModelProperty(notes = "Invoice State", value = "success", example = "success", required = true, position = 5)
    @JsonProperty(value = "ik_inv_st")
    private String state;

    @ApiModelProperty(notes = "Invoice Created", value = "2013-03-17 17:30:33", example = "2013-03-17 17:30:33", required = true, position = 6)
    @JsonProperty(value = "ik_inv_crt")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime created;

    @ApiModelProperty(notes = "Invoice Processed", value = "2013-03-20 15:46:58", example = "2013-03-20 15:46:58", required = true, position = 7)
    @JsonProperty(value = "ik_inv_prc")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime processed;

    @ApiModelProperty(notes = "Currency", value = "UAH", example = "UAH", required = true, position = 8)
    @JsonProperty(value = "ik_cur")
    private String currency;

    @ApiModelProperty(notes = "Payway Via", value = "visa_liqpay_merchant_usd", example = "visa_liqpay_merchant_usd", required = true, position = 9)
    @JsonProperty(value = "ik_pw_via")
    private String paywayType;

    @ApiModelProperty(notes = "Paysystem Price", value = "99.23", example = "99.23", required = true, position = 10)
    @JsonProperty(value = "ik_ps_price")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal price;

    @ApiModelProperty(notes = "Amount", value = "99.23", example = "99.23", required = true, position = 11)
    @JsonProperty(value = "ik_am")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal amount;

    @ApiModelProperty(notes = "Checkout Refund", value = "96.2531", example = "96.2531", required = true, position = 12)
    @JsonProperty(value = "ik_co_rfn")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal checkoutRefund;

    @ApiModelProperty(notes = "Description", value = "Cool stuff", example = "Cool stuff", required = false, position = 13)
    @JsonProperty(value = "ik_desc")
    private String description;

    @ApiModelProperty(notes = "Email", value = "fed.lviv@gmail.com", example = "fed.lviv@gmail.com", required = false, position = 14)
    @JsonProperty(value = "ik_cli")
    private String email;

    @ApiModelProperty(notes = "Signature", value = "oVAOevI3mWrcvrjB4j/ySg==", example = "oVAOevI3mWrcvrjB4j/ySg==", required = true, position = 15)
    @JsonProperty(value = "ik_sign")
    private String signature;

}