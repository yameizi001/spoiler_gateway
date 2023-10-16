-- ----------------------------
-- spoiler_gateway_b_element
-- ----------------------------
DROP TABLE IF EXISTS "public"."spoiler_gateway_b_element";
CREATE TABLE "public"."spoiler_gateway_b_element"
(
    "id"          int8                                        NOT NULL,
    "name"        varchar(64) COLLATE "pg_catalog"."default"  NOT NULL,
    "alias"       varchar(64) COLLATE "pg_catalog"."default"  NOT NULL,
    "icon"        varchar(256) COLLATE "pg_catalog"."default" NOT NULL,
    "description" varchar(256) COLLATE "pg_catalog"."default" NOT NULL,
    "ordered"     int4,
    "type"        int2                                        NOT NULL
)
;

-- ----------------------------
-- spoiler_gateway_b_file
-- ----------------------------
DROP TABLE IF EXISTS "public"."spoiler_gateway_b_file";
CREATE TABLE "public"."spoiler_gateway_b_file"
(
    "id"    int8                                        NOT NULL,
    "name"  varchar(128) COLLATE "pg_catalog"."default" NOT NULL,
    "path"  varchar(256) COLLATE "pg_catalog"."default" NOT NULL,
    "bytes" bytea                                       NOT NULL
)
;

-- ----------------------------
-- spoiler_gateway_b_instance
-- ----------------------------
DROP TABLE IF EXISTS "public"."spoiler_gateway_b_instance";
CREATE TABLE "public"."spoiler_gateway_b_instance"
(
    "id"          int8                                       NOT NULL,
    "service_id"  int8                                       NOT NULL,
    "name"        varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
    "scheme"      varchar(16) COLLATE "pg_catalog"."default",
    "secure"      bool                                       NOT NULL,
    "host"        varchar(15) COLLATE "pg_catalog"."default" NOT NULL,
    "port"        int4                                       NOT NULL,
    "health"      bool,
    "weight"      int4                                       NOT NULL,
    "metadata"    jsonb,
    "create_time" timestamp(6)                               NOT NULL,
    "update_time" timestamp(6)                               NOT NULL,
    "enabled"     bool                                       NOT NULL
)
;

-- ----------------------------
-- spoiler_gateway_b_property
-- ----------------------------
DROP TABLE IF EXISTS "public"."spoiler_gateway_b_property";
CREATE TABLE "public"."spoiler_gateway_b_property"
(
    "id"          int8                                        NOT NULL,
    "element_id"  int8                                        NOT NULL,
    "alias"       varchar(64) COLLATE "pg_catalog"."default"  NOT NULL,
    "description" varchar(256) COLLATE "pg_catalog"."default" NOT NULL,
    "key"         varchar(64) COLLATE "pg_catalog"."default",
    "required"    bool                                        NOT NULL,
    "regex"       varchar(128) COLLATE "pg_catalog"."default"
)
;

-- ----------------------------
-- spoiler_gateway_b_route
-- ----------------------------
DROP TABLE IF EXISTS "public"."spoiler_gateway_b_route";
CREATE TABLE "public"."spoiler_gateway_b_route"
(
    "id"          int8                                       NOT NULL,
    "service_id"  int8                                       NOT NULL,
    "template_id" int8                                       NOT NULL,
    "name"        varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
    "description" varchar(128) COLLATE "pg_catalog"."default",
    "predicates"  jsonb,
    "filters"     jsonb,
    "ordered"     int4                                       NOT NULL,
    "metadata"    jsonb,
    "create_time" timestamp(6)                               NOT NULL,
    "update_time" timestamp(6)                               NOT NULL,
    "enabled"     bool                                       NOT NULL
)
;

-- ----------------------------
-- spoiler_gateway_b_service
-- ----------------------------
DROP TABLE IF EXISTS "public"."spoiler_gateway_b_service";
CREATE TABLE "public"."spoiler_gateway_b_service"
(
    "id"          int8                                       NOT NULL,
    "name"        varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
    "description" varchar(128) COLLATE "pg_catalog"."default",
    "metadata"    jsonb,
    "create_time" timestamp(6)                               NOT NULL,
    "update_time" timestamp(6)                               NOT NULL,
    "enabled"     bool                                       NOT NULL
)
;

-- ----------------------------
-- spoiler_gateway_b_template
-- ----------------------------
DROP TABLE IF EXISTS "public"."spoiler_gateway_b_template";
CREATE TABLE "public"."spoiler_gateway_b_template"
(
    "id"          int8                                       NOT NULL,
    "name"        varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
    "description" varchar(256) COLLATE "pg_catalog"."default",
    "type"        int2                                       NOT NULL,
    "create_time" timestamp(6)                               NOT NULL,
    "update_time" timestamp(6)                               NOT NULL
)
;

-- ----------------------------
-- spoiler_gateway_r_template_element
-- ----------------------------
DROP TABLE IF EXISTS "public"."spoiler_gateway_r_template_element";
CREATE TABLE "public"."spoiler_gateway_r_template_element"
(
    "template_id" int8 NOT NULL,
    "element_id"  int8 NOT NULL
)
;

-- ----------------------------
-- spoiler_gateway_r_template_element_property
-- ----------------------------
DROP TABLE IF EXISTS "public"."spoiler_gateway_r_template_element_property";
CREATE TABLE "public"."spoiler_gateway_r_template_element_property"
(
    "template_id" int8  NOT NULL,
    "element_id"  int8  NOT NULL,
    "property_id" int8  NOT NULL,
    "values"      jsonb NOT NULL
)
;

-- ----------------------------
-- spoiler_gateway_r_template_property
-- ----------------------------
DROP TABLE IF EXISTS "public"."spoiler_gateway_r_template_property";
CREATE TABLE "public"."spoiler_gateway_r_template_property"
(
    "template_id" int8 NOT NULL,
    "property_id" int8 NOT NULL,
    "values"      jsonb
)
;

-- ----------------------------
-- Indexes for table spoiler_gateway_b_element
-- ----------------------------
CREATE UNIQUE INDEX "spoiler_gateway_b_element_alias_idx" ON "public"."spoiler_gateway_b_element" USING btree (
    "alias" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
CREATE UNIQUE INDEX "spoiler_gateway_b_element_name_idx" ON "public"."spoiler_gateway_b_element" USING btree (
    "name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Primary Key for table spoiler_gateway_b_element
-- ----------------------------
ALTER TABLE "public"."spoiler_gateway_b_element"
    ADD CONSTRAINT "gateway_spoiler_b_element_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes for table spoiler_gateway_b_file
-- ----------------------------
CREATE UNIQUE INDEX "spoiler_gateway_b_file_name_idx" ON "public"."spoiler_gateway_b_file" USING btree (
    "name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
CREATE UNIQUE INDEX "spoiler_gateway_b_file_path_idx" ON "public"."spoiler_gateway_b_file" USING btree (
    "path" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Primary Key for table spoiler_gateway_b_file
-- ----------------------------
ALTER TABLE "public"."spoiler_gateway_b_file"
    ADD CONSTRAINT "gateway_spoiler_b_file_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes for table spoiler_gateway_b_instance
-- ----------------------------
CREATE INDEX "spoiler_gateway_b_instance_host_idx" ON "public"."spoiler_gateway_b_instance" USING btree (
    "host" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
CREATE UNIQUE INDEX "spoiler_gateway_b_instance_name_idx" ON "public"."spoiler_gateway_b_instance" USING btree (
    "name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
CREATE INDEX "spoiler_gateway_b_instance_service_id_idx" ON "public"."spoiler_gateway_b_instance" USING btree (
    "service_id" "pg_catalog"."int8_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Uniques for table spoiler_gateway_b_instance
-- ----------------------------
ALTER TABLE "public"."spoiler_gateway_b_instance"
    ADD CONSTRAINT "spoiler_gateway_b_instance_name_unique_constraint" UNIQUE ("name");

-- ----------------------------
-- Primary Key for table spoiler_gateway_b_instance
-- ----------------------------
ALTER TABLE "public"."spoiler_gateway_b_instance"
    ADD CONSTRAINT "spoiler_gateway_b_instance_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes for table spoiler_gateway_b_property
-- ----------------------------
CREATE UNIQUE INDEX "spoiler_gateway_b_property_alias_idx" ON "public"."spoiler_gateway_b_property" USING btree (
    "alias" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
CREATE INDEX "spoiler_gateway_b_property_element_id_idx" ON "public"."spoiler_gateway_b_property" USING btree (
    "element_id" "pg_catalog"."int8_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Primary Key for table spoiler_gateway_b_property
-- ----------------------------
ALTER TABLE "public"."spoiler_gateway_b_property"
    ADD CONSTRAINT "gateway_spoiler_b_property_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes for table spoiler_gateway_b_route
-- ----------------------------
CREATE UNIQUE INDEX "spoiler_gateway_b_route_name_idx" ON "public"."spoiler_gateway_b_route" USING btree (
    "name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
CREATE INDEX "spoiler_gateway_b_route_service_id_idx" ON "public"."spoiler_gateway_b_route" USING btree (
    "service_id" "pg_catalog"."int8_ops" ASC NULLS LAST
    );
CREATE INDEX "spoiler_gateway_b_route_template_id_idx" ON "public"."spoiler_gateway_b_route" USING btree (
    "template_id" "pg_catalog"."int8_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Uniques for table spoiler_gateway_b_route
-- ----------------------------
ALTER TABLE "public"."spoiler_gateway_b_route"
    ADD CONSTRAINT "spoiler_gateway_b_route_name_unique_constraint" UNIQUE ("name");

-- ----------------------------
-- Primary Key for table spoiler_gateway_b_route
-- ----------------------------
ALTER TABLE "public"."spoiler_gateway_b_route"
    ADD CONSTRAINT "spoiler_gateway_b_route_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes for table spoiler_gateway_b_service
-- ----------------------------
CREATE UNIQUE INDEX "spoiler_gateway_b_service_name_idx" ON "public"."spoiler_gateway_b_service" USING btree (
    "name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Uniques for table spoiler_gateway_b_service
-- ----------------------------
ALTER TABLE "public"."spoiler_gateway_b_service"
    ADD CONSTRAINT "gateway_spoiler_b_service_name_unique_constraint" UNIQUE ("name");

-- ----------------------------
-- Primary Key for table spoiler_gateway_b_service
-- ----------------------------
ALTER TABLE "public"."spoiler_gateway_b_service"
    ADD CONSTRAINT "gateway_spoiler_service_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes for table spoiler_gateway_b_template
-- ----------------------------
CREATE UNIQUE INDEX "spoiler_gateway_b_template_name_idx" ON "public"."spoiler_gateway_b_template" USING btree (
    "name" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Primary Key for table spoiler_gateway_b_template
-- ----------------------------
ALTER TABLE "public"."spoiler_gateway_b_template"
    ADD CONSTRAINT "gateway_spoiler_b_template_pkey" PRIMARY KEY ("id");

-- ----------------------------
-- Indexes for table spoiler_gateway_r_template_element
-- ----------------------------
CREATE INDEX "spoiler_gateway_r_template_element_element_id_idx" ON "public"."spoiler_gateway_r_template_element" USING btree (
    "element_id" "pg_catalog"."int8_ops" ASC NULLS LAST
    );
CREATE INDEX "spoiler_gateway_r_template_element_template_id_idx" ON "public"."spoiler_gateway_r_template_element" USING btree (
    "template_id" "pg_catalog"."int8_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Indexes for table spoiler_gateway_r_template_element_property
-- ----------------------------
CREATE INDEX "spoiler_gateway_r_template_element_property_element_id_idx" ON "public"."spoiler_gateway_r_template_element_property" USING btree (
    "element_id" "pg_catalog"."int8_ops" ASC NULLS LAST
    );
CREATE INDEX "spoiler_gateway_r_template_element_property_property_id_idx" ON "public"."spoiler_gateway_r_template_element_property" USING btree (
    "property_id" "pg_catalog"."int8_ops" ASC NULLS LAST
    );
CREATE INDEX "spoiler_gateway_r_template_element_property_template_id_idx" ON "public"."spoiler_gateway_r_template_element_property" USING btree (
    "template_id" "pg_catalog"."int8_ops" ASC NULLS LAST
    );

-- ----------------------------
-- Indexes for table spoiler_gateway_r_template_property
-- ----------------------------
CREATE INDEX "spoiler_gateway_r_template_property_property_id_idx" ON "public"."spoiler_gateway_r_template_property" USING btree (
    "property_id" "pg_catalog"."int8_ops" ASC NULLS LAST
    );
CREATE INDEX "spoiler_gateway_r_template_property_template_id_idx" ON "public"."spoiler_gateway_r_template_property" USING btree (
    "template_id" "pg_catalog"."int8_ops" ASC NULLS LAST
    );
