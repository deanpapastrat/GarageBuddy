# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table items (
  id                        serial not null,
  created_by_email          varchar(255) not null,
  sold_by_email             varchar(255),
  sale_id                   integer,
  name                      varchar(255),
  description               varchar(255),
  price                     float,
  minprice                  float,
  purchased                 boolean,
  sold_for                  float,
  constraint pk_items primary key (id))
;

create table sales (
  sale_id                   serial not null,
  constraint pk_sales primary key (sale_id))
;

create table users (
  email                     varchar(255) not null,
  name                      varchar(255),
  password                  varchar(255),
  address                   varchar(255),
  city                      varchar(255),
  state                     varchar(255),
  postal_code               varchar(255),
  is_super_user             boolean,
  constraint pk_users primary key (email))
;

alter table items add constraint fk_items_createdBy_1 foreign key (created_by_email) references users (email);
create index ix_items_createdBy_1 on items (created_by_email);
alter table items add constraint fk_items_soldBy_2 foreign key (sold_by_email) references users (email);
create index ix_items_soldBy_2 on items (sold_by_email);
alter table items add constraint fk_items_sale_3 foreign key (sale_id) references sales (sale_id);
create index ix_items_sale_3 on items (sale_id);



# --- !Downs

drop table if exists items cascade;

drop table if exists sales cascade;

drop table if exists users cascade;

