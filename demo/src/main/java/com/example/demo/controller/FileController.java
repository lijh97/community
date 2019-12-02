package com.example.demo.controller;


import com.example.demo.dto.FileDTO;
import com.example.demo.provider.QCloudProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
public class FileController {
    @Autowired
    private QCloudProvider qCloudProvider;

    @RequestMapping("/file/upload")
    @ResponseBody
    public FileDTO upload(HttpServletRequest request){
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest)request;
        MultipartFile file = multipartHttpServletRequest.getFile("editormd-image-file");
        try {
            String filename = qCloudProvider.upload(file.getInputStream(), file.getContentType(), file.getOriginalFilename(), file.getSize());
            FileDTO fileDTO = new FileDTO();
            fileDTO.setSuccess(1);
            fileDTO.setUrl(filename);
            return fileDTO;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
