package me.wener.cbhistory.domain;

import com.google.gson.annotations.Expose;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import me.wener.cbhistory.core.CBHistory;
import org.springframework.data.domain.Persistable;

@Data
@ToString(exclude = {"result"})
@Accessors(chain = true)
@Entity
@Table(name = RawData.TABLE_NAME)
public class RawData implements Persistable<Long>, CBHistoryTable
{
    public static final String TABLE_NAME = TABLE_PREFIX+"rawdata";

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

    @Column
    private String status;
    @Column
    @Lob
    private String result;

    private Date lastUpdate;
}
