package me.wener.cbhistory.domain;

import com.google.gson.annotations.Expose;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

public abstract class DomainGotSid implements Persistable<Long>
{
    @Id
    @Expose(deserialize = false,serialize = false)
    @Getter @Setter
    private Long id;

    @Override
    public boolean isNew()
    {
        return null == getId();
    }

    private Long sid;

    public DomainGotSid setSid(int sid)
    {
        setSid(sid);
        return this;
    }
    public DomainGotSid setSid(Long sid)
    {
        // 需要将 sid 与 id 同步
        this.sid = sid;
        if (getId() == null)
            setId(sid);
        return this;
    }

    public abstract void _setSid(Long id);
}
