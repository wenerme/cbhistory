package me.wener.cbhistory.domain;

import javax.persistence.Lob;
import lombok.Data;

@Data
public class StatusResponse
{
    private String status;
    private Object result;
}
