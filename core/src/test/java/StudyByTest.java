import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.io.Files;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.wener.cbhistory.core.CBHistory;
import me.wener.cbhistory.domain.CommentInfo;
import me.wener.cbhistory.domain.RawComment;
import me.wener.cbhistory.util.InstanceCreatorWithInstance;
import me.wener.cbhistory.core.event.AbstractEvent;
import me.wener.cbhistory.util.CodecUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Slf4j
@Ignore
public class StudyByTest
{
    @Test
    public void testGetPage() throws Exception
    {
        String url = "http://www.cnbeta.com/articles/287931.htm";
        HttpRequest request = HttpRequest.get(url);
        System.out.println("start sending");
        HttpResponse response = request.send();
        System.out.println("get response");
        String body = response.bodyText();

        Pattern regGV = Pattern.compile("^GV\\.DETAIL[^\\{]+(?<data>\\{[^\\}]+})", Pattern.MULTILINE);
        Matcher matcher = regGV.matcher(body);
        if (matcher.find())
        {
            String data = matcher.group("data");
//            ArticleDetail detail = ArticleDetail.of(data);
//            System.out.println(detail);
        } else
            throw new Exception("无法匹配出 GvDetail 的内容.");
    }

    @Test
    public void testAllProps()
    {
        System.out.println(System.getProperties());
    }

    @Test
    public void testGetCmt()
    {
        String url = "http://www.cnbeta.com/cmt";
        HttpRequest request = HttpRequest.post(url);
        request
                .contentType("application/x-www-form-urlencoded")
                .header("X-Requested-With", "XMLHttpRequest")
                .form("op", "MSwyODc5MzEsOTUyNGM%3DkreE8FRh");

        HttpResponse response = request.send();
        String json = response.bodyText();

        Map<String, String> map = CodecUtils.jsonToMap(json);
        //assert map.get("")
        String result = CodecUtils.decodeBase64(map.get("result"));
        result = result.replaceFirst("^cnbeta", "");// 去除前缀

        System.out.println(result);

    }

    @Test
    public void testUseLocalData() throws IOException
    {
        String json = Files.toString(new File("C:\\cmt.json"), Charsets.UTF_8);

        Map<String, String> map = CodecUtils.jsonToMap(json);
        //assert map.get("")
        String result = CodecUtils.decodeBase64(map.get("result"));
        result = result.replaceFirst("^cnbeta", "");// 去除前缀

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        RawComment rawComment = gson.fromJson(result, RawComment.class);

        System.out.println(result);
    }

    public static void main(String[] args)
    {
        // 用 @Test 不会让该线程持续运行
        Timer timer = new Timer();
        timer.schedule(new SimpleTask(), 0, 1000);
        System.out.println("App started.");
    }

    static class SimpleTask extends TimerTask
    {

        @Override
        public void run()
        {
            System.out.println("SimpleTask take task.");
        }
    }

    @Test
    public void testCurrentEvent() throws InterruptedException
    {
        EventBus bus = new AsyncEventBus(new Executor()
        {
            @Override
            public void execute(Runnable command)
            {
                new Thread(command).start();
            }
        });

        AbstractEvent object = new AbstractEvent()
        {
            @Subscribe
            public void dealInt(Integer i) throws InterruptedException
            {
                System.out.println("Start deal i VALUE:" + i);
                Thread.sleep(500);
                System.out.println("End deal i VALUE:" + i);
            }

            @Subscribe
            public void dealInt2(Integer i) throws InterruptedException
            {
                System.out.println("Start dealInt2 VALUE:" + i);
                Thread.sleep(500);
                System.out.println("End dealInt2 VALUE:" + i);
            }

            @Subscribe
            @AllowConcurrentEvents
            public void dealDouble(Double i) throws InterruptedException
            {
                log.info("Start dealDouble VALUE:" + i);
                Thread.sleep(500);
                log.info("End dealDouble VALUE:" + i);
            }

            @Subscribe
            @AllowConcurrentEvents
            public void dealDouble2(Double i) throws InterruptedException
            {
                log.info("Start dealDouble2 VALUE:" + i);
                Thread.sleep(500);
                log.info("End dealDouble2 VALUE:" + i);
            }
        };
        bus.register(object);
        bus.register(object);

        bus.post(1);
        bus.post(2);
        log.warn("End post int");
        bus.post(3.0);
        bus.post(4.0);
        log.warn("End post double");

        Thread.sleep(5000);
    }

    @Test
    public void testLog4j()
    {
//        System.out.println(Logger.getRootLogger().getLevel());
        System.out.println(log.isDebugEnabled());
//        System.out.println(Logger.getRootLogger().getLevel());
        System.out.println(log.isDebugEnabled());

        log.debug("Debug it!");
        log.info("Info it!");
        log.warn("warn it!");
        System.out.println(log);

        WithStaticBlock.nothing();
    }
    @Slf4j
    static class WithStaticBlock
    {
        static {
            log.info("********************** init static block");
        }
        public static void nothing()
        {}
    }

    @Test
    public void testBase64()
    {
        // atob('MSwyODcwNTMsMzBhZjI=')
        // "1,287053,30af2"
        assert CodecUtils.decodeBase64("MSwyODcwNTMsMzBhZjI=").equals("1,287053,30af2");
        assert CodecUtils.encodeBase64("1,287053,30af2").equals("MSwyODcwNTMsMzBhZjI=");

        System.out.println(CBHistory.calcOp("1","287053","30af2", 8));
        assert CBHistory.calcOp("1","287053","30af2", 8).startsWith("MSwyODcwNTMsMzBhZjI");

        System.out.println(CodecUtils.decodeURIComponent(CBHistory.calcOp("1","287053","30af2", 8)));
    }

    @Test
    public void testMergeObject()
    {
        String jsonA = "{\"name\":\"wener\",\"gf\":{\"name\":\"xiao\",\"age\":21}}";
        String jsonB = "{\"age\":22}";
        Person person = new Person();
        Gson gson = new GsonBuilder().registerTypeAdapter(person.getClass(), new InstanceCreatorWithInstance<>(person)).create();
        gson.fromJson(jsonA, person.getClass());
        gson.fromJson(jsonB, Person.class);

        assert person.getName().equals("wener");
        assert person.getAge().equals(22);
    }
    @Data
    static class Person
    {
        String name;
        Integer age;
    }

    @Test
    public void testGsonToDate()
    {
        System.out.println(new Gson().fromJson("\"2014-04-30 11:57:01\"", Date.class));
        // 这个会失败
//        System.out.println(new Gson().fromJson("2014-04-30 11:57:01", Date.class));
    }

    @Test
    public void testUseMultiMap() throws IOException
    {
        GsonBuilder builder = new GsonBuilder();
        HandObj handObj = new Gson().fromJson(Files.toString(new File("C:\\tmp.json"), Charsets.UTF_8), HandObj.class);

        System.out.println(handObj);
    }

    static class HandObj
    {
        @SerializedName("cmntdict")
        private Map<String, List<CommentInfo>> commentDict;
    }

    @Test
    public void testPropertySourcesPlaceholderConfigurer()
    {
        PropertySourcesPlaceholderConfigurer p = new PropertySourcesPlaceholderConfigurer();
        String[] resources = {"default.properties", "db.properties","app.properties"};
        List<Resource> resourceLocations = Lists.newArrayList();

        for (String resource : resources)
        {
            ClassPathResource classPathResource = new ClassPathResource(resource);
            if (classPathResource.exists())
            {
                log.info("加载属性文件: "+resource);
                resourceLocations.add(classPathResource);
            }
        }

        p.setLocations(resourceLocations.toArray(new Resource[0]));

    }

}


