package me.wener.cbhistory.domain;

import com.google.gson.annotations.Expose;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.domain.Persistable;

@Data
@ToString(exclude = {"result","article"})
@EqualsAndHashCode(exclude = {"article"})
@Entity
public class RawData implements Persistable<Long>
{
//    @Id
//    @Expose(deserialize = false,serialize = false)
//    private Long id;
    @Override
    public Long getId()
    {
        return getSid();
    }

    @Override
    public boolean isNew()
    {
        return null == getId();
    }

    public RawData setSid(Long sid)
    {
        this.sid = sid;
        return this;
    }

    @Id
    @Expose(deserialize = false)
    private Long sid;

    private String status;
    @Lob
    private String result;

    @OneToOne(mappedBy = "rawData", optional = false, fetch = FetchType.LAZY)
    @Expose(deserialize = false)
    private Article article;
}