# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table users (
  email                     varchar(255) not null,
  name                      varchar(255),
  password                  varchar(255),
  address                   varchar(255),
  city                      varchar(255),
  state                     varchar(255),
  postal_code               varchar(255),
  constraint pk_users primary key (email))
;




# --- !Downs

drop table if exists users cascade;

