package me.study.mylog.common.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class LocalFileProperties {
    public static final String UPLOAD_PATH = "C:\\myLogUpload\\images\\";
}
