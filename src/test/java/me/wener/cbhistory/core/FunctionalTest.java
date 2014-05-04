package me.wener.cbhistory.core;

import me.wener.cbhistory.domain.OpInfo;
import org.junit.Test;

public class FunctionalTest
{
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
}
