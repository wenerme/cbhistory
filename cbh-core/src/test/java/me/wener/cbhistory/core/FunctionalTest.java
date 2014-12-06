package me.wener.cbhistory.core;

import me.wener.cbhistory.core.process.CommonProcess;
import me.wener.cbhistory.domain.entity.Article;
import me.wener.cbhistory.domain.OpInfo;
import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;
import org.junit.Ignore;
import org.junit.Test;

public class FunctionalTest
{
    @Test
    public void launchTest()
    {
        App.getInjector();
    }
    @Test
    public void parseOp()
    {
        // atob('MSwyODc5MzEsOTUyNGM=')
        // "1,287931,9524c"
        String noTail = "MSwyODc5MzEsOTUyNGM=";
        OpInfo info = CBHistory.parseOp(noTail);

        assert info.getPage() == 1;
        assert info.getSid() == 287931;
        assert info.getSn().equals("9524c");

    }
    @Test
    public void parseOpInversable()
    {
        String noTail = "MSwyODc5MzEsOTUyNGM=";
        OpInfo info = CBHistory.parseOp(noTail);
        String opText = CBHistory.calcOp(info);
        info = CBHistory.parseOp(opText);

        assert info.getPage() == 1;
        assert info.getSid() == 287931;
        assert info.getSn().equals("9524c");
    }

    @Test
    public void parseOpNormal()
    {
        // OpInfo(page=1, sid=287171, sn=fb52e)
        String opNomal = "MSwyODcxNzEsZmI1MmU%3DNAwIm%2FW1";
        OpInfo info = CBHistory.parseOp(opNomal);

        assert info.getPage() == 1;
        assert info.getSid() == 287171;
        assert info.getSn().equals("fb52e");
    }

    @Test
    public void testUpdateStrategy()
    {
        CommonProcess process=new CommonProcess() {};

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredDate = now.minus(Hours.hours(process.getArticleExpiredHours()));

        Article article = new Article();
        // 距离过期还有 60 分钟
        article.setDate(expiredDate.plus(Minutes.minutes(60)));

        // 因为没有 lastUpdate, 所以肯定是需要更新的
        assert process.isArticleNeedUpdate(article);

        // 这个也是会更新的,因为 距离过期还有60 分钟,会缩短这个更新间隔
        article.setLastUpdateDate(now.minus(Minutes.minutes(1+process.getArticleUpdateInterval()/2)));

        assert process.isArticleNeedUpdate(article);

        // 而这个是不会更新的,因为已经过期了不会考虑那个因子
        article.setDate(expiredDate.minusMinutes(1));

        assert !process.isArticleNeedUpdate(article);

        {
            // 距离过期还有 60 分钟
            article.setDate(expiredDate.plus(Minutes.minutes(60+1)));

            double interval = (double)process.getArticleUpdateInterval() / ((double)process.getArticleUpdateFactor()/60);
            interval -= 1;

            // 即便考虑这个因子,更新时间很接近 也不会更新的
            article.setLastUpdateDate(now.minus(Minutes.minutes((int)interval)));
            assert !process.isArticleNeedUpdate(article);

            // 刚好超过该时间 则会更新
            interval +=2;
            article.setLastUpdateDate(now.minus(Minutes.minutes((int)interval)));
            assert process.isArticleNeedUpdate(article);

        }

    }
}
