package me.wener.cbhistory.export;

import java.util.Map;
import javax.annotation.Nullable;
import org.joda.time.LocalDateTime;

public interface Exporter
{
    String getCode();
    String getTitle();
    void setBasePath(String basePath);
    void setLimit(int limit);

    void doExport(LocalDateTime startExportTime, LocalDateTime endExportTime);
    Map<String, String> getCategories();
    void export(String category,@Nullable String description, Object date);
    String getBasePath();
    Map<String, Object> getInfo();
}
