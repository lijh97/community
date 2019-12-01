package com.example.demo.cache;

import com.example.demo.dto.TagDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TagCache {
    public static List<TagDTO> get() {
        ArrayList<TagDTO> tagDTOS = new ArrayList<>();
        TagDTO programme = new TagDTO();
        programme.setCategoryName("开发语言");
        programme.setTags(Arrays.asList("js", "php", "css", "html", "node", "java", "node", "python"));
        tagDTOS.add(programme);

        TagDTO framework = new TagDTO();
        framework.setCategoryName("平台框架");
        framework.setTags(Arrays.asList("spring", "struts", "mybatis", "hibernate", "spring boot", "spring cloud"));
        tagDTOS.add(framework);

        TagDTO server = new TagDTO();
        server.setCategoryName("服务器");
        server.setTags(Arrays.asList("linux","docker","ubuntu","nginx","tomcat"));
        tagDTOS.add(server);

        TagDTO db = new TagDTO();
        db.setCategoryName("数据库");
        db.setTags(Arrays.asList("mysql","Access","redis","mongodb","oracle"));
        tagDTOS.add(db);


        return tagDTOS;
    }
}
