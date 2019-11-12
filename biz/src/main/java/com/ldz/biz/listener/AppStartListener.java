package com.ldz.biz.listener;

import com.ldz.sys.service.GnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Decoder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class AppStartListener implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private GnService gnService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event)  {
        //只在初始化“根上下文”的时候执行
        final ApplicationContext app = event.getApplicationContext();
        if (null == app.getParent()) { // 当存在父子容器时，此判断很有用
            gnService.initPermission();
        }
    }

    public static void main(String[] args) throws IOException {
        FileOutputStream f = new FileOutputStream("D:/a.jpg");
        String base = "iVBORw0KGgoAAAANSUhEUgAAAQAAAAEAAQMAAABmvDolAAAABlBMVEX///8AAABVwtN+AAABbUlEQVR42uyYTW7EIAyFzcrH4KYBbsoxWPEq22TaTDObqos8iacR0sC3Qf57Qba2tra2/kMFAPohuUqeIqL2f7IBh212X1FFDu1rkwtQTO2iPQFVUbUXdFLg8KvZETOQ0NOIv5yAp1yCh0xy/ZCTDwdW+evr96E/PBv4iaW7IwrgUDRY+4pMO7SnkScbkAAMWQPF4XlNOQrAjhTT1war/ZgsZIBFJ1LOx6JIGm9jkQAodtrLWP6kWvi60AFA85RrQ9KIVpwnG5BGXmEyo2K5166tjAJ4JV4ZHiyLV658QHa7uPY99wiBcCaScVZQvTYxCmA5k7MVyK/ypwDELpibT/bVCpQPSH5aRvSBHo6LDvguf+B12ckGxL0iTGYU1WuHDVjPC+ouMdbrWOQAzucFUXgfi69CSsCtr316wOL11oppACt/hEWR2werpwPxvDByW1Ujt4XzcOB8XsB0rwibjHzA1tbW1taf9RUAAP//NlX9q+5fIqIAAAAASUVORK5CYII=";
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] bytes = decoder.decodeBuffer(base);
        f.write(bytes);
        f.flush();
        f.close();
    }

}