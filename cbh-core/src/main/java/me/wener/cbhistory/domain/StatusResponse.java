package me.wener.cbhistory.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class StatusResponse
{
    private Status status;
    private Object result;

    public static StatusResponse success(Object result)
    {
        return of(Status.success, result);
    }

    public static StatusResponse error(Object result)
    {
        return of(Status.error, result);
    }

    public static StatusResponse of(Status status, Object result)
    {
        return new StatusResponse().setStatus(status).setResult(result);
    }

    public enum Status
    {
        error, success
    }
}
