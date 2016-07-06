# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table items (
  id                            serial not null,
  created_by_email              varchar(255) not null,
  sold_by_email                 varchar(255),
  transaction_id                integer,
  sale_id                       integer,
  name                          varchar(255),
  description                   varchar(255),
  price                         float,
  minprice                      float,
  purchased                     boolean,
  sold_for                      float,
  constraint pk_items primary key (id)
);

create table sales (
  id                            serial not null,
  name                          varchar(255),
  start_date                    timestamp,
  end_date                      timestamp,
  users                         jsonb,
  constraint pk_sales primary key (id)
);

create table transactions (
  id                            serial not null,
  created_at                    timestamp,
  customer_name                 varchar(255),
  value                         decimal default '0.00',
  seller_email                  varchar(255),
  customer_email                varchar(255),
  sale_id                       integer,
  num_items                     integer,
  constraint pk_transactions primary key (id)
);

create table users (
  email                         varchar(255) not null,
  name                          varchar(255),
  password                      varchar(255),
  address                       varchar(255),
  city                          varchar(255),
  state                         varchar(255),
  postal_code                   varchar(255),
  is_super_user                 boolean,
  login_attempts                integer,
  constraint pk_users primary key (email)
);

alter table items add constraint fk_items_created_by_email foreign key (created_by_email) references users (email) on delete restrict on update restrict;
create index ix_items_created_by_email on items (created_by_email);

alter table items add constraint fk_items_sold_by_email foreign key (sold_by_email) references users (email) on delete restrict on update restrict;
create index ix_items_sold_by_email on items (sold_by_email);

alter table items add constraint fk_items_transaction_id foreign key (transaction_id) references transactions (id) on delete restrict on update restrict;
create index ix_items_transaction_id on items (transaction_id);

alter table items add constraint fk_items_sale_id foreign key (sale_id) references sales (id) on delete restrict on update restrict;
create index ix_items_sale_id on items (sale_id);

alter table transactions add constraint fk_transactions_seller_email foreign key (seller_email) references users (email) on delete restrict on update restrict;
create index ix_transactions_seller_email on transactions (seller_email);

alter table transactions add constraint fk_transactions_customer_email foreign key (customer_email) references users (email) on delete restrict on update restrict;
create index ix_transactions_customer_email on transactions (customer_email);

alter table transactions add constraint fk_transactions_sale_id foreign key (sale_id) references sales (id) on delete restrict on update restrict;
create index ix_transactions_sale_id on transactions (sale_id);


# --- !Downs

alter table if exists items drop constraint if exists fk_items_created_by_email;
drop index if exists ix_items_created_by_email;

alter table if exists items drop constraint if exists fk_items_sold_by_email;
drop index if exists ix_items_sold_by_email;

alter table if exists items drop constraint if exists fk_items_transaction_id;
drop index if exists ix_items_transaction_id;

alter table if exists items drop constraint if exists fk_items_sale_id;
drop index if exists ix_items_sale_id;

alter table if exists transactions drop constraint if exists fk_transactions_seller_email;
drop index if exists ix_transactions_seller_email;

alter table if exists transactions drop constraint if exists fk_transactions_customer_email;
drop index if exists ix_transactions_customer_email;

alter table if exists transactions drop constraint if exists fk_transactions_sale_id;
drop index if exists ix_transactions_sale_id;

drop table if exists items cascade;

drop table if exists sales cascade;

drop table if exists transactions cascade;

drop table if exists users cascade;

