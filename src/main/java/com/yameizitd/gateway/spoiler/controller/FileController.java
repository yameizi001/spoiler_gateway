package com.yameizitd.gateway.spoiler.controller;

import com.yameizitd.gateway.spoiler.domain.entity.FileEntity;
import com.yameizitd.gateway.spoiler.domain.view.GenericResp;
import com.yameizitd.gateway.spoiler.exception.impl.EntryNotExistException;
import com.yameizitd.gateway.spoiler.mapper.FileMapper;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Optional;

@Controller
@RequestMapping("/gateway-backend")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class FileController {
    private final FileMapper fileMapper;

    public FileController(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    @ResponseBody
    @PostMapping("/file/upload")
    public Mono<GenericResp<String>> upload(@RequestPart("file") Mono<FilePart> filePart) {
        return filePart
                .publishOn(Schedulers.boundedElastic())
                .flatMap(FileEntity::fromFilePart)
                .map(record -> {
                    fileMapper.insert(record);
                    return record.getPath();
                })
                .map(GenericResp::ok);
    }

    @GetMapping("/file/{dir}/{id}.{suffix}")
    public Mono<Void> download(@PathVariable("dir") String dir,
                               @PathVariable("id") Long id,
                               @PathVariable("suffix") String suffix,
                               ServerHttpResponse response) {
        return Mono.just(id)
                .publishOn(Schedulers.boundedElastic())
                .map(fileId -> Optional.ofNullable(fileMapper.selectById(fileId)))
                .flatMap(record -> {
                    if (record.isEmpty() || !dir.equals(suffix) || !record.get().getName().endsWith(suffix)) {
                        return Mono.error(new EntryNotExistException("File not found"));
                    }
                    response.setStatusCode(HttpStatus.OK);
                    byte[] bytes = record.get().getBytes();
                    DataBuffer wrap = response.bufferFactory().wrap(bytes);
                    return response.writeWith(Mono.just(wrap));
                });
    }
}
