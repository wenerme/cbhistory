package me.wener.cbhistory.domain;

import javax.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Data
public class BasicInfo
{
    private Long tid;
    private Long pid;
    private Long sid;
}
