package com.yameizitd.gateway.spoiler.domain.po;

import com.yameizitd.gateway.spoiler.domain.FileConstants;
import com.yameizitd.gateway.spoiler.exception.impl.FileInvalidException;
import com.yameizitd.gateway.spoiler.util.IdUtils;
import lombok.*;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class FilePO implements Serializable {
    @Serial
    private static final long serialVersionUID = -6223316002239050164L;

    private Long id;
    private String name;
    private String path;
    private byte[] bytes;

    public static Mono<FilePO> fromFilePart(FilePart filePart) {
        if (filePart == null) {
            throw new FileInvalidException("File should not be empty");
        }
        String filename = filePart.filename();
        if (!StringUtils.hasLength(filename)) {
            throw new FileInvalidException("Filename should not be null");
        }
        String[] filenameOptions = filename.split("\\.");
        if (filenameOptions.length <= 1) {
            throw new FileInvalidException("The file must have a format suffix");
        }
        String suffix = filenameOptions[filenameOptions.length - 1];
        FilePO filePO = new FilePO();
        long id = IdUtils.nextSnowflakeId();
        filePO.setId(id);
        filePO.setName(filename);
        filePO.setPath(FileConstants.FILE_BASE_PATH + "/" + suffix + "/" + id + "." + suffix);
        return DataBufferUtils.join(filePart.content())
                // asByteBuffer() is deprecated
                .map(dataBuffer -> dataBuffer.asByteBuffer().array())
                .map(bytes -> {
                    filePO.setBytes(bytes);
                    return filePO;
                });
    }
}
