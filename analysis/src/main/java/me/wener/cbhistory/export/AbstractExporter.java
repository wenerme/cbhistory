package me.wener.cbhistory.export;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import me.wener.cbhistory.repo.ArticleRepo;
import me.wener.cbhistory.repo.CommentRepo;
import me.wener.cbhistory.utils.Same;
import org.omg.SendingContext.RunTime;

public abstract class AbstractExporter implements Exporter
{

    @Getter @Setter
    private int limit = 15;

    @Getter
    private final Map<String, String> categories = Maps.newLinkedHashMap();
    private Map<String, Object> info = null;

    @Getter
    @Setter
    private String basePath;

    @Override
    public void export(String category, String description, Object data)
    {
        categories.put(category, description == null ? category : description);

        save(category, data);
    }

    protected void save(String category, Object data)
    {
        try
        {
            Files.write(Same.getGson().toJson(data), new File(getFilePath(category)), Charsets.UTF_8);
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    protected final String getFilePath(String category)
    {
        return getBasePath() + File.separator + getCode() + "-" + category + ".json";
    }

    @Override
    public final Map<String, Object> getInfo()
    {
        if (info == null)
        {
            info = Maps.newHashMap();
            info.put("category", categories);
            info.put("code", getCode());
            info.put("title", getTitle());
            info.put("generateDate", new Date());
        }

        return info;
    }


    /**
     * 导出所有, 并且不输出其他类
     */
    public Object asPieCount(Map<?, Long> sourceDesc)
    {
        return asPieCount(sourceDesc, -1, -1);
    }

    /**
     * 导出所有
     */
    public Object asPieCount(Map<?, Long> sourceDesc, long count)
    {
        return asPieCount(sourceDesc, count, -1);
    }

    public Object asPieCount(Map<?, Long> sourceDesc, long count, int top)
    {
        List<LabelValue> result = Lists.newArrayList();
        int i = 0;
        int sum = 0;
        for (final Map.Entry<?, Long> entry : sourceDesc.entrySet())
        {
            if (i++ == top)
                break;
            sum += entry.getValue();
            result.add(new LabelValue(String.valueOf(entry.getKey()), entry.getValue()));
        }

        if (count - sum > 0)
            result.add(new LabelValue("其他", count - sum));

        return result;
    }

}
