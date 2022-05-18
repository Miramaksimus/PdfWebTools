BEGIN;

-- Database: postgres

-- DROP DATABASE IF EXISTS postgres;

-- CREATE DATABASE postgres
--     WITH
--     OWNER = postgres
--     ENCODING = 'UTF8'
--     LC_COLLATE = 'en_US.utf8'
--     LC_CTYPE = 'en_US.utf8'
--     TABLESPACE = pg_default
--     CONNECTION LIMIT = -1;
--
-- COMMENT ON DATABASE postgres
--     IS 'default administrative connection database';


-- SCHEMA: pdf_web_tools
-- DROP SCHEMA IF EXISTS pdf_web_tools ;

CREATE SCHEMA IF NOT EXISTS pdf_web_tools
    AUTHORIZATION postgres;

set schema 'pdf_web_tools';

DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS user_roles CASCADE;
DROP TABLE IF EXISTS folder CASCADE;
DROP TABLE IF EXISTS document CASCADE;
DROP TABLE IF EXISTS barcode CASCADE;
DROP TABLE IF EXISTS signature CASCADE;

CREATE TABLE users (
   id  SERIAL NOT NULL,
   username varchar(50) NOT NULL UNIQUE,
   name varchar(50) NOT NULL,
   surname varchar(50) NOT NULL,
   password varchar(100) NOT NULL,
--    register_date TIMESTAMP(0) WITHOUT TIME ZONE default (now() at time zone 'utc'),
   register_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   is_active boolean NOT NULL DEFAULT FALSE,
   PRIMARY KEY (id)
);

CREATE TABLE roles (
   id SERIAL NOT NULL,
   role varchar(50) NOT NULL,
   PRIMARY KEY (id)
);


CREATE TABLE user_roles (
    user_id bigint NOT NULL,
    role_id bigint NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

ALTER TABLE user_roles
    ADD CONSTRAINT user_fk FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;
ALTER TABLE user_roles
    ADD CONSTRAINT role_fk FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE;


CREATE TABLE folder (
    id SERIAL NOT NULL,
    name varchar(200) NOT NULL,
    parent_folder bigint,
    user_id bigint NOT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE folder
    ADD CONSTRAINT folder_user_id_fk FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;
ALTER TABLE folder
    ADD CONSTRAINT folder_parent_folder_fk FOREIGN KEY (parent_folder) REFERENCES folder (id) ON DELETE CASCADE;
ALTER TABLE  folder
    ADD CONSTRAINT unq_parent_user_name UNIQUE (name, parent_folder, user_id);

CREATE TABLE document (
    id SERIAL NOT NULL,
    title varchar(350) NOT NULL,
    mime_type varchar(200) NOT NULL,
    date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ecmid varchar(200) NOT NULL,
    folder_id bigint NOT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE document
    ADD CONSTRAINT document_parent_folder_fk FOREIGN KEY (folder_id) REFERENCES folder (id) ON DELETE CASCADE;
ALTER TABLE document
    ADD CONSTRAINT unq_title_mime_in_folder UNIQUE (title, mime_type, folder_id);

CREATE TABLE barcode (
    id SERIAL NOT NULL,
    text varchar(500) NOT NULL,
    document_id bigint NOT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE barcode
    ADD CONSTRAINT barcode_document_id_fk FOREIGN KEY (document_id) REFERENCES document (id) ON DELETE CASCADE;


CREATE TABLE signature (
     id SERIAL NOT NULL,
     signer_name varchar(100) NOT NULL,
     signer_nif varchar(50) NOT NULL,
     is_valid boolean NOT NULL,
     is_cover_whole boolean NOT NULL,
     sig_date TIMESTAMP NOT NULL,
     metadata varchar(2000) NOT NULL,
     document_id bigint NOT NULL,
     PRIMARY KEY (id)
);

ALTER TABLE signature
    ADD CONSTRAINT signature_document_id_fk FOREIGN KEY (document_id) REFERENCES document (id) ON DELETE CASCADE;


INSERT INTO roles (role) VALUES ('ADMIN');
INSERT INTO roles (role) VALUES ('USER');
-- user admin with password admin
INSERT INTO users (username, name, surname, password, is_active) VALUES ('admin','admin','admin', '$2a$04$6su6bxnQ./lLkwqQZ.tGcuCGnuNIwTLAJ5FGNddstz6FfGkXp4nm2', 'TRUE');
-- user admin has role admin
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);
INSERT INTO folder (name, user_id) VALUES ('Repository Root Folder', 1);

END;