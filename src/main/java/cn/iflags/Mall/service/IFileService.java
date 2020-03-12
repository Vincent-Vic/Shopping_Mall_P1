package cn.iflags.Mall.service;

import cn.iflags.Mall.common.ServerResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 描述:
 *
 * @author Vincent Vic
 * create 2020-02-17 20:30
 */

public interface IFileService {

    ServerResponse upload(MultipartFile file, String path, String suffix);
}
