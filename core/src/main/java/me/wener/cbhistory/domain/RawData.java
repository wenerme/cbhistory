package me.wener.cbhistory.domain;

import com.google.gson.annotations.Expose;
import java.util.Date;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import me.wener.cbhistory.domain.entity.Identifiable;

@Data
@ToString(exclude = {"result"})
@Accessors(chain = true)
public class RawData implements Identifiable<Long>
{
    @Expose(deserialize = false)
    private Long sid;

    private String status;
    private String result;
    private Date lastUpdateDate;

    @Override
    public Long getId()
    {
        return sid;
    }
}
