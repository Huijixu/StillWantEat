package com.huijixu.controller;
/**
 * @author HuiJiXu
 * @address QingDao China
 */

import com.huijixu.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@SuppressWarnings({"all"})
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${stillwanteat.uploadPath}")
    private String uploadPath;


    /**
     *下载方法
     */
    @GetMapping("/download")
    public void download(
            String name,
            HttpServletResponse response
    ){
        //本地存储 ---》 后端 输入流
        try {
            FileInputStream fis = new FileInputStream(new File(uploadPath + name));

            //后端 ---》 客户端
            ServletOutputStream os = response.getOutputStream();

            //设置后端相应给浏览器的资源类型
            response.setContentType("image/jpeg");

            int len =  0 ;
            byte[] bytes = new byte[2048*2];
            while ((len = fis.read(bytes)) != -1){
                os.write(bytes,0,len);
            }

            fis.close();
            os.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }



    /**
     * 文件上传
     *
     * @param file file文件为临时文件地址
     *             这个参数名是EUI在前端动态生成的(  <input type="file" name="file" class="el-upload__input">)
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        log.info(file.toString());

        //新建目录对象
        File dir = new File(uploadPath);
        if (!dir.exists()){
            //不存在目录则创建
            dir.mkdirs();
        }
        //获取新文件名
        String fileName = UUID.randomUUID().toString();
        //获取源文件的后缀名
        String OriginalName = file.getOriginalFilename();
        String suffix = OriginalName.substring(OriginalName.lastIndexOf("."));
        //组建新文件名
        fileName = fileName + suffix;
        //临时文件转存 (路径名 + 文件全名)
        try {
            file.transferTo(new File(uploadPath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //返回文件名称给前端的表中，在把fileName和菜品信息一起存到数据库中
        return R.success(fileName);
    }
}
