package me.wener.cbhistory.core;


import me.wener.cbhistory.domain.Article;
import me.wener.cbhistory.domain.OpInfo;
import me.wener.cbhistory.domain.RawComment;
import me.wener.cbhistory.util.CodecUtils;
import static com.google.common.base.Preconditions.*;
public class CBHistory
{
    public static RawComment getCommentOf(int id)
    {

        return null;
    }

    public static String calcOp(Article detail)
    {
        return calcOp(1,detail.getSid(), detail.getSn(), 8);
    }
    public static String calcOp(String page, String sid, String sn, int n)
    {
        checkNotNull(page);
        checkNotNull(sid);
        return calcOp(Integer.parseInt(page), Integer.parseInt(sid), sn, n);
    }
    public static String calcOp(Integer page, Integer sid, String sn, int n)
    {
        return calcOp(page,(long)((int)sid),sn,n);
    }
    public static String calcOp(Integer page, Long sid, String sn, int n)
    {
        checkNotNull(page);
        checkNotNull(sid);
        checkNotNull(sn);

        final String b64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
        final char[] b64Chars = b64.toCharArray();
        String op = "%s,%s,%s";
        op = String.format(op, page, sid, sn);
        op = CodecUtils.encodeBase64(op);

        {
            String extra = "";
            for (int i = 0; i < n; i++)
                extra += b64Chars[((int) Math.floor(Math.random() * b64Chars.length))];
            op += extra;
        }

        return CodecUtils.encodeURIComponent(op);
    }

    public static OpInfo parseOp(String op)
    {
        OpInfo info = new OpInfo();
        op = CodecUtils.decodeURIComponent(op);
        op = op.substring(0, op.indexOf("=")+1);
        op = CodecUtils.decodeBase64(op);
        String[] parts = op.split(",");

        info.setPage(Integer.parseInt(parts[0]));
        info.setSid(Long.parseLong(parts[1]));
        info.setSn(parts[2]);

        return info;
    }

    public static String calcOp(OpInfo info)
    {
        return calcOp(info.getPage(), info.getSid(), info.getSn(), 8);
    }
}
