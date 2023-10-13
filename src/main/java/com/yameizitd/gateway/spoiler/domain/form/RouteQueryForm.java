package com.yameizitd.gateway.spoiler.domain.form;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class RouteQueryForm implements Serializable {
    @Serial
    private static final long serialVersionUID = 1908541554964345841L;

    private Long id;
    private Long serviceId;
    private Long templateId;
    private String name;
    private List<QueryItem> predicateQueries;
    private List<QueryItem> filterQueries;
    private List<QueryItem> metadataQueries;
    private Boolean enabled;
    private Page page;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class QueryItem implements Serializable {
        @Serial
        private static final long serialVersionUID = -5053037622456215201L;

        private String name;
        private String args;
    }
}
