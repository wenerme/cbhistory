package me.wener.cbhistory.parser.v1;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import me.wener.cbhistory.parser.Response;

@Data
@Accessors(chain = true)
@ToString(exclude = "content")
public class ResponseData implements Response
{
    private String state;
    private String message;
    private String content;

    @Override
    public boolean isSuccess()
    {
        return "success".equals(state);
    }
}
